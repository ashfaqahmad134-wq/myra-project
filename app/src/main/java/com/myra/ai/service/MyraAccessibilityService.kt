package com.myra.ai.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.net.Uri
import android.view.accessibility.AccessibilityEvent
import timber.log.Timber

class MyraAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event != null) {
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    val packageName = event.packageName?.toString()
                    Timber.d("Window state changed: $packageName")
                }
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                    Timber.d("Notification state changed")
                }
            }
        }
    }

    override fun onInterrupt() {
        Timber.d("Accessibility service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Timber.d("Accessibility service connected")
    }

    fun handleWhatsAppMessage(recipient: String, message: String) {
        Timber.d("Sending WhatsApp message to $recipient: $message")
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/?text=$message")
                setPackage("com.whatsapp")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to send WhatsApp message")
        }
    }

    fun handleWhatsAppCall(phoneNumber: String, isVideo: Boolean) {
        Timber.d("Initiating WhatsApp ${if (isVideo) "video" else "audio"} call to $phoneNumber")
        try {
            val scheme = if (isVideo) "vnd.com.whatsapp.voip.call" else "vnd.com.whatsapp.call"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("$scheme:///$phoneNumber")
                setPackage("com.whatsapp")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to initiate WhatsApp call")
        }
    }

    fun shareToSocialMedia(filePath: String, platform: String) {
        Timber.d("Sharing to $platform: $filePath")
        val file = java.io.File(filePath)
        if (!file.exists()) {
            Timber.e("File does not exist: $filePath")
            return
        }
        
        try {
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this,
                "com.myra.ai.fileprovider",
                file
            )
            
            val packageName = when (platform.lowercase()) {
                "facebook" -> "com.facebook.katana"
                "instagram" -> "com.instagram.android"
                "tiktok" -> "com.zhiliaoapp.musically"
                else -> return
            }
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                setPackage(packageName)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to share to $platform")
        }
    }
}
