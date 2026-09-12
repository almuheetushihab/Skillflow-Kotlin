package com.example.skillflow.domain.manager

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for managing Text-to-Speech (TTS) operations across the application.
 */
interface TtsManager {
    /**
     * StateFlow indicating whether TTS is currently speaking audio.
     */
    val isSpeaking: StateFlow<Boolean>

    /**
     * Speaks the provided text using TextToSpeech.
     *
     * @param text The text content to be read aloud.
     */
    fun speak(text: String)

    /**
     * Stops any ongoing speech immediately.
     */
    fun stop()

    /**
     * Releases TTS resources when no longer needed.
     */
    fun shutdown()
}
