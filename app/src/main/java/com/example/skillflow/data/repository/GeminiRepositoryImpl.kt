package com.example.skillflow.data.repository

import com.example.skillflow.BuildConfig
import com.example.skillflow.domain.repository.GeminiRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepositoryImpl @Inject constructor() : GeminiRepository {

    override fun askGemini(context: String, userQuestion: String): Flow<Result<String>> = flow {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim().removeSurrounding("\"").removeSurrounding("'")
        if (apiKey.isBlank()) {
            emit(
                Result.failure(
                    IllegalStateException(
                        "Gemini API Key is missing. Please add GEMINI_API_KEY=your_key in local.properties"
                    )
                )
            )
            return@flow
        }

        val systemInstructionContent = content {
            text(
                "You are a professional Android Development and UI/UX Tutor for the SkillFlow app. " +
                        "Answer the user's question based on this nugget content: $context. Keep answers concise and beginner-friendly."
            )
        }

        // Candidate models to try sequentially in case of model availability or 404 issues
        val candidateModels = listOf(
            "gemini-1.5-flash-latest",
            "gemini-1.5-flash",
            "gemini-2.0-flash",
            "gemini-1.5-pro",
            "gemini-1.0-pro"
        )

        var lastErrorMsg: String? = null

        for (modelName in candidateModels) {
            try {
                Timber.d("Attempting Gemini API request with model: $modelName")
                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey,
                    systemInstruction = systemInstructionContent
                )

                val response = generativeModel.generateContent(userQuestion)
                val responseText = response.text

                if (!responseText.isNullOrBlank()) {
                    Timber.d("Successfully received response from model: $modelName")
                    emit(Result.success(responseText))
                    return@flow
                }
            } catch (t: Throwable) {
                Timber.w(t, "Model $modelName failed")
                lastErrorMsg = t.localizedMessage ?: t.message
            }
        }

        // Format clean error message if all candidate models fail
        val cleanErrorMessage = when {
            lastErrorMsg?.contains("404") == true || lastErrorMsg?.contains("NOT_FOUND") == true || lastErrorMsg?.contains("not found") == true ->
                "The AI model is currently unavailable for your API key. Please check Google AI Studio (aistudio.google.com) to verify your API Key and enabled models."
            lastErrorMsg?.contains("401") == true || lastErrorMsg?.contains("invalid authentication") == true ->
                "Invalid API Key. Please verify GEMINI_API_KEY in local.properties."
            else ->
                "Unable to connect to SkillFlow AI Tutor right now. Please try again in a moment."
        }

        emit(Result.failure(Exception(cleanErrorMessage)))
    }.flowOn(Dispatchers.IO)
}
