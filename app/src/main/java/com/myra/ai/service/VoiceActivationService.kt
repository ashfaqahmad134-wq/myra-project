package com.myra.ai.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.RESULTS_RECOGNITION
import androidx.core.app.NotificationCompat
import com.myra.ai.R
import timber.log.Timber
import java.util.Locale

class VoiceActivationService : Service(), RecognitionListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var hotwakeupDetected = false
    private val hotword = "myra"
    private val notificationId = 1002

    override fun onCreate() {
        super.onCreate()
        Timber.d("VoiceActivationService created")
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(this)
        
        startForegroundNotification()
        startContinuousListening()
    }

    private fun startForegroundNotification() {
        val notification = NotificationCompat.Builder(this, "myra_voice_channel")
            .setContentTitle("Myra Voice Assistant")
            .setContentText("Listening for hotword 'Myra'...")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
        
        startForeground(notificationId, notification)
        Timber.d("Voice activation foreground notification started")
    }

    private fun startContinuousListening() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            val speechIntent = Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().language)
                putExtra(android.speech.RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                putExtra(android.speech.RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
            }
            
            speechRecognizer?.startListening(speechIntent)
            isListening = true
            Timber.d("Started continuous speech listening")
        } else {
            Timber.e("Speech recognition not available")
        }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Timber.d("Ready for speech")
    }

    override fun onBeginningOfSpeech() {
        Timber.d("Beginning of speech detected")
    }

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        Timber.d("End of speech detected")
    }

    override fun onError(error: Int) {
        Timber.e("Speech recognition error: $error")
        // Restart listening on error
        if (isListening) {
            startContinuousListening()
        }
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
            val recognizedText = matches[0].lowercase(Locale.getDefault())
            Timber.d("Recognized: $recognizedText")
            
            // Check if hotword detected
            if (recognizedText.contains(hotword)) {
                hotwakeupDetected = true
                Timber.d("Hotword 'Myra' detected!")
                handleHotwakeup()
            } else if (hotwakeupDetected) {
                // Process command after hotword
                handleUserCommand(recognizedText)
                hotwakeupDetected = false
            }
        }
        
        // Restart listening
        if (isListening) {
            startContinuousListening()
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val partialMatches = partialResults?.getStringArrayList(RESULTS_RECOGNITION)
        if (partialMatches != null && partialMatches.isNotEmpty()) {
            Timber.d("Partial result: ${partialMatches[0]}")
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    private fun handleHotwakeup() {
        // Play wake-up sound or visual feedback
        Timber.d("Processing hotwakeup sequence")
    }

    private fun handleUserCommand(command: String) {
        Timber.d("Processing command: $command")
        
        when {
            command.contains("take") && command.contains("picture") -> {
                startService(Intent(this, CameraControlService::class.java).apply {
                    action = "com.myra.ai.ACTION_TAKE_SELFIE"
                })
            }
            command.contains("record") && command.contains("video") -> {
                startService(Intent(this, CameraControlService::class.java).apply {
                    action = "com.myra.ai.ACTION_RECORD_VIDEO"
                })
            }
            command.contains("call") -> {
                handlePhoneCallCommand(command)
            }
            command.contains("whatsapp") -> {
                handleWhatsAppCommand(command)
            }
        }
    }

    private fun handlePhoneCallCommand(command: String) {
        Timber.d("Phone call command: $command")
        // Delegate to call handler
    }

    private fun handleWhatsAppCommand(command: String) {
        Timber.d("WhatsApp command: $command")
        // Delegate to accessibility service
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        isListening = false
        Timber.d("VoiceActivationService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
