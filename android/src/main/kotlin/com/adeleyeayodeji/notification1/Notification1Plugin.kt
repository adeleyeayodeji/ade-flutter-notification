package com.adeleyeayodeji.notification1

import androidx.annotation.NonNull
import android.content.Context
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

/** Notification1Plugin */
class Notification1Plugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var notificationHandler: NotificationHandler
  //applicationContext
  private lateinit var applicationContext: Context

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "notification1")
    //applicationContext
    applicationContext = flutterPluginBinding.applicationContext
    //notificationHandler
    notificationHandler = NotificationHandler(applicationContext)

    //setMethodCallHandler
    channel.setMethodCallHandler(this)
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
      else -> {
        result.notImplemented()
      }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
