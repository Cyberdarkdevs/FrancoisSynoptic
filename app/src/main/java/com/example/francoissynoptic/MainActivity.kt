package com.example.francoissynoptic

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var selectedCity: String = "Valletta" // Default city
    private val newBattery = NewBattery()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val citySpinner: Spinner = findViewById(R.id.spinner_city_selector)
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedCity = parent.getItemAtPosition(position) as String
                Log.d("SelectedCity!!!!))!)!)!)!", selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        val updateButton: Button = findViewById(R.id.button_update_widget)
        updateButton.setOnClickListener {
            ConfigurationActivity.updateWidget(this, AppWidgetManager.INVALID_APPWIDGET_ID, selectedCity)
        }

        // Start the battery update timer
        newBattery.startBatteryUpdateTimer(this)

    }
}
