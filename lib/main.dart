import 'dart:async';

import 'package:appdetector/context.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  MyApp({Key key}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
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
    return Timer(Duration(seconds: 3), () {
      getPackageName();
      if (globalPackageName == 'com.android.settings') {
        Fluttertoast.showToast(msg: "You're entering settings");
      }
      getTimer();
    });
  }
}
