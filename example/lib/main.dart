import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:notification1/notification1.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  Notification1.createNotificationChannel(
      id: "CHAT_MESSAGES",
      name: "Chats",
      description: "This is a notification from Notification1");
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  //text controller
  TextEditingController titleController = TextEditingController();
  TextEditingController bodyController = TextEditingController();

  @override
  void initState() {
    super.initState();
    //requestNotificationPermission
    Notification1.requestNotificationPermission();
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
    return MaterialApp(
      //use system theme
      darkTheme: ThemeData.dark(),
      themeMode: ThemeMode.system,
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            //move content to the center
            mainAxisAlignment: MainAxisAlignment.center,
            //center column contents vertically
            crossAxisAlignment: CrossAxisAlignment.center,
            //add children to the column
            children: [
              Text('Running on: $_platformVersion\n',
                  style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
              FlatButton(
                //add background color blue
                color: Colors.blue,
                //give it a border radius
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(30.0)),
                onPressed: () {
                  initPlatformState();
                },
                child: Text("Show device version",
                    style: TextStyle(color: Colors.white, fontSize: 15)),
              ),
              //requestNotificationPermission
              FlatButton(
                //add background color blue
                color: Colors.blue,
                //give it a border radius
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(30.0)),
                onPressed: () {
                  Notification1.requestNotificationPermission();
                },
                child: Text("Request notification permission",
                    style: TextStyle(color: Colors.white, fontSize: 15)),
              ),
              //text box
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Column(
                  children: [
                    TextField(
                      controller: titleController,
                      decoration: InputDecoration(
                        border: OutlineInputBorder(),
                        labelText: 'Title',
                      ),
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
                  //get the title
                  String title = titleController.text;
                  //get the body
                  String body = bodyController.text;
                  //show notification
                  Notification1.showNotification(
                      title: title, body: body, channelid: "CHAT_MESSAGES");
                },
                child: Text("Show notification",
                    style: TextStyle(color: Colors.white, fontSize: 15)),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
