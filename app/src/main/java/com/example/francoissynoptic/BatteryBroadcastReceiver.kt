package com.example.francoissynoptic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

open class BatteryBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

            val batteryStatusIntent = Intent(context, BatteryStatusService::class.java).apply {
                putExtra("BATTERY_LEVEL", batteryLevel)
                putExtra("BATTERY_STATUS", batteryStatus)
            }
            context.startService(batteryStatusIntent)
        }
    }
}


