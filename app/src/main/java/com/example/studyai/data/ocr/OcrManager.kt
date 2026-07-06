package com.example.studyai.data.ocr

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class OcrManager {
    suspend fun extractText(bitmap: Bitmap) : String{
        return suspendCancellableCoroutine { continuation ->

            val image = InputImage.fromBitmap(bitmap , 0)

            val recognizer = TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )

            recognizer.process(image)
                .addOnSuccessListener { Text->
                    continuation.resume(Text.text)
                }
                .addOnFailureListener {
                    continuation.resume("")
                }
        }
    }
}