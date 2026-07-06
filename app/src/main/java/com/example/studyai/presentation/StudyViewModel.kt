package com.example.studyai.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyai.data.ocr.OcrManager
import com.example.studyai.domain.SummaryUseCase.GenerateFlashCardUseCase
import com.example.studyai.domain.SummaryUseCase.GenerateSummaryUseCase
import com.example.studyai.utils.PdfUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(private val generateSummaryUseCases: GenerateSummaryUseCase , private val flashCardUseCase: GenerateFlashCardUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyUiState())
    val uiState : StateFlow<StudyUiState> =_uiState.asStateFlow()


    fun onAction(actions: StudyActions){
        when(actions){
            is StudyActions.UpdateInput -> {
                _uiState.value = _uiState.value.copy(
                    inputText = actions.text ,
                )
            }
            is StudyActions.GenerateSummary -> {
                Log.e("GENERATE", "Button Clicked")
                viewModelScope.launch {
                   try {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true
                        )

                       val textToSummarize = when{
                           _uiState.value.pdfText.isNotBlank() -> _uiState.value.pdfText
                           _uiState.value.ocrText.isNotBlank() -> _uiState.value.ocrText
                           else -> ""
                       }

                       val text = if (textToSummarize.isNotBlank()){
                           """
                               Study Material: $textToSummarize 
                               Additional Instructions : ${_uiState.value.inputText}
                           """.trimIndent()
                       }else{
                           _uiState.value.inputText
                       }

                        val summary = generateSummaryUseCases.invoke(text)

                        _uiState.value = _uiState.value.copy(
                            summary = summary,
                            flashcards = emptyList(),
                            isLoading = false,
                            error = null 
                        )
                   }catch (e : Exception){
                       Log.d("GEMINI_ERROR" , e.stackTraceToString())
                       _uiState.value = _uiState.value.copy(
                           isLoading = false ,
                           error = e.message
                       )
                   }
                }
            }

            is StudyActions.LoadPdf -> {

                viewModelScope.launch(Dispatchers.IO){
                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                    val text = PdfUtils.extractTextFromPdf(actions.context , actions.uri).take(3000)

                    _uiState.value = _uiState.value.copy(
                        pdfText = text ,
                        pdfLoaded = true,
                        ocrLoaded = false,
                        isLoading = false ,
                    )
                }
            }

            is StudyActions.GenerateFlashCard -> {
                viewModelScope.launch {
                    try {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true
                        )

                        val req  = when{
                            _uiState.value.pdfText.isNotBlank() -> _uiState.value.pdfText
                            _uiState.value.ocrText.isNotBlank() -> _uiState.value.ocrText
                            else -> ""
                        }

                        val text = if (req.isNotBlank()){
                            """
                                Study Material : $req 
                                Additional Instructions : ${_uiState.value.inputText}
                            """.trimIndent()
                        }else{
                            _uiState.value.inputText
                        }

                        val flash = flashCardUseCase.invoke(text)
                        val cards = parseFlashCards(flash)

                        if (cards.isEmpty()){
                            _uiState.value = _uiState.value.copy(
                                isLoading = false ,
                                error = flash
                            )
                            return@launch
                        }

                        _uiState.value = _uiState.value.copy(
                            flashcards = cards ,
                            summary = "",
                            isLoading = false ,
                            error = null
                        )
                    }catch (e : Exception){
                        Log.e("flashCard" , e.message.toString())
                        _uiState.value = _uiState.value.copy(
                            isLoading = false ,
                            error = e.message
                        )
                    }
                }
            }

            is StudyActions.LoadOcrText -> {
                viewModelScope.launch{

                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )

                    val text = OcrManager().extractText(actions.bitmap)

                    _uiState.value = _uiState.value.copy(
                        ocrText = text,
                        ocrLoaded = true ,
                        isLoading = false ,
                        pdfLoaded = false ,
                    )
                }
            }

            is StudyActions.ClearOcr -> {
                _uiState.value = _uiState.value.copy(
                    ocrText = "" ,
                    ocrLoaded = false
                )
            }

            is StudyActions.ClearPdf -> {
                _uiState.value = _uiState.value.copy(
                    pdfText = "" ,
                    pdfLoaded = false
                )
            }
        }
    }


    private fun parseFlashCards(response : String) : List<FlashCard>{
        val cards = mutableListOf<FlashCard>()
        val regex = Regex(
            "Q:\\s*(.*?)\\nA:\\s*(.*?)(?=\\nQ:|$)" ,
            RegexOption.DOT_MATCHES_ALL
        )

        regex.findAll(response).forEach { match->
            cards.add(
                FlashCard(
                    questions = match.groupValues[1].trim() ,
                    answer = match.groupValues[2].trim()
                )
            )
        }
        return cards
    }
}