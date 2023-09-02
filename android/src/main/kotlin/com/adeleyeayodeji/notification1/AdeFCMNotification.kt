package com.adeleyeayodeji.notification1
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
            // Send a broadcast to the app
            sendBroadcast(intent)
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