package com.example.androidtheater5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryBroadcastReceiver extends BroadcastReceiver {
    private final static String BATTERY_LEVEL = "level";

    public volatile int currentLevel;

    @Override
    public void onReceive(Context context, Intent intent) {
        currentLevel = intent.getIntExtra(BATTERY_LEVEL, 0);
    }
}