package com.example.studyai

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.studyai.data.Repo.AiRepoImpl
import com.example.studyai.data.remote.RetrofitInstance
import com.example.studyai.domain.SummaryUseCase.GenerateFlashCardUseCase
import com.example.studyai.domain.SummaryUseCase.GenerateSummaryUseCase
import com.example.studyai.presentation.AiUi
import com.example.studyai.presentation.StudyViewModel
import com.example.studyai.ui.theme.StudyAITheme
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        PDFBoxResourceLoader.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            StudyAITheme{
                Surface(Modifier.fillMaxSize() , color = MaterialTheme.colorScheme.background) {
                    AiUi()
                }
            }
        }
    }
}



