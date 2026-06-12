package com.myra.ai.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import timber.log.Timber
import java.util.Locale

class TextToSpeechService : Service(), TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("TextToSpeechService created")
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val text = intent?.getStringExtra("text") ?: return START_NOT_STICKY
        val language = intent.getStringExtra("language") ?: "en"
        
        Timber.d("Speaking text: $text in language: $language")
        speakText(text, language)
        
        return START_NOT_STICKY
    }

    private fun speakText(text: String, language: String) {
        textToSpeech?.apply {
            val locale = when (language.lowercase()) {
                "ur", "urdu" -> Locale("ur", "PK")
                else -> Locale.ENGLISH
            }
            setLanguage(locale)
            speak(text, TextToSpeech.QUEUE_ADD, null)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Timber.d("TextToSpeechService initialized")
        } else {
            Timber.e("TextToSpeechService initialization failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        Timber.d("TextToSpeechService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
