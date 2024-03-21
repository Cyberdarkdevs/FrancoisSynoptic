package com.example.francoissynoptic

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.francoissynoptic.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WidgetWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        appWidgetIds.forEach { appWidgetId ->
            CoroutineScope(Dispatchers.IO).launch {

                val prefs = context.getSharedPreferences(context.getString(R.string.widget_preferences_file), Context.MODE_PRIVATE)
                val key = context.getString(R.string.pref_selected_city_key) + "_0"
                val city = prefs.getString(key, "Paris")
                city?.let {

                    val message = null
                    updateAppWidget(context, appWidgetManager, appWidgetId, it, message)
                }
                Log.d(TAG, "City in shared pref: $key")
                Log.d(TAG, "Id in shared pref: $appWidgetId")
                Log.d(TAG, "Context in shared pref: $context")
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            // Handle update request
            val selectedCity = intent.getStringExtra("EXTRA_CITY_NAME")
            if (selectedCity != null) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisWidget = ComponentName(context, WidgetWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        } else if (intent.action == "EXTREME_WEATHER_ALERT") {
            // Handle extreme weather alert intent
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, WidgetWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            val message = intent.getStringExtra("EXTRA_WEATHER_MESSAGE")
            updateWidgetWithRedWarning(context, appWidgetManager, appWidgetIds, message)
        }
    }

    private suspend fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, city: String, message: String?) {
        try {
            val weatherData = RetrofitClient.webservice.getWeather(city.lowercase())
            withContext(Dispatchers.Main) {
                val views = RemoteViews(context.packageName, R.layout.widget).apply {
                    setTextViewText(R.id.city_name, weatherData.name)
                    setTextViewText(R.id.temperature, "${weatherData.temp}°C")
                    setTextViewText(R.id.weather_status, weatherData.condition)

                    if (message.isNullOrEmpty()) {
                        setViewVisibility(R.id.extreme_weather_warning, View.GONE)
                    } else {
                        setTextViewText(R.id.extreme_weather_warning, message)
                        setViewVisibility(R.id.extreme_weather_warning, View.VISIBLE)
                    }
                }
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
            Log.d(TAG, "Weather Data: ${weatherData.name}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateWidgetWithRedWarning(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, message: String?) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget).apply {
                setTextViewText(R.id.extreme_weather_warning, message ?: "")
                setViewVisibility(R.id.extreme_weather_warning, if (message.isNullOrEmpty()) View.GONE else View.VISIBLE)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    companion object {
        private const val TAG = "WidgetWidgetProvider"
    }


}
