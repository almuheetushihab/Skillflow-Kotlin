package com.example.skillflow.data.repository
import com.example.skillflow.domain.repository.GeminiRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.SerializationException
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
                "You are a professional Android and UI/UX Tutor for SkillFlow. " +
                        "Answer the user's question based on this nugget content: $context. " +
                        "Keep answers concise and beginner-friendly."
            )
        }

        val candidateModels = listOf(
            "gemini-1.5-flash-latest",
            "gemini-1.5-pro"
        )

        var lastError: Throwable? = null
        var lastTriedModel: String? = null

        for (modelName in candidateModels) {
            try {
                lastTriedModel = modelName
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
                } else {
                    lastError = IllegalStateException("Model '$modelName' returned an empty response.")
                }
            } catch (e: SerializationException) {
                Timber.w(e, "Serialization failed for model: $modelName")
                lastError = e
            } catch (e: Exception) {
                Timber.w(e, "Request failed for model: $modelName")
                lastError = e
            }
        }

        val errorText = lastError?.message.orEmpty()
        val message = when {
            errorText.contains("404", ignoreCase = true) ||
                    errorText.contains("NOT_FOUND", ignoreCase = true) ||
                    errorText.contains("not found", ignoreCase = true) -> {
                "Gemini model status error: '$lastTriedModel' is not available on the v1beta endpoint. " +
                        "Tried fallback model 'gemini-1.5-pro' as well, but both failed."
            }
            errorText.contains("401", ignoreCase = true) ||
                    errorText.contains("unauthorized", ignoreCase = true) ||
                    errorText.contains("invalid api key", ignoreCase = true) -> {
                "Invalid Gemini API key. Please verify GEMINI_API_KEY in local.properties."
            }
            lastError is SerializationException -> {
                "Gemini returned an unexpected response format (serialization error). Please try again later."
            }
            else -> {
                "Unable to connect to SkillFlow AI Tutor right now. ${lastError?.message ?: ""}".trim()
            }
        }

        emit(Result.failure(Exception(message, lastError)))
    }.flowOn(Dispatchers.IO)
}