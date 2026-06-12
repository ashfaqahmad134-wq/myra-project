package com.myra.ai.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.myra.ai.R
import timber.log.Timber

class FloatingWindowService : Service(), View.OnTouchListener, GestureDetector.OnGestureListener {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingIcon: ImageView
    private lateinit var gestureDetector: GestureDetector
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private val notificationId = 1001

    override fun onCreate() {
        super.onCreate()
        Timber.d("FloatingWindowService onCreate")
        
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        gestureDetector = GestureDetector(this, this)
        
        createFloatingIcon()
        startForegroundNotification()
    }

    private fun createFloatingIcon() {
        val layoutParams = WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            format = PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            width = 120
            height = 120
            x = 0
            y = 0
            gravity = Gravity.TOP or Gravity.START
        }
        
        floatingIcon = ImageView(this).apply {
            setImageDrawable(ContextCompat.getDrawable(this@FloatingWindowService, R.drawable.ic_myra_assistant))
            setBackgroundResource(R.drawable.floating_icon_background)
            scaleType = ImageView.ScaleType.CENTER
            setOnTouchListener(this@FloatingWindowService)
        }
        
        windowManager.addView(floatingIcon, layoutParams)
        Timber.d("Floating icon created and added to window")
    }

    private fun startForegroundNotification() {
        val notification = NotificationCompat.Builder(this, "myra_channel")
            .setContentTitle("Myra Assistant")
            .setContentText("Floating window is active")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        startForeground(notificationId, notification)
        Timber.d("Foreground notification started")
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            gestureDetector.onTouchEvent(event)
            
            return when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = (v?.layoutParams as WindowManager.LayoutParams).x
                    initialY = (v?.layoutParams as WindowManager.LayoutParams).y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - initialTouchX
                    val deltaY = event.rawY - initialTouchY
                    
                    val params = v?.layoutParams as WindowManager.LayoutParams
                    params.x = (initialX + deltaX).toInt()
                    params.y = (initialY + deltaY).toInt()
                    windowManager.updateViewLayout(v, params)
                    true
                }
                else -> false
            }
        }
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        Timber.d("Floating icon tapped - initiating voice input")
        // Trigger voice activation on tap
        val voiceActivationIntent = Intent(this, VoiceActivationService::class.java)
        voiceActivationIntent.action = "com.myra.ai.ACTION_VOICE_ACTIVATE"
        startService(voiceActivationIntent)
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean = false
    override fun onShowPress(e: MotionEvent?) {}
    override fun onSingleTapUp(e: MotionEvent?): Boolean = false
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = false
    override fun onLongPress(e: MotionEvent?) {}
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = false

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingIcon)
        Timber.d("FloatingWindowService destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
