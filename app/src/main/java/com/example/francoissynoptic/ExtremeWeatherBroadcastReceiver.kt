package com.example.francoissynoptic

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class ExtremeWeatherBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "EXTREME_WEATHER_ALERT") {
            Log.d("Broadcast Received", "Extreme weather alert received!")

            ConfigurationActivity.updateWidget(context, AppWidgetManager.INVALID_APPWIDGET_ID, "")
        }
    }
}

