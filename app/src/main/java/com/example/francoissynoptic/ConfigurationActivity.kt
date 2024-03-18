package com.example.francoissynoptic

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ConfigurationActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget)

        intent.extras?.also { extras ->
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val cities = listOf("Valletta", "Paris", "Rome")
        val listView: ListView = findViewById<ListView>(R.id.city_list).apply {
            adapter = ArrayAdapter(this@ConfigurationActivity, android.R.layout.simple_list_item_1, cities)
            setOnItemClickListener { _, _, position, _ ->
                val selectedCity = cities[position]

                saveSelectedCityToSharedPreferences(selectedCity)

                updateWidget(selectedCity)

                setResult(RESULT_OK, Intent().apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                })
                finish()
            }
        }
    }

    private fun saveSelectedCityToSharedPreferences(city: String) {
        getSharedPreferences(getString(R.string.widget_preferences_file), Context.MODE_PRIVATE).edit().apply {
            putString(getString(R.string.pref_selected_city_key) + "_$appWidgetId", city)
            apply()
        }
    }

    private fun updateWidget(selectedCity: String) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val widget = ComponentName(this, WidgetWidgetProvider::class.java)
        val ids = appWidgetManager.getAppWidgetIds(widget)

        ids.forEach { id ->
            val intent = Intent(this, WidgetWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(id))
                putExtra("EXTRA_CITY_NAME", selectedCity) // You can pass the selected city as an extra
            }
            sendBroadcast(intent)
        }
    }
}
