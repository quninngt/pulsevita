package com.healthapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 开机自启广播接收器
 * 设备重启后恢复饮水提醒
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (WaterReminderManager.isEnabled(context)) {
                WaterReminderManager.scheduleNext(context)
            }
        }
    }
}
