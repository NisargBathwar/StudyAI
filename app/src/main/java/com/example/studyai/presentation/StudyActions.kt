package com.example.studyai.presentation

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

sealed class StudyActions {
    data class UpdateInput(val text : String) : StudyActions()
    data class LoadPdf(val context: Context, val uri: Uri) : StudyActions()
    data class LoadOcrText(val bitmap: Bitmap) : StudyActions()
    object GenerateFlashCard : StudyActions()
    object GenerateSummary : StudyActions()
    data object ClearPdf : StudyActions()
    data object ClearOcr : StudyActions()
}

