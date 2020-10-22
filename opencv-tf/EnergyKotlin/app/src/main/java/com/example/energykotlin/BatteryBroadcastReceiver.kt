package com.example.energykotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BatteryBroadcastReceiver : BroadcastReceiver() {
    @Volatile
    var currentLevel = 0

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        currentLevel = intent.getIntExtra(BATTERY_LEVEL, 0)
    }

    companion object {
        private const val BATTERY_LEVEL = "level"
    }
}