package com.adeleyeayodeji.notification1_example

import io.flutter.embedding.android.FlutterActivity
import android.util.Log

class MainActivity: FlutterActivity() {


    /**
     * On create method
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Send a broadcast to the app
        //log
        Log.d(TAG, "App is in the foreground: intent action")
    }

    /**
     * On new intent method
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Send a broadcast to the app
        val intent = Intent()
        intent.action = "ADE_FLUTTER_NOTIFICATION_RESUMED"
        sendBroadcast(intent)
        //log
        Log.d(TAG, "App is in the foreground (onNewIntent): intent action: ${intent.action}")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
