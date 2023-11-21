package com.adeleyeayodeji.notification1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin

class AdeFCMNotificationClickHandler : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //log
        Log.d("AdeFCMNotificationClickHandler", "notification clicked")
        //send broadcast to flutter
        val intent = Intent("ADE_FLUTTER_NOTIFICATION_CLICKED")
        context?.sendBroadcast(intent)
    }
}