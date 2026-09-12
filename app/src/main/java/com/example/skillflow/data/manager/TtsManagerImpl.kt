package com.example.skillflow.data.manager

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.skillflow.domain.manager.TtsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android TextToSpeech implementation of [TtsManager].
 */
@Singleton
class TtsManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TtsManager, TextToSpeech.OnInitListener {

    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.getDefault()
            }
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java", ReplaceWith("onError(utteranceId, -1)"))
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })
            isInitialized = true
            pendingText?.let { text ->
                pendingText = null
                speakInternal(text)
            }
        } else {
            isInitialized = false
            _isSpeaking.value = false
        }
    }

    override fun speak(text: String) {
        if (text.isBlank()) return

        if (!isInitialized) {
            pendingText = text
            if (tts == null) {
                tts = TextToSpeech(context, this)
            }
            return
        }

        speakInternal(text)
    }

    private fun speakInternal(text: String) {
        val utteranceId = UUID.randomUUID().toString()
        val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        if (result == TextToSpeech.ERROR) {
            _isSpeaking.value = false
        }
    }

    override fun stop() {
        pendingText = null
        if (isInitialized) {
            tts?.stop()
        }
        _isSpeaking.value = false
    }

    override fun shutdown() {
        pendingText = null
        if (isInitialized || tts != null) {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        }
        _isSpeaking.value = false
    }
}
