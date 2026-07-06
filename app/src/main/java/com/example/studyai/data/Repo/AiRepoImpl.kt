package com.example.studyai.data.Repo

import android.util.Log
import com.example.studyai.data.remote.Constants
import com.example.studyai.data.remote.GeminiApi
import com.example.studyai.data.remote.dto.Content
import com.example.studyai.data.remote.dto.GeminiRequest
import com.example.studyai.data.remote.dto.Parts
import com.example.studyai.domain.Repo.AiRepository
import javax.inject.Inject

class AiRepoImpl @Inject constructor(private val provideGeminiApi: GeminiApi) : AiRepository {
    override suspend fun generateSummary(text: String): String {
        val prompt = """
            You are an expert study assistant.
            
            If the input is a topic, explain it and create study notes.
            
            If the input is detailed notes, summarize them.
            
            Use this format:
            
            📌 Key Concepts
            📖 Important Details
            🎯 Exam Points
            
            Input:
            $text
            """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Parts(prompt)
                    )
                )
            )
        )

        return executeRequest(request)
    }

    override suspend fun generateFlashcards(text: String): String {
        val prompt = """
            You are an expert study assistant.

            Generate 10 study flashcards.

            Format exactly like:

            Q: Question
            A: Answer

            Rules:
            - Keep answers concise.
            - Focus on important concepts.
            - Return only flashcards.
            - Do not add introductions.

            Notes: $text """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Parts(prompt)
                    )
                )
            )
        )
        return executeRequest(request)
    }


    private suspend fun executeRequest(request: GeminiRequest) : String{
        for((index , apikey) in Constants.API_KEY.withIndex()){
            try {
                Log.d("Using Api key " , "${index + 1}")
                val response = provideGeminiApi.generateSummary(apikey , request)
                return response.candidates
                    .first()
                    .content
                    .parts
                    .first()
                    .text
            }catch (e : retrofit2.HttpException){
                if (e.code() == 429){
                    continue
                }
                Log.d("Switching to new" , "${index + 1}")
                throw  e
            }catch (e : Exception){
                throw e
            }
        }

        return "⚠\uFE0F All AI API keys have reached their daily limit."
    }
}