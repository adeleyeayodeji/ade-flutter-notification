package com.adeleyeayodeji.notification1

import androidx.annotation.NonNull
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.Context
import android.content.Intent
import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import com.google.firebase.messaging.FirebaseMessaging

/** Notification1Plugin */
class Notification1Plugin: BroadcastReceiver(), FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var notificationHandler: NotificationHandler
  //applicationContext
  private lateinit var applicationContext2: Context

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "notification1")
    //applicationContext
    applicationContext2 = flutterPluginBinding.applicationContext
    //notificationHandler
    notificationHandler = NotificationHandler(applicationContext2)

    //register broadcast receiver
    val intentFilter = IntentFilter()
    intentFilter.addAction("ADE_FLUTTER_NOTIFICATION_MESSAGE_RECEIVED")
    applicationContext2.registerReceiver(this, intentFilter)


    //get firebase token
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
      if (!task.isSuccessful) {
        println("Fetching FCM registration token failed ${task.exception}")
        return@addOnCompleteListener
      }

      // Get new FCM registration token
      val token = task.result
      println("FCM registration token: $token")
      //send token to flutter
      sendTokenToFlutter(token)
    }

    //setMethodCallHandler
    channel.setMethodCallHandler(this)
  }

    //sendTokenToFlutter
    private fun sendTokenToFlutter(token: String?) {
        //send token to flutter
        channel.invokeMethod("onFCMToken", token)
    }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android testing ${android.os.Build.VERSION.RELEASE}")
      }
      "showNotification" -> {
        val title = call.argument<String>("title")
        val message = call.argument<String>("message")
        val channel_id = call.argument<String>("channel_id")

        if (title != null && message != null && channel_id != null) {
          notificationHandler.showNotification(title, message, channel_id)
          result.success(null)
        } else {
          result.error("INVALID_ARGUMENTS", "Title or message is missing.", null)
        }
      }
      "createNotificationChannel" -> {
        val argData = call.arguments as java.util.HashMap<String, String>
        val completed = notificationHandler.createNotificationChannel(argData)
        if (completed == true){
          result.success(completed)
        }
        else{
          result.error("Error Code", "Error Message", null)
        }
      }
        "requestNotificationPermission" -> {
            val permissionGranted = notificationHandler.requestNotificationPermission()
            result.success(permissionGranted)
        }
      "getFCMToken" -> {
        //get firebase token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
          if (!task.isSuccessful) {
            println("Fetching FCM registration token failed ${task.exception}")
            return@addOnCompleteListener
          }

          // Get new FCM registration token
          val token = task.result
          //return token
            result.success(token)
        }
        result.success(null)
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    //unregister broadcast receiver
    applicationContext2.unregisterReceiver(this)
  }

  override fun onReceive(cntext: Context?, intent: Intent?) {

    // Check if the received intent is null
    if (intent == null) {
      return
    }

    // Get the action from the intent
    val action = intent.action

    // Check the action using a switch statement
    when (action) {
      "ADE_FLUTTER_NOTIFICATION_MESSAGE_RECEIVED" -> {
        //get data from intent
        val data = intent?.getStringExtra("data")
        //get title
        val title = intent?.getStringExtra("title")
        //get body
        val body = intent?.getStringExtra("body")
        //trigger notification
        notificationHandler.showNotification(title!!, body!!, "CHAT_MESSAGES")
        //log
//        Log.d("Notification1Plugin", "onReceive: $data")
        //send data to flutter
        channel.invokeMethod("handleNotification", data)
      }
      "ADE_FLUTTER_NOTIFICATION_CLICK" -> {
        //get data from intent
        val data = intent?.getStringExtra("data")
        //log
//        Log.d("Notification1Plugin", "onClick: $data")
        //send data to flutter
        channel.invokeMethod("onNotificationClick", data)
      }
      "ADE_FLUTTER_NOTIFICATION_TOKEN_REFRESH" -> {
        //get token from intent
        val token = intent?.getStringExtra("token")
        //send token to flutter
        sendTokenToFlutter(token)
      }
      // Add more cases for other actions as needed
      else -> {
        // Handle unknown or default action
      }
    }
  }
}
