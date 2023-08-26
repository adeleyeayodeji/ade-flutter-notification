import UIKit
import Flutter
import Firebase
import FirebaseMessaging

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate, MessagingDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)
      
    FirebaseApp.configure()
    Messaging.messaging().delegate = self
      
    UNUserNotificationCenter.current().delegate = self

    let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
      UNUserNotificationCenter.current().requestAuthorization(
        options: authOptions,
        completionHandler: { _, _ in }
    )
      
    // Register for remote notifications
    application.registerForRemoteNotifications()
      
      
    Messaging.messaging().token { token, error in
        if let error = error {
          print("Error fetching FCM registration token: \(error)")
        } else if let token = token {
          print("FCM registration token: \(token)")
        }
    }
      
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
    
    
   func application(application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
      Messaging.messaging().apnsToken = deviceToken
   }
    
    
   func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
      print("Firebase registration token: \(String(describing: fcmToken))")

//      let dataDict: [String: String] = ["token": fcmToken ?? ""]
//      NotificationCenter.default.post(
//        name: Notification.Name("FCMToken"),
//        object: nil,
//        userInfo: dataDict
//      )
      // TODO: If necessary send token to application server.
      // Note: This callback is fired at each app startup and whenever a new token is generated.
    }
}
