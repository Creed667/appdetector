package com.example.appdetector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "appdetector";
    String TAG = this.getClass().getSimpleName();


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.drawable.launch_background);
//
//        //
//        // Dart needed
//        GlobalMethods.startService(this, CreedsService.class);
//        GlobalMethods.startAlarmManager(this);
//
//        //
//        // Dart needed
//        GlobalMethods.ignoreBatteryOptimization(MainActivity.this, 123);
//
//        //
//        // Dart needed
//        if (!GlobalMethods.isUsagedStatPermissionAccepted(this)) {
//            GlobalMethods.getUsageAccess(this, 234);
//        }
//    }

    
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if(call.method.equals("getUsagePermission")){
                                if(!GlobalMethods.isUsagedStatPermissionAccepted(this)){
                                    GlobalMethods.ignoreBatteryOptimization(MainActivity.this, 123);
                                    GlobalMethods.getUsageAccess(this,234);

                                }
                            }
                            else if (call.method.equals("globalservice")) {
                                GlobalMethods.startService(this, CreedsService.class);
                                GlobalMethods.startAlarmManager(this);

                            }

                            else if (call.method.equals("packageName")) {
                                result.success(GlobalVariables.packageNames);
                            }
                        }
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode);

        if (requestCode == 123) {
            if (!GlobalMethods.isIgnoreBatteryOptEnabled(this)) {
                GlobalMethods.ignoreBatteryOptimization(MainActivity.this, 123);
                // If not, get permission again
                // Dart needed
            }
        }

        if (requestCode == 234) {
            if (!GlobalMethods.isUsagedStatPermissionAccepted(this)) {
                // If not, get permission again
                GlobalMethods.getUsageAccess(this,234);
                // Dart needed

            }
        }
    }
}
