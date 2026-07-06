package com.example.studyai.data.remote.dto

data class GeminiRequest(
    val contents : List<Content>
)


data class Content(
    val parts : List<Parts>
)


data class Parts(
    val text : String
)
