package com.example.studyai.domain.SummaryUseCase

import com.example.studyai.domain.Repo.AiRepository
import javax.inject.Inject

class GenerateFlashCardUseCase @Inject constructor(val repository: AiRepository) {
    suspend operator fun invoke(text : String) : String{
        return repository.generateFlashcards(text)
    }
}

