
import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class Notification1 {
  static const MethodChannel _channel =
      const MethodChannel('notification1');

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
      final String version = await _channel.invokeMethod('showNotification', {
        'title': title,
        'message': body,
        'channel_id': channelid
      });
      return version;
    } catch (e) {
      print(e);
      return e.toString();
    }
  }

  static Future<String> createNotificationChannel({String id, String name, String description}) async {
    try {
      final String version = await _channel.invokeMethod('createNotificationChannel', {
        'id': id,
        'name': name,
        'description': description,
      });
      return version;
    } catch (e) {
      print(e);
      return e.toString();
    }
  }

  ///requestNotificationPermission
  static Future<String> requestNotificationPermission() async {
    try{
      final String version = await _channel.invokeMethod('requestNotificationPermission');
      return version;
    } catch(e){
      print(e);
      return e.toString();
    }
  }
}
