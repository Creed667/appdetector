import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';

void main() {
  runApp(MyApp());

  WidgetsBinding.instance
      .addObserver(LifecycleEventHandler(detachedCallBack: () {
    print("Detached");
  }, resumeCallBack: () {
    print("Resumed");
  }));
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
    return Timer(Duration(seconds: 3), () {
      getPackageName();
      if (globalPackageName == 'com.android.settings') {
        print("Settings");
        Fluttertoast.showToast(msg: "You're entering settings");
      }
      getTimer();
    });
  }
}

class LifecycleEventHandler extends WidgetsBindingObserver {
  LifecycleEventHandler({this.resumeCallBack, this.detachedCallBack});

  var resumeCallBack;
  var detachedCallBack;

//  @override
//  Future<bool> didPopRoute()

//  @override
//  void didHaveMemoryPressure()

  @override
  Future<void> didChangeAppLifecycleState(AppLifecycleState state) async {
    switch (state) {
      case AppLifecycleState.inactive:
      case AppLifecycleState.paused:
      case AppLifecycleState.detached:
        await detachedCallBack();
        break;
      case AppLifecycleState.resumed:
        await resumeCallBack();
        break;
    }
  }

//  @override
//  void didChangeLocale(Locale locale)

//  @override
//  void didChangeTextScaleFactor()

//  @override
//  void didChangeMetrics();

//  @override
//  Future<bool> didPushRoute(String route)
}
