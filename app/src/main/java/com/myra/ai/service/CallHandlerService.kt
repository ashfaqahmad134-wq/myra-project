package com.myra.ai.service

import android.content.Intent
import android.os.Build
import android.telecom.CallScreeningService
import android.telecom.Call
import timber.log.Timber

class CallHandlerService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        Timber.d("Call screening initiated for: ${callDetails.handle}")
        
        // Use Myra's voice commands to handle the call
        val caller = callDetails.handle?.schemeSpecificPart ?: "Unknown"
        Timber.d("Incoming call from: $caller")
        
        // Trigger text-to-speech to announce caller
        announceIncomingCall(caller)
        
        // Application doesn't reject the call by default
        // User voice commands will determine action
    }

    private fun announceIncomingCall(caller: String) {
        Timber.d("Announcing incoming call from: $caller")
        val textToSpeech = Intent(this, TextToSpeechService::class.java).apply {
            action = "com.myra.ai.ACTION_ANNOUNCE_CALL"
            putExtra("caller", caller)
        }
        startService(textToSpeech)
    }
}
