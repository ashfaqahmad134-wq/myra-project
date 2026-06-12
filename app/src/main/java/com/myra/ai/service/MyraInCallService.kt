package com.myra.ai.service

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.telecom.Call
import android.telecom.InCallService
import timber.log.Timber
import java.util.Locale

class MyraInCallService : InCallService(), TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var callListener: Call.Callback? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("MyraInCallService created")
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        Timber.d("Call added: ${call.state}")
        
        callListener = object : Call.Callback() {
            override fun onStateChanged(call: Call, state: Int) {
                handleCallState(call, state)
            }
        }
        call.registerCallback(callListener!!)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        Timber.d("Call removed")
        if (callListener != null) {
            call.unregisterCallback(callListener!!)
        }
    }

    private fun handleCallState(call: Call, state: Int) {
        Timber.d("Call state changed: $state")
        when (state) {
            Call.STATE_RINGING -> {
                Timber.d("Call ringing - can accept/reject via voice")
            }
            Call.STATE_ACTIVE -> {
                announceCallActive()
            }
            Call.STATE_DISCONNECTED -> {
                Timber.d("Call disconnected")
            }
        }
    }

    private fun announceCallActive() {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            textToSpeech?.speak(
                "Call connected",
                TextToSpeech.QUEUE_FLUSH,
                null
            )
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.getDefault()
            Timber.d("TextToSpeech initialized")
        } else {
            Timber.e("TextToSpeech initialization failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        Timber.d("MyraInCallService destroyed")
    }
}
