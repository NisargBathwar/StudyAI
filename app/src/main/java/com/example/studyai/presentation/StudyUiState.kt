package com.example.studyai.presentation

data class StudyUiState(
    val inputText : String = "",
    val pdfText : String = "",
    val pdfLoaded : Boolean = false,
    val ocrText : String = "" ,
    val ocrLoaded : Boolean = false ,
    val summary : String = "",
    val flashcards : List<FlashCard> = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null
)

data class FlashCard(
    val questions : String ,
    val answer : String
)
