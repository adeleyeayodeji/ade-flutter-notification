import Flutter
import UIKit
import UserNotifications

/**
* SwiftNotification1Plugin
*/
public class SwiftNotification1Plugin: NSObject, FlutterPlugin, UNUserNotificationCenterDelegate {

  /**
    * Register the plugin
   */
  public static func register(with registrar: FlutterPluginRegistrar) {
   //declare the method channel for the package holding
    let channel = FlutterMethodChannel(name: "notification1", binaryMessenger: registrar.messenger())
    let instance = SwiftNotification1Plugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  /**
   * Handle the method channel
  */
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
     switch call.method {
    /**
    * Get the platform version
    */
     case "getPlatformVersion":
         let platformVersion = "iOS " + UIDevice.current.systemVersion
         print(platformVersion)
         result(platformVersion)
         //exit
         return
      /**
       * Trigger the notification
       */
     case "showNotification":
            let arguments = call.arguments as? [String: Any]
            let title = arguments?["title"] as? String ?? ""
            let body = arguments?["message"] as? String ?? ""
            let channelid = arguments?["channel_id"] as? String ?? ""
            scheduleLocalNotification(title: title, body: body, channelid: channelid)
          //exit
          return
     /**
      * Request notification permission
      */
     case "requestNotificationPermission":
            let center = UNUserNotificationCenter.current()
            center.delegate = self
            // Override point for customization after application launch.
            center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
                if granted {
                    print("Notification permission granted")
                } else {
                    print("Notification permission denied")
                    
//                    if let topViewController = UIApplication.shared.keyWindow?.rootViewController {
//                        // Show an alert to the user
//                       let alert = UIAlertController(title: "Notification Permission Denied", message: "You can enable notifications in the Settings app to stay updated.", preferredStyle: .alert)
//                       alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
//                       alert.addAction(UIAlertAction(title: "Open Settings", style: .default, handler: { _ in
//                           if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
//                               UIApplication.shared.open(settingsURL, options: [:], completionHandler: nil)
//                           }
//                       }))
//
//                       DispatchQueue.main.async {
//                           topViewController.present(alert, animated: true, completion: nil)
//                       }
//                    }
                }
            }
            //exit
            return
    /**
     * Fallback to default
     */
     default:
         result(FlutterMethodNotImplemented)
     }
  }

  /**
    * Show toast message
  */
    private func showToast(_ message: String) {
        if let topViewController = UIApplication.shared.keyWindow?.rootViewController {
            let toast = UIAlertController(title: nil, message: message, preferredStyle: .alert)
            print(UIScreen.main.bounds.height)
            // Calculate the y-coordinate to position the toast at the bottom
            let screenHeight = UIScreen.main.bounds.height
            print(screenHeight)
            let toastHeight: CGFloat = 50 // You can adjust this value
            let yPosition = screenHeight - toastHeight
            
            let toastFrame = CGRect(x: 500, y: yPosition, width: topViewController.view.frame.size.width, height: toastHeight)
            toast.view.frame = toastFrame
            
            topViewController.present(toast, animated: true, completion: nil)
            
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 3) {
                toast.dismiss(animated: true, completion: nil)
            }
        }
    }
    
    /**
     * Show the notification alert
     */
    public func userNotificationCenter(_ center: UNUserNotificationCenter,
                                    willPresent notification: UNNotification,
                                    withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.sound, .badge, .banner, .list])
    }


    /**
     * Schedule the notification
     */
    public func scheduleLocalNotification(title: String, body: String, channelid: String) {
            let content = UNMutableNotificationContent()
            content.title = title
            content.body = body
            content.sound = UNNotificationSound.default

            let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
            let request = UNNotificationRequest(identifier: channelid, content: content, trigger: trigger)

            let currentDelegate = UNUserNotificationCenter.current()
            //self as delegate
            currentDelegate.delegate = self

            currentDelegate.add(request) { error in
                if let error = error {
                    print("Error scheduling notification: \(error.localizedDescription)")
                } else {
                    print("Notification scheduled successfully")
                }
            }
    }
}
