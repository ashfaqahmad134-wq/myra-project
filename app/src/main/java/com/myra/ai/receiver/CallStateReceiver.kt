package com.myra.ai.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import timber.log.Timber

class CallStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            
            Timber.d("Call state: $state, Incoming number: $incomingNumber")
            
            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    Timber.d("Phone is ringing")
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    Timber.d("Phone call in progress")
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Timber.d("Phone call disconnected")
                }
            }
        }
    }
}
