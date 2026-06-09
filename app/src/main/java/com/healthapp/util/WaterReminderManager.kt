package com.healthapp.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

/**
 * 饮水提醒管理器
 * 使用 AlarmManager 定时发送通知提醒用户喝水
 */
object WaterReminderManager {

    const val CHANNEL_ID = "water_reminder"
    const val NOTIFICATION_ID = 1001
    const val ACTION_REMINDER = "com.healthapp.WATER_REMINDER"
    private const val PREFS_NAME = "water_reminder_prefs"
    private const val KEY_ENABLED = "reminder_enabled"
    private const val KEY_INTERVAL_HOURS = "reminder_interval_hours"
    private const val KEY_START_HOUR = "reminder_start_hour"
    private const val KEY_END_HOUR = "reminder_end_hour"

    /** 创建通知渠道（Android 8+） */
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "饮水提醒",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "定时提醒您喝水，保持健康"
            enableVibration(true)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    /** 获取用户设置 */
    fun isEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)
    }

    fun getIntervalHours(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_INTERVAL_HOURS, 2)
    }

    fun getStartHour(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_START_HOUR, 8)
    }

    fun getEndHour(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_END_HOUR, 22)
    }

    /** 保存设置 */
    fun saveSettings(context: Context, enabled: Boolean, intervalHours: Int, startHour: Int, endHour: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ENABLED, enabled)
            .putInt(KEY_INTERVAL_HOURS, intervalHours)
            .putInt(KEY_START_HOUR, startHour)
            .putInt(KEY_END_HOUR, endHour)
            .apply()

        if (enabled) {
            scheduleNext(context)
        } else {
            cancel(context)
        }
    }

    /** 调度下一次提醒 */
    fun scheduleNext(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, WaterReminderReceiver::class.java).apply {
            action = ACTION_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intervalMs = getIntervalHours(context) * 60 * 60 * 1000L
        val triggerTime = System.currentTimeMillis() + intervalMs

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Fallback to inexact alarm
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    /** 取消提醒 */
    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, WaterReminderReceiver::class.java).apply {
            action = ACTION_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /** 检查当前时间是否在提醒范围内 */
    fun isWithinReminderHours(context: Context): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val start = getStartHour(context)
        val end = getEndHour(context)
        return hour in start until end
    }
}
