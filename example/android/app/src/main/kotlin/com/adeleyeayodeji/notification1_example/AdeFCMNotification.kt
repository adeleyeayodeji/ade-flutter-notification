package com.adeleyeayodeji.notification1_example
import org.json.JSONObject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.ActivityManager

import android.app.PendingIntent
import androidx.annotation.NonNull
import android.content.IntentFilter

public class AdeFCMNotification : FirebaseMessagingService() {

    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        //get notification title
        val notificationTitle = remoteMessage.notification?.title
        //get notification body
        val notificationBody = remoteMessage.notification?.body
        // Add title and body to the JSON object
        val notificationData = JSONObject()
        //put data
        notificationData.put("data", remoteMessage.data)

        // Encode the JSON object as a string
        val notificationDataString = notificationData.toString()

        // Send the broadcast action ADE_FLUTTER_NOTIFICATION_MESSAGE_RECEIVED
        val intent = Intent()
        intent.action = "ADE_FLUTTER_NOTIFICATION_MESSAGE_RECEIVED"
        intent.putExtra("data", notificationDataString)
        //put title
        intent.putExtra("title", notificationTitle)
        //put body
        intent.putExtra("body", notificationBody)

        // Check if the app is in the foreground
        if (isAppInForeground(this)) {
            //log
            Log.d(TAG, "App is in the foreground")
            // Send a broadcast to the app
//            sendBroadcast(intent)

            //send notification
            // Create an explicit intent for an Activity in your app
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            //get notification title
            val notificationTitle = remoteMessage.notification?.title
            //get notification body
            val notificationBody = remoteMessage.notification?.body

            // Create notification
            val builder = NotificationCompat.Builder(this, "CHAT_MESSAGES")
                .setSmallIcon(R.drawable.small_icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            //get notification sound
            val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            //set notification sound
            builder.setSound(notificationSound)

            //show notification
            with(NotificationManagerCompat.from(this)) {
                //randomise notification id
                val notificationId = 1;
                // Use the provided notificationId to ensure unique notifications
                notify(notificationId, builder.build())
            }
        } else {
            Log.d(TAG, "App is in the background")
            // do nothing
        }

    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        //send broadcast
        val intent = Intent()
        //put token
        intent.putExtra("token", token)
        //action ADE_FLUTTER_NOTIFICATION_TOKEN_REFRESH
        intent.action = "ADE_FLUTTER_NOTIFICATION_TOKEN_REFRESH"
        //send broadcast
        sendBroadcast(intent)
    }
    // [END on_new_token]

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    companion object {
        private const val TAG = "AdeFCMNotification"

        private fun isAppInForeground(context: Context): Boolean {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
            val packageName = context.packageName
            val runningAppProcesses = activityManager?.runningAppProcesses ?: return false

            for (processInfo in runningAppProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && processInfo.processName == packageName) {
                    return true
                }
            }
            return false
        }
    }
}