package com.example.androidtheater5;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidsalsa.resources.AndroidProxy;
import examples.exsort.Exp_Starter;
import examples.nqueens.Nqueens;
import examples.ping.Ping;
import salsa.language.UniversalActor;

//import demo1.Nqueens;
//import demo1.Nqueens2;
//import demo1.Fibonacci;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

	private Activity activity;
	private Context m_context;

	public MyExceptionHandler(Activity a, Context context) {
		activity = a;
		m_context = context;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Intent intent = new Intent(activity, MainActivity.class);
		intent.putExtra("crash", true);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(m_context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager mgr = (AlarmManager) m_context.getSystemService(Context.ALARM_SERVICE);
//		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
		activity.finish();
		System.exit(2);
	}
}

