package com.example.francoissynoptic

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val NOTIFICATION_ID = 123

class WeatherAlertMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.containsKey("alert")) {
            val alertMessage = remoteMessage.data["alert"]
            Log.d(TAG, "FCM alert received: $alertMessage")
            displayNotification(alertMessage)
            triggerExtremeWeatherAlert(applicationContext)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "123",
                "Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM token refreshed: $token")
    }

    private fun displayNotification(message: String?) {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "NOTIFICATION_ID")
                .setContentTitle("Extreme Weather Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Handle missing permissions
            return
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun triggerExtremeWeatherAlert(context: Context) {
        val message = "WARNING: EXTREME WEATHER DETECTED"
        Log.d(TAG, "Triggering extreme weather alert: $message")
        val intent = Intent("EXTREME_WEATHER_ALERT").apply {
            putExtra("MESSAGE", message)
        }
        context.sendBroadcast(intent)
    }
}
