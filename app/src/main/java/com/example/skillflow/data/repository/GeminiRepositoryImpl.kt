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
        val apiKey = BuildConfig.GEMINI_API_KEY
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

        // Configure system instruction using the SDK content builder
        val systemInstructionContent = content {
            text(
                "You are a professional Android Development and UI/UX Tutor for the SkillFlow app. " +
                        "Answer the user's question based on this nugget content: $context. Keep answers concise and beginner-friendly."
            )
        }

        // Primary model is gemini-1.5-flash with a fallback to gemini-1.5-pro
        val candidateModels = listOf("gemini-1.5-flash", "gemini-1.5-pro")
        var lastException: Throwable? = null

        for (modelName in candidateModels) {
            try {
                Timber.d("Attempting Gemini API call with model: $modelName")
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
            } catch (e: Exception) {
                Timber.e(e, "Failed Gemini API call with model: $modelName")
                lastException = e
            } catch (t: Throwable) {
                Timber.e(t, "Serialization or runtime exception with model: $modelName")
                lastException = t
            }
        }

        val userFriendlyMessage = when {
            lastException?.message?.contains("404") == true || lastException?.message?.contains("not found") == true ->
                "The requested Gemini AI model was not found or is not enabled for your API key. Please check your Gemini API key in local.properties."
            lastException?.message?.contains("API_KEY") == true || lastException?.message?.contains("403") == true ->
                "Invalid or unauthorized Gemini API Key. Please verify GEMINI_API_KEY in local.properties."
            else ->
                lastException?.localizedMessage ?: "Unable to retrieve response from SkillFlow AI Tutor."
        }

        emit(Result.failure(Exception(userFriendlyMessage, lastException)))
    }.flowOn(Dispatchers.IO)
}
