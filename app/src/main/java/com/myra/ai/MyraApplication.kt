package com.myra.ai

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import timber.log.Timber

class MyraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("MyraApplication initialized")
        
        // Create notification channels
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Floating Window Channel
            val floatingWindowChannel = NotificationChannel(
                "myra_channel",
                "Myra Floating Window",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for Myra floating window service"
            }
            notificationManager.createNotificationChannel(floatingWindowChannel)
            
            // Voice Activation Channel
            val voiceActivationChannel = NotificationChannel(
                "myra_voice_channel",
                "Myra Voice Activation",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for Myra voice activation service"
            }
            notificationManager.createNotificationChannel(voiceActivationChannel)
            
            Timber.d("Notification channels created")
        }
    }
}
