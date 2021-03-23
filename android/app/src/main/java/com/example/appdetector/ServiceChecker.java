package com.example.appdetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ServiceChecker extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String type = intent.getStringExtra(GlobalVariables.typeStr);
            if (type.contentEquals(GlobalVariables.receiverExtraType)) {
                GlobalMethods.startService(context, CreedsService.class);
                GlobalMethods.startAlarmManager(context);
            }
        }
    }
}
