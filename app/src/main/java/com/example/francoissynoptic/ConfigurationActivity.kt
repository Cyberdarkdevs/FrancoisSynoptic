package com.example.francoissynoptic

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class ConfigurationActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        intent.extras?.also { extras ->
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val cities = listOf("Valletta", "Paris", "Rome")
        val listView: ListView = findViewById(R.id.city_list)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cities)

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = cities[position]
            Log.d("----Selected City----", selectedCity)

            saveSelectedCityToSharedPreferences(selectedCity)

            updateWidget(this, appWidgetId, selectedCity)

            setResult(RESULT_OK, Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            })
            finish()
        }
    }

    private fun saveSelectedCityToSharedPreferences(city: String) {
        val prefs = getSharedPreferences(getString(R.string.widget_preferences_file), Context.MODE_PRIVATE)
        prefs.edit().putString(getString(R.string.pref_selected_city_key) + "_$appWidgetId", city).apply()
        Log.d("SharedPreferences", "Selected city saved: $city")
    }

    companion object {
        private fun saveSelectedCityToSharedPreferences(context: Context, appWidgetId: Int, city: String) {
            val prefs = context.getSharedPreferences(context.getString(R.string.widget_preferences_file), Context.MODE_PRIVATE)
            prefs.edit().putString(context.getString(R.string.pref_selected_city_key) + "_$appWidgetId", city).apply()
            Log.d("SharedPreferences edit key", "Selected city saved: ${context.getString(R.string.pref_selected_city_key) + "_$appWidgetId"}")
        }

        fun updateWidget(context: Context, appWidgetId: Int, selectedCity: String) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val widget = ComponentName(context, WidgetWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(widget)

            ids.forEach { id ->
                val intent = Intent(context, WidgetWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(id))
                    putExtra("EXTRA_CITY_NAME", selectedCity)
                }
                context.sendBroadcast(intent)
            }

            // Logs left here for proof of testing purposes
            Log.d("city in updateWidget", selectedCity)
            Log.d("appwidgetId in updateWidget", appWidgetId.toString())
            Log.d("context in updateWidget", context.toString())
            saveSelectedCityToSharedPreferences(context, appWidgetId, selectedCity)
        }
    }
}