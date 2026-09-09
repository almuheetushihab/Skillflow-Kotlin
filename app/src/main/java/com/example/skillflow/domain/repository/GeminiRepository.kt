package com.example.skillflow.domain.repository

import kotlinx.coroutines.flow.Flow

interface GeminiRepository {
    /**
     * Sends a user question to Gemini along with the nugget context.
     * Returns a Flow emitting Result containing the response text or failure.
     */
    fun askGemini(context: String, userQuestion: String): Flow<Result<String>>
}
