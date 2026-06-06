package com.healthapp

import android.app.Application
import com.healthapp.util.CrashLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HealthApp : Application() {
    override fun onCreate() {
        CrashLogger.init(this)  // Must be before super.onCreate()
        super.onCreate()
    }
}
