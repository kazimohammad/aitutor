package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val tag = "TTSManager"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    private val _currentSpeed = MutableStateFlow(1.0f)
    val currentSpeed = _currentSpeed.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            // Attempt Bangla locale first, fall back to English if Bengali speech voice unavailable
            val bnLocale = Locale.forLanguageTag("bn-BD")
            val result = tts?.setLanguage(bnLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.forLanguageTag("bn"))
            }

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
        } else {
            Log.e(tag, "TTS init returned status $status")
        }
    }

    fun speak(text: String, isEnglish: Boolean = false) {
        if (!isInitialized || tts == null) return
        stop()

        if (isEnglish) {
            tts?.setLanguage(Locale.US)
        } else {
            val bnLocale = Locale.forLanguageTag("bn-BD")
            val res = tts?.setLanguage(bnLocale)
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
        }

        tts?.setSpeechRate(_currentSpeed.value)
        _isSpeaking.value = true
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PorashonaUtterance_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun setSpeed(speed: Float) {
        _currentSpeed.value = speed
        tts?.setSpeechRate(speed)
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
