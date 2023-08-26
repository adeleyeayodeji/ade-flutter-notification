package com.adeleyeayodeji.notification1

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.widget.Toast
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.net.Uri;
import android.media.AudioAttributes;
import android.content.ContentResolver;
import android.graphics.Color
import android.os.Build


class NotificationHandler(private val context: Context) {

    companion object {
        const val NOTIFICATION_PERMISSION = Manifest.permission.ACCESS_NOTIFICATION_POLICY
        const val PERMISSION_REQUEST_CODE = 123 // You can set any code here
    }

    fun showNotification(title: String, message: String, channel_id: String) {
       try {
           val smallIconName = context.getString(context.resources.getIdentifier("small_icon", "string", context.packageName))
           // Create notification
           val builder = NotificationCompat.Builder(context, channel_id).setSmallIcon(
               context.resources.getIdentifier(
                   smallIconName, "drawable", context.packageName
               )
           ).setContentTitle(title).setContentText(message)

           val notificationManager = NotificationManagerCompat.from(context)
           // notificationId is a unique int for each notification that you must define
           notificationManager.notify(1, builder.build())

           // Show toast
           showNotificationSentToast()
       } catch (e: Exception) {
           //print
           println("Error: ${e.message}")
           // Show toast
           Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
}
