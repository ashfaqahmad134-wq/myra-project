package com.myra.ai.util

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import timber.log.Timber
import java.io.File

class ImageCaptureCallback(private val imageFile: File) : ImageCapture.OnImageSavedCallback {

    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        Timber.d("Image successfully saved to: ${imageFile.absolutePath}")
    }

    override fun onError(exception: ImageCaptureException) {
        Timber.e(exception, "Image capture failed")
    }
}
