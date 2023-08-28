import UIKit
import Flutter
import Firebase
import FirebaseMessaging

/**
* AppDelegate
* This class is used to handle the FCM token and notification clicks
* @author: Adeleye Ayodeji <adeleyeayodeji.com>
* @since: 1.0.0
* @flutter_version: 1.22.6
*/
@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate, MessagingDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)
    // Configure Firebase
    FirebaseApp.configure()
    // Set the messaging delegate
    Messaging.messaging().delegate = self
    // Register for remote notifications. This shows a permission dialog on first run, to
    UNUserNotificationCenter.current().delegate = self
    // Request permission to display alerts and play sounds for remote notifications
     let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
      UNUserNotificationCenter.current().requestAuthorization(
        options: authOptions,
        completionHandler: { _, _ in }
    )
      
    // Register for remote notifications
    application.registerForRemoteNotifications()
      
    // Get FCM token
    Messaging.messaging().token { token, error in
        if let error = error {
          print("IOS Error fetching FCM registration token: \(error)")
        } else if let token = token {
          print("IOS FCM registration token: \(token)")
        }
    }
      
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
    
    /**
    * Handle FCM Token
    */
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
      let dataDict: [String: String] = ["token": fcmToken ?? ""]
      NotificationCenter.default.post(
        name: Notification.Name("FCMToken"),
        object: nil,
        userInfo: dataDict
      )

      //send the token to flutter
      if let flutterViewController = window?.rootViewController as? FlutterViewController {
            let channel = FlutterMethodChannel(name: "notification1", binaryMessenger: flutterViewController.binaryMessenger)
            channel.invokeMethod("onFCMToken", arguments: fcmToken)
       }
    }

    /**
    * Handle Click on Notification
    */
    override func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse) async {
      let userInfo = response.notification.request.content.userInfo
        
      // Trigger the method channel in Flutter
      if let flutterViewController = window?.rootViewController as? FlutterViewController {
          let channel = FlutterMethodChannel(name: "notification1", binaryMessenger: flutterViewController.binaryMessenger)
          channel.invokeMethod("onNotificationClick", arguments: userInfo)
      }

      // With swizzling disabled you must let Messaging know about the message, for Analytics
       Messaging.messaging().appDidReceiveMessage(userInfo)
    }
    
    /**
    * Handle Remote Notification
    */
    override func application(_ application: UIApplication,
    didReceiveRemoteNotification userInfo: [AnyHashable : Any],
       fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
      Messaging.messaging().appDidReceiveMessage(userInfo)
      completionHandler(.noData)
    }
    
    /**
    * Register for remote notifications (iOS 10+)
    */
    override func application(_ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
      Messaging.messaging().apnsToken = deviceToken;
    }
     
    /**
     * Handle Remote Notification
     */
    override func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
      withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
      let userInfo = notification.request.content.userInfo
        
      // Trigger the method channel in Flutter
      if let flutterViewController = window?.rootViewController as? FlutterViewController {
        let channel = FlutterMethodChannel(name: "notification1", binaryMessenger: flutterViewController.binaryMessenger)
        channel.invokeMethod("handleNotification", arguments: userInfo)
      }

      Messaging.messaging().appDidReceiveMessage(userInfo)

      // Change this to your preferred presentation option
      completionHandler([[.alert, .sound, .badge, .banner, .list]])
    }

}
