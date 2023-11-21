package com.adeleyeayodeji.notification1

import android.Manifest
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class NotificationHandler(private val context: Context) {

    companion object {
        const val NOTIFICATION_PERMISSION = Manifest.permission.ACCESS_NOTIFICATION_POLICY
        const val PERMISSION_REQUEST_CODE = 123 // You can set any code here
    }

    fun showNotification(title: String, message: String, channel_id: String, dynamicnulldata: String? = null, notificationId: Int = 1) {
        try {
            // Get small icon name
            val smallIconName = context.getString(context.resources.getIdentifier("small_icon", "string", context.packageName))

            // Create an intent to open an activity when the notification is clicked in flutter
            val intent = Intent(context, AdeFlutterActivity::class.java)
            // Add a unique action name for your app
            intent.action = "ADE_FLUTTER_NOTIFICATION_CLICK"
            // Check if dynamicnulldata is not null
            if (dynamicnulldata != null) {
                // Put dynamicnulldata
                intent.putExtra("data", dynamicnulldata)
            } else {
                // Put data
                intent.putExtra("data", "null")
            }

            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Create notification
            val builder = NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(
                    context.resources.getIdentifier(
                        smallIconName, "drawable", context.packageName
                    )
                )
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(
                    pendingIntent
                )

            val notificationManager = NotificationManagerCompat.from(context)
            //randomise notification id
            val notificationId = (0..100).random()
            // Use the provided notificationId to ensure unique notifications
            notificationManager.notify(notificationId, builder.build())
        } catch (e: Exception) {
            // Log any errors
            Log.d("ADEFLUTTERNOTIFICATION", "Error: ${e.message}")
        }
    }


    fun requestNotificationPermission(): Boolean {
        //checkIfNotificationAllowed
        if (!checkIfNotificationAllowed()) {
            //open notification settings
            openNotificationSettings()
        }
        return true
    }

    fun createNotificationChannel(mapData: HashMap<String, String>): Boolean {
        //get data from map
        val id = mapData["id"]
        val name = mapData["name"]
        val descriptionText = mapData["description"]

        //create notification channel
        val notificationChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                id,
                name,
                NotificationManager.IMPORTANCE_HIGH
            )
        } else {
            //show toast
            Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show()
            TODO("VERSION.SDK_INT < O")
        }

        //set light color
        notificationChannel.lightColor = Color.RED

        //set short vibration pattern
        notificationChannel.vibrationPattern = longArrayOf(0, 100, 200, 300, 0, 0, 0, 0, 0, 0)

        //set description
        notificationChannel.description = descriptionText

        //add sound
        notificationChannel.setShowBadge(true)

        //get notification manager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //create notification channel
        notificationManager.createNotificationChannel(notificationChannel)

        return true
    }

    private fun showNotificationSentToast() {
        Toast.makeText(context, "Notification sent!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Open notification settings if notification is not allowed
     */
    private fun openNotificationSettings() {
        //open notification settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //open notification settings
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
            } else {
                //show toast
                Toast.makeText(context, "Unable to open NotificationSettings", Toast.LENGTH_SHORT).show()
                return
            }

            //create intent
            val settingsIntent = Intent(intent)
            //FLAG_ACTIVITY_NEW_TASK
            settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            //set package
            settingsIntent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)

            //open settings
            context.startActivity(settingsIntent)
        } else {
            //show toast
            Toast.makeText(context, "Unable to open NotificationSettings", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Check if notification is allowed
     */
    private fun checkIfNotificationAllowed(): Boolean {
        //get notification manager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //check if notification is allowed
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.areNotificationsEnabled()
        } else {
            //show toast
            Toast.makeText(context, "Notification not allowed", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Check if the app is running in the background
     */
    private fun isAppRunningInBackground(context: Context): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val packageName = context.packageName
        val runningAppProcesses = activityManager?.runningAppProcesses ?: return false

        for (processInfo in runningAppProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && processInfo.processName == packageName) {
                return false
            }
        }
        return true
    }
}