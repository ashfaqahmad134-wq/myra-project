package com.myra.ai.service

import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService as NLS
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.os.Handler
import android.os.Looper
import timber.log.Timber
import java.util.Locale

class NotificationListenerService : NLS(), TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private val supportedPackages = listOf(
        "com.whatsapp",
        "com.whatsapp.w4b",
        "com.facebook.katana",
        "com.instagram.android",
        "com.zhiliaoapp.musically"
    )

    override fun onCreate() {
        super.onCreate()
        Timber.d("NotificationListenerService created")
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn != null) {
            val packageName = sbn.packageName
            if (packageName in supportedPackages) {
                val notification = sbn.notification
                val bundle = notification.extras
                
                val sender = bundle.getString(android.app.Notification.EXTRA_TITLE) ?: "Unknown"
                val content = bundle.getString(android.app.Notification.EXTRA_TEXT) ?: ""
                
                Timber.d("Notification from $packageName - Sender: $sender, Content: $content")
                
                announceNotification(packageName, sender, content)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Timber.d("Notification removed: ${sbn?.packageName}")
    }

    private fun announceNotification(packageName: String, sender: String, content: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val announcementText = buildAnnouncementText(packageName, sender, content)
            textToSpeech?.speak(
                announcementText,
                TextToSpeech.QUEUE_ADD,
                null
            )
        }
    }

    private fun buildAnnouncementText(packageName: String, sender: String, content: String): String {
        return when {
            packageName.contains("whatsapp") -> "WhatsApp from $sender: $content"
            packageName.contains("facebook") -> "Facebook message from $sender: $content"
            packageName.contains("instagram") -> "Instagram from $sender: $content"
            packageName.contains("tiktok") -> "TikTok notification from $sender: $content"
            else -> "Notification from $sender: $content"
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.getDefault()
            Timber.d("TextToSpeech initialized in NotificationListenerService")
        } else {
            Timber.e("TextToSpeech initialization failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        Timber.d("NotificationListenerService destroyed")
    }
}
