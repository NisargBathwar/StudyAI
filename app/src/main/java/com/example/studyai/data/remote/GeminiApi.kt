package com.example.studyai.data.remote

import com.example.studyai.data.remote.dto.GeminiRequest
import com.example.studyai.data.remote.dto.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApi {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateSummary(
        @Query("key") apikey : String,
        @Body request: GeminiRequest
    ) : GeminiResponse
}

