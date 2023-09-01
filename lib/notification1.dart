
import 'dart:async';
import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

/// Notification1Plugin
/// This class is used to handle the FCM token and notification clicks
/// @author: Adeleye Ayodeji <adeleyeayodeji.com>
/// @since: 1.0.0
/// @flutter_version: 1.22.6
class Notification1 {
  static const MethodChannel _channel =
      const MethodChannel('notification1');

  /// Get platform version as string
  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// showNotification
  /// @param title
  /// @param body
  /// @return
  static Future<String> showNotification({String title, String body, String channelid}) async {
    try {
      final String res = await _channel.invokeMethod('showNotification', {
        'title': title,
        'message': body,
        'channel_id': channelid
      });
      return res;
    } catch (e) {
      print(e);
      return e.toString();
    }
  }

  /// showNotificationWithAttachment
  /// @param id
  /// @param title
  /// @param description
  /// @return
  static Future<String> createNotificationChannel({String id, String name, String description}) async {
    try {
      //check platform
      if(Platform.isAndroid) {
        final String res = await _channel.invokeMethod(
            'createNotificationChannel', {
          'id': id,
          'name': name,
          'description': description,
        });
        return res;
      }
    } catch (e) {
      print(e);
      return e.toString();
    }
  }

  ///requestNotificationPermission
  ///@return
  static Future<String> requestNotificationPermission() async {
    try{
      final String res = await _channel.invokeMethod('requestNotificationPermission');
      return res;
    } catch(e){
      print(e);
      return e.toString();
    }
  }


  ///requestFirebaseNotificationPermission
  ///@return
  static Future<String> requestFirebaseNotificationPermission() async {
    try{
      final String res = await _channel.invokeMethod('requestFirebaseNotificationPermission');
      return res;
    } catch(e){
      print(e);
      return e.toString();
    }
  }

  //getFCMToken
  static Future<String> getFCMToken() async {
    try{
      final String res = await _channel.invokeMethod('getFCMToken');
      return res;
    } catch(e){
      print(e);
      return e.toString();
    }
  }

}
