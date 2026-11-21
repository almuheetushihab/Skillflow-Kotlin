package com.example.skillflow.domain.repository

import android.graphics.Bitmap
import com.example.skillflow.domain.model.KnowledgeNugget
import kotlinx.coroutines.flow.Flow

interface GeminiRepository {
    /**
     * Sends a user question to Gemini along with the nugget context.
     * Returns a Flow emitting Result containing the response text or failure.
     */
    fun askGemini(context: String, userQuestion: String): Flow<Result<String>>

    /**
     * Translates title and content into natural, clear Bengali (Bangla).
     * Returns a Flow emitting Result containing Pair(translatedTitle, translatedContent).
     */
    fun translateToBangla(title: String, content: String): Flow<Result<Pair<String, String>>>

    /**
     * Analyzes a lecture sheet photo using Gemini Vision API.
     * Extracts structured content (Classwork, Homework, Phonetics, Grammar, Vocabularies, Quizzes).
     */
    fun analyzeLectureSheetImage(imageBitmap: Bitmap, targetCourseId: String): Flow<Result<KnowledgeNugget>>
}
