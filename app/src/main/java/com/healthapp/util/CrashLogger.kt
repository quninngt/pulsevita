package com.healthapp.util

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Global crash logger. Call init() in Application.onCreate() BEFORE super.onCreate().
 * On crash: writes full stack trace to app-private file.
 * On next launch: call getCrashLog() to retrieve and display it.
 */
object CrashLogger {

    private const val FILE_NAME = "crash_log.txt"

    fun init(context: Context) {
        val ctx = context.applicationContext
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val log = buildString {
                    appendLine("=== CRASH LOG ===")
                    appendLine("Time: $timestamp")
                    appendLine("Thread: ${thread.name}")
                    appendLine("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
                    appendLine("Android: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})")
                    appendLine()
                    appendLine(sw.toString())
                }
                File(ctx.filesDir, FILE_NAME).writeText(log)
            } catch (_: Exception) {
                // Best effort — don't mask the original crash
            }
            // Let the default handler kill the process
            Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(thread, throwable)
        }
    }

    fun getCrashLog(context: Context): String? {
        val file = File(context.filesDir, FILE_NAME)
        return if (file.exists()) {
            val content = file.readText()
            file.delete()
            content
        } else null
    }
}
