package com.example.studyai.domain.Repo

interface AiRepository {
    suspend fun generateSummary(text : String) : String
    suspend fun generateFlashcards(text : String) : String
}

