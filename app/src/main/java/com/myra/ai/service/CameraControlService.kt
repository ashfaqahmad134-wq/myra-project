package com.myra.ai.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.myra.ai.util.ImageCaptureCallback
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class CameraControlService : Service() {

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val countdownInterval = 1000L // 1 second
    private var countdownValue = 3
    private var imageCapture: ImageCapture? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Timber.d("CameraControlService action: $action")
        
        return when (action) {
            "com.myra.ai.ACTION_TAKE_SELFIE" -> {
                takeSelfieWithCountdown()
                START_NOT_STICKY
            }
            "com.myra.ai.ACTION_RECORD_VIDEO" -> {
                startVideoRecording()
                START_NOT_STICKY
            }
            else -> START_NOT_STICKY
        }
    }

    private fun takeSelfieWithCountdown() {
        Timber.d("Starting selfie countdown...")
        countdownValue = 3
        
        val countdownThread = Thread {
            while (countdownValue > 0) {
                Timber.d("Countdown: $countdownValue")
                Thread.sleep(countdownInterval)
                countdownValue--
            }
            
            // Capture image after countdown
            captureImage(CameraSelector.LENS_FACING_FRONT)
        }
        countdownThread.start()
    }

    private fun captureImage(lensFacing: Int) {
        Timber.d("Capturing image with lens facing: $lensFacing")
        
        val imageFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            .format(System.currentTimeMillis())
        val storageDir = getExternalFilesDir(null)
        val imageFile = File(storageDir, "IMG_$imageFileName.jpg")
        
        val outputOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()
        
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            ImageCaptureCallback(imageFile)
        )
        
        Timber.d("Image saved to: ${imageFile.absolutePath}")
    }

    private fun startVideoRecording() {
        Timber.d("Starting video recording from rear camera")
        // Video recording implementation would go here
        // This requires VideoCapture API from CameraX
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        Timber.d("CameraControlService destroyed")
    }
}
