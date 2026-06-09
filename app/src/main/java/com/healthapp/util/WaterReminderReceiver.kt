package com.healthapp.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * 饮水提醒广播接收器
 * 接收 AlarmManager 触发的提醒，显示通知
 */
class WaterReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != WaterReminderManager.ACTION_REMINDER) return

        // 检查是否在提醒时间范围内
        if (!WaterReminderManager.isWithinReminderHours(context)) {
            // 不在范围内，调度下一次
            WaterReminderManager.scheduleNext(context)
            return
        }

        // 显示通知
        showNotification(context)

        // 调度下一次提醒
        WaterReminderManager.scheduleNext(context)
    }

    private fun showNotification(context: Context) {
        // 点击通知打开APP
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val reminders = listOf(
            "💧 该喝水啦！保持水分，保持健康~",
            "💧 工作辛苦了，喝杯水休息一下吧！",
            "💧 记得喝水哦！每天8杯水，健康常相伴~",
            "💧 喝水时间到！补充水分，提高效率~",
            "💧 别忘了喝水！你的身体需要水分~",
            "💧 来杯水吧！保持充足水分，皮肤更好哦~"
        )
        val message = reminders.random()

        val notification = NotificationCompat.Builder(context, WaterReminderManager.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("饮水提醒")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                WaterReminderManager.NOTIFICATION_ID, notification
            )
        } catch (e: SecurityException) {
            // Android 13+ 需要 POST_NOTIFICATIONS 权限
            e.printStackTrace()
        }
    }
}
