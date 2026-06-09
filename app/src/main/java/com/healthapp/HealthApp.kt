package com.healthapp

import android.app.Application
import com.healthapp.util.CrashLogger
import com.healthapp.util.WaterReminderManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HealthApp : Application() {
    override fun onCreate() {
        CrashLogger.init(this)  // Must be before super.onCreate()
        super.onCreate()
        WaterReminderManager.createNotificationChannel(this)
    }
}
