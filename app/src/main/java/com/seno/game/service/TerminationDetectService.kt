package com.seno.game.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.seno.game.prefs.PrefsManager
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar

class TerminationDetectService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
//        PrefsManager.lastTimestamp = System.currentTimeMillis()

        val timestamp = System.currentTimeMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        stopSelf()
    }
}