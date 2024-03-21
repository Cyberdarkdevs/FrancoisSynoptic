package com.example.francoissynoptic

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import android.widget.RemoteViews

class BatteryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("BatteryWidgetProvider", "onUpdate: Battery widget updated")
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Log.d("BatteryWidgetProvider", "onEnabled: Battery widget enabled")
        context.registerReceiver(batteryStatusReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Log.d("BatteryWidgetProvider", "onDisabled: Battery widget disabled")
        context.unregisterReceiver(batteryStatusReceiver)
    }

    private val batteryStatusReceiver = object : BatteryBroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisWidget = ComponentName(context, BatteryWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                appWidgetIds.forEach { appWidgetId ->
                    updateBatteryWidget(context, appWidgetManager, appWidgetId, batteryLevel, batteryStatus)
                }
            }
        }
    }

    companion object {
        private fun updateBatteryWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, batteryLevel: Int, batteryStatus: Int) {
            Log.d("BatteryWidgetProvider", "updateBatteryWidget: Updating battery widget with id $appWidgetId")
            val views = RemoteViews(context.packageName, R.layout.widget).apply {
                val batteryColor = when {
                    batteryLevel in 75..100 && batteryStatus == BatteryManager.BATTERY_STATUS_DISCHARGING -> R.drawable.seventy_percent
                    batteryLevel in 45..74 && batteryStatus == BatteryManager.BATTERY_STATUS_DISCHARGING -> R.drawable.fifty_percent
                    batteryLevel < 45 && batteryStatus == BatteryManager.BATTERY_STATUS_DISCHARGING -> R.drawable.twenty_percent
                    else -> R.drawable.fully_charged
                }
                setImageViewResource(R.id.battery_icon, batteryColor)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
