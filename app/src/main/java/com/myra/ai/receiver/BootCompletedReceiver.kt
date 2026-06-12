package com.myra.ai.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.myra.ai.service.FloatingWindowService
import com.myra.ai.service.VoiceActivationService
import timber.log.Timber

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.d("Device boot completed - starting Myra services")
            
            if (context != null) {
                // Start Floating Window Service
                val floatingWindowIntent = Intent(context, FloatingWindowService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(floatingWindowIntent)
                } else {
                    context.startService(floatingWindowIntent)
                }
                
                // Start Voice Activation Service
                val voiceActivationIntent = Intent(context, VoiceActivationService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(voiceActivationIntent)
                } else {
                    context.startService(voiceActivationIntent)
                }
            }
        }
    }
}
