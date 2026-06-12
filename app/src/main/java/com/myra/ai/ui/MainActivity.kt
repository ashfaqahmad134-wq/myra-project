package com.myra.ai.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.myra.ai.R
import com.myra.ai.service.FloatingWindowService
import com.myra.ai.service.VoiceActivationService
import com.myra.ai.util.PermissionManager
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        Timber.d("MainActivity created")
        
        // Initialize Permission Manager
        permissionManager = PermissionManager(this)
        
        // Set window attributes for dark theme
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = resources.getColor(R.color.background, theme)
            navigationBarColor = resources.getColor(R.color.background, theme)
        }
        
        // Request necessary permissions
        requestRequiredPermissions()
        
        // Initialize services
        initializeServices()
    }

    private fun requestRequiredPermissions() {
        val requiredPermissions = mutableListOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.ANSWER_PHONE_CALLS,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            requiredPermissions.add(android.Manifest.permission.READ_MEDIA_VIDEO)
        }
        
        val permissionsToRequest = permissionManager.getMissingPermissions(requiredPermissions)
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
        
        // Check for overlay permission
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = android.net.Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }
    }

    private fun initializeServices() {
        // Start Floating Window Service
        val floatingWindowIntent = Intent(this, FloatingWindowService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(floatingWindowIntent)
        } else {
            startService(floatingWindowIntent)
        }
        Timber.d("FloatingWindowService started")
        
        // Start Voice Activation Service
        val voiceActivationIntent = Intent(this, VoiceActivationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(voiceActivationIntent)
        } else {
            startService(voiceActivationIntent)
        }
        Timber.d("VoiceActivationService started")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = mutableListOf<String>()
            for (i in permissions.indices) {
                if (grantResults[i] != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i])
                }
            }
            
            if (deniedPermissions.isNotEmpty()) {
                Timber.e("Permissions denied: $deniedPermissions")
            } else {
                Timber.d("All permissions granted")
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
