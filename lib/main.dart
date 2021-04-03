import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_background_service/flutter_background_service.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:fluttertoast/fluttertoast.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  FlutterBackgroundService.initialize(() {
    print("Started");
  });
  // FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
  //     FlutterLocalNotificationsPlugin();
  // const AndroidNotificationDetails androidPlatformChannelSpecifics =
  //     AndroidNotificationDetails(
  //         'your channel id', 'RichIT', 'your channel description',
  //         importance: Importance.max, priority: Priority.high, showWhen: false);
  // const NotificationDetails platformChannelSpecifics =
  //     NotificationDetails(android: androidPlatformChannelSpecifics);
  // await flutterLocalNotificationsPlugin.show(
  //     0, 'plain title', 'plain body', platformChannelSpecifics,
  //     payload: 'item x');

  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  MyApp({Key key}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  FlutterLocalNotificationsPlugin fltNotification;

  @override
  void initState() {
    super.initState();
    var androidInitialize = new AndroidInitializationSettings(defaultIcon);
    getUsagePermission();
    globalService();
    getTimer();
  }

  String globalPackageName;
  static const platform = const MethodChannel("appdetector");
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Text("Creed"),
        ),
      ),
    );
  }

  Future<void> getUsagePermission() async {
    try {
      await platform.invokeMethod("getUsagePermission");
    } on PlatformException catch (e) {
      print(e);
    }
  }

  Future<void> globalService() async {
    try {
      await platform.invokeMethod("globalservice");
    } on PlatformException catch (e) {
      print(e);
    }
  }

  Future<void> getPackageName() async {
    try {
      String packageName = await platform.invokeMethod("packageName");
      setState(() {
        globalPackageName = packageName;
      });
      print("This is packageName: " + globalPackageName);
    } on PlatformException catch (e) {
      print('$e\n');
      print("Failed to get packageName");
    }
  }

  getTimer() {
    return Timer(Duration(seconds: 1), () {
      if (globalPackageName == 'com.android.settings') {
        Fluttertoast.showToast(msg: "You're entering settings");
      }
      getPackageName();

      getTimer();
    });
  }
}
