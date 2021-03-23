package com.example.appdetector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.List;

public class GlobalMethods {
    private static final int period = 15 * 1000; //15 minutes;

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void startService(Context context, Class<?> serviceClass) {
        if (!isServiceRunning(context, serviceClass)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundServices(context, serviceClass);
            } else {
                context.startService(new Intent(context, serviceClass));
            }
        }
    }

    public void stopService(Context context, Class<?> serviceClass) {
        if (isServiceRunning(context, serviceClass))
            context.stopService(new Intent(context, serviceClass));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startForegroundServices(Context context, Class<?> serviceClass) {
        context.startForegroundService(new Intent(context, serviceClass));
    }

    public static void ignoreBatteryOptimization(Context context, int requestCode) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(context.getPackageName())) {
                @SuppressLint("BatteryLife")
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                ((Activity) context).startActivityForResult(intent, requestCode);
            }
        }
    }

    public static boolean isIgnoreBatteryOptEnabled(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }

        return true;
    }

    public static void getUsageAccess(Context context, int requestCode) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && isUsageStatsOptionAvailable(context)) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
    }

    public static boolean isUsagedStatPermissionAccepted(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName);
                return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) == AppOpsManager.MODE_ALLOWED;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean isUsageStatsOptionAvailable(@NonNull Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }
        return false;
    }

    public static void startAlarmManager(Context context) {
        Intent intent = new Intent(context, ServiceChecker.class);
        intent.putExtra(GlobalVariables.typeStr, GlobalVariables.receiverExtraType);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 95374, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + period, pendingIntent);

    }

    public static void stopAlarmManager(Context context) {
        Intent intent = new Intent(context, ServiceChecker.class);
        intent.putExtra(GlobalVariables.typeStr, GlobalVariables.receiverExtraType);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 95374, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static Handler handler = new Handler();
    private static Runnable mRunnable;

    public static void doSomethingAfter(double seconds, Runnable runnable) {
        handler.removeCallbacks(mRunnable);
        mRunnable = runnable;
        handler.postDelayed(runnable, (long) (seconds * 1000));
    }
}
