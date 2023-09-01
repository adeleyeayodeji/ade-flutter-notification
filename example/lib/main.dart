import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_barcode_scanner/flutter_barcode_scanner.dart';
import 'package:notification1/notification1.dart';
import 'package:qr_flutter/qr_flutter.dart';

import 'function.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  Notification1.createNotificationChannel(
      id: "CHAT_MESSAGES",
      name: "Chats",
      description: "This is a notification from Notification1");
  runApp(MaterialApp(
    //use system theme
    darkTheme: ThemeData.dark(),
    themeMode: ThemeMode.system,
    debugShowCheckedModeBanner: false,
    home: MyApp(),
  ));
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String deviceToken = "";
  String _scanBarcode = 'Unknown';

  //text controller
  TextEditingController titleController = TextEditingController();
  TextEditingController bodyController = TextEditingController();


  startBarcodeScanStream() async {
    FlutterBarcodeScanner.getBarcodeStreamReceiver(
        "#ff6666", "Cancel", true, ScanMode.BARCODE)
        .listen((barcode) => print(barcode));
  }

  Future<void> scanQR() async {
    String barcodeScanRes;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      barcodeScanRes = await FlutterBarcodeScanner.scanBarcode(
          "#ff6666", "Cancel", true, ScanMode.QR);
      print(barcodeScanRes);
    } on PlatformException {
      barcodeScanRes = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _scanBarcode = barcodeScanRes;
    });
  }

  @override
  void initState() {
    super.initState();
    //requestNotificationPermission
    Notification1.requestNotificationPermission();

    //add a listener to handle notification when the app is in the foreground
    const MethodChannel _notificationChannel = MethodChannel('notification1');

    _notificationChannel.setMethodCallHandler((call) async {
      //Handle notification methods here
      switch (call.method) {
        case "handleNotification":
          // Handle the notification data here
          dynamic notificationData = call.arguments;
          print('Received notification in Flutter: $notificationData');
          break;
        case "onNotificationClick":
          // Handle notification click here
          dynamic notificationData = call.arguments;
          print('Notification clicked in Flutter: $notificationData');
          break;
        case "onFCMToken":
          // Device token
          String fcmToken = call.arguments;
          print('Device FCM Token in Flutter: $fcmToken');
          //update deviceToken
          setState(() {
            deviceToken = fcmToken;
          });
          break;
      }
    });
  }

  /**
   * On app reload, get the device token
   */
  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    //check if state is resumed
    if (state == AppLifecycleState.resumed) {
      //check if platform is android
      if (Platform.isAndroid) {
        //request for device token
        Notification1.getFCMToken();
      }
    }
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await Notification1.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Ade Flutter Notification'),
        ),
        body: SingleChildScrollView(
          child: Center(
            child: Column(
              //move content to the center
              mainAxisAlignment: MainAxisAlignment.center,
              //center column contents vertically
              crossAxisAlignment: CrossAxisAlignment.center,
              //add children to the column
              children: [
                //check if deviceToken is not empty
                deviceToken != "" ? Padding(
                  padding: const EdgeInsets.all(20.0),
                  child: QrImage(
                    data: deviceToken,
                    version: QrVersions.auto,
                    size: 200.0,
                  ),
                ) : SizedBox(),
                //text box
                Padding(
                  padding: const EdgeInsets.all(28.0),
                  child: Column(
                    children: [
                      Text("Send friendly notification", style: TextStyle(fontSize: 20),),
                      //scanQR()
                      _scanBarcode == "Unknown" ? FlatButton(
                        //add background color blue
                        color: Colors.blue,
                        //give it a border radius
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(30.0)),
                        onPressed: () {
                          scanQR();
                        },
                        child: Text("Scan user QR code",
                            style: TextStyle(color: Colors.white, fontSize: 15)),
                      ) : Text("Device Connected"),
                      SizedBox(
                        height: 20,
                      ),
                      TextField(
                        controller: titleController,
                        decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Title',
                        ),
                      ),
                      SizedBox(
                        height: 20,
                      ),
                      //text box
                      TextField(
                        controller: bodyController,
                        decoration: InputDecoration(
                          border: OutlineInputBorder(),
                          labelText: 'Body',
                        ),
                      ),
                    ],
                  ),
                ),
                //add a button to show the notification
                FlatButton(
                  //add background color blue
                  color: Colors.blue,
                  //give it a border radius
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(30.0)),
                  onPressed: () {
                    //check if _scanBarcode is not equal to unknown
                    if(_scanBarcode != "Unknown" && deviceToken != "" && titleController.text != "" && bodyController.text != ""){
                      //send notification
                      sendServerNotifcation(titleController.text, bodyController.text, _scanBarcode);
                    }else{
                      //show error
                      showDialog(
                        context: context,
                        builder: (BuildContext context) {
                          return AlertDialog(
                            title: Text("Error"),
                            content: Text("Please scan user QR code"),
                            actions: [
                              FlatButton(
                                child: Text("Close"),
                                onPressed: () {
                                  Navigator.of(context).pop();
                                },
                              )
                            ],
                          );
                        },
                      );
                    }
                  },
                  child: Text("Send notification",
                      style: TextStyle(color: Colors.white, fontSize: 15)),
                ),
              ],
            ),
          ),
        ),
      );
  }
}
