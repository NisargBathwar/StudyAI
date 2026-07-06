package com.example.studyai.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

object PdfUtils {
    fun extractTextFromPdf(
        context: Context ,
        uri: Uri
    ) : String{
       return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val document = PDDocument.load(inputStream)
                val stripper = PDFTextStripper()
                val endPage = minOf(document.numberOfPages , 30)
                stripper.startPage = 1
                stripper.endPage = endPage
                val text = stripper.getText(document)
                document.close()
                text
            } ?: ""
       }catch (e : Exception){
           Log.e("PDF" , "Error reading pdf" , e)
           ""
       }
    }
}