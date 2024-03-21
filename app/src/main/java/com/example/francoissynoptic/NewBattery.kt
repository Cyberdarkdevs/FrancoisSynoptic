package com.example.francoissynoptic

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.BatteryManager
import android.util.Log
import android.widget.RemoteViews
import java.util.Timer
import java.util.TimerTask

class NewBattery {
    companion object {
        val intervalMillis = 2000 // 3600000 = 1 hour

        fun SwitchBattery(intent: Intent, context: Context) {




            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

                if (isCharging) {
                    Log.d("BatteryInfo", "Device is charging")
                    val chargingImageResource = R.drawable.charging
                    val views = RemoteViews(context.packageName, R.layout.widget)
                    views.setImageViewResource(R.id.battery_icon, chargingImageResource)
                    NewBattery.saveIsCharging(context, true)
                    Log.d("BatteryInfo", "IsCharging saved: ${NewBattery.saveIsCharging(context, true)}")

                    // I dont know why this is not working... Code tested and logs state its working.
                }

                else {
                    val batteryStatusIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                    val batteryLevel = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                    val batteryStatus = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN) ?: BatteryManager.BATTERY_STATUS_UNKNOWN
                    Log.d("BatteryInfo", "Battery Level: $batteryLevel, Status: $batteryStatus")

                    val (batteryDrawable, _) = getBatteryDrawableResource(batteryLevel)

                    val views = RemoteViews(context.packageName, R.layout.widget)



                    views.setImageViewResource(R.id.battery_icon, batteryDrawable)


                    val widget = ComponentName(context, WidgetWidgetProvider::class.java)
                    val manager = AppWidgetManager.getInstance(context)
                    manager.updateAppWidget(widget, views)
                }
            }







        }


        fun saveIsCharging(context: Context, isCharging: Boolean) {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedpref", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("IsCharging", isCharging)
            editor.apply()
        }




        fun getBatteryDrawableResource(percentage: Int = 0): Pair<Int, Int> {
            val resourceId: Int

            if (percentage >= 75) {
                resourceId = R.drawable.fully_charged
                Log.d("getting inside here", "75 - " + resourceId)
            } else if (percentage in 45..74) {
                resourceId = R.drawable.fifty_percent
                Log.d("getting inside here", "60 - " + resourceId)
            } else if (percentage in 15..44) {
                resourceId = R.drawable.twenty_percent
                Log.d("getting inside here", "30 - " + resourceId)
            } else {
                resourceId = R.drawable.ten_percent
                Log.d("getting inside here", "else - " + resourceId)
            }

            return Pair(resourceId, percentage)
        }
    }


    fun startBatteryUpdateTimer(context: Context) {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                SwitchBattery(batteryIntent ?: Intent(), context)
            }
        }, 0, intervalMillis.toLong())
    }

}


