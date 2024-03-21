package com.example.francoissynoptic

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager

object BatteryUtils {
    private const val TAG = "BatteryUtils"

    fun saveLastChargerConnectionTime(context: Context) {
        val currentTimeMillis = System.currentTimeMillis()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putLong("last_charger_connection_time", currentTimeMillis)
        editor.apply()
        Log.d(TAG, "saveLastChargerConnectionTime: Last charger connection time saved: $currentTimeMillis")
    }

    fun getLastChargerConnectionTime(context: Context): Long {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val lastConnectionTime = sharedPreferences.getLong("last_charger_connection_time", 0)
        Log.d(TAG, "getLastChargerConnectionTime: Last charger connection time retrieved: $lastConnectionTime")
        return lastConnectionTime
    }
}