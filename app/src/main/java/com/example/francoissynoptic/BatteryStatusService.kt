package com.example.francoissynoptic

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import java.util.Calendar
import java.util.Date

class BatteryStatusService : Service() {

    private val TAG = "BatteryStatusService"
    private val INTERVAL = 2000 // 1 hour interval in milliseconds

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Handle battery status change
            updateBatteryWidget(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        registerReceiver(batteryReceiver, filter)
        scheduleNextUpdate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun updateBatteryWidget(context: Context?) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisWidget = ComponentName(context!!, BatteryWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        appWidgetIds.forEach { appWidgetId ->
            val batteryLevel = getBatteryLevel(context)
            val batteryStatus = getBatteryStatus(context)
            val views = RemoteViews(context.packageName, R.layout.widget).apply {
                val batteryColor = when {
                    batteryLevel in 75..100 && batteryStatus == BatteryManager.BATTERY_STATUS_DISCHARGING -> R.drawable.fully_charged
                    batteryLevel in 45..74 && batteryStatus == BatteryManager.BATTERY_STATUS_DISCHARGING -> R.drawable.seventy_percent
                    batteryLevel < 45 && batteryStatus == BatteryManager.BATTERY_STATUS_DISCHARGING -> R.drawable.fifty_percent
                    else -> R.drawable.fully_charged
                }
                setImageViewResource(R.id.battery_icon, batteryColor)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getBatteryLevel(context: Context): Int {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    }

    private fun getBatteryStatus(context: Context): Int {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    }

    private fun scheduleNextUpdate() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, BatteryStatusService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val currentTime = Calendar.getInstance().timeInMillis
        val nextUpdateTime = currentTime + INTERVAL
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextUpdateTime, pendingIntent)
        Log.d(TAG, "Next update scheduled at: ${Date(nextUpdateTime)}")
    }
}
