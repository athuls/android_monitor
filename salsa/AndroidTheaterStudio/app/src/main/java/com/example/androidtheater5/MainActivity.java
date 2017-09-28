package com.example.androidtheater5;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import androidsalsa.resources.AndroidProxy;
import com.example.androidtheater5.AndroidTheaterService;
import demo1.Nqueens;
import demo1.Fibonacci;
import demo1.HelloWorld;
import salsa.language.UniversalActor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import android.os.Handler;
import java.util.logging.Logger;

public class MainActivity extends Activity{
	private final String TAG = "AndroidTheater";

	private ScrollView scrollView = null;
	private TextView textView = null;

	private Handler handler = new Handler();

	private Runnable runnableBattery = new Runnable(){
		@Override
		public void run() {
			SampleBattery();
			handler.postDelayed(runnableBattery, 1000);
		}
	};
	private Runnable runnableNqueens = new Runnable(){
		@Override
		public void run() {
			String[] args = {"12", "12", "10"};
			Nqueens.main(args);

			String[] args2 = {"50"};
			//Fibonacci.main(args2);
			handler.postDelayed(runnableNqueens, 1000);
		}

	};

	private Runnable runnableFib = new Runnable(){
		@Override
		public void run() {
			String[] args = {"50"};
			Fibonacci.main(args);
			handler.postDelayed(runnableFib, 1000);
		}

	};
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		debugPrint( "onCreate() is called" );
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		if (scrollView == null) {
			scrollView = new ScrollView( this );
			textView = new TextView( this );
			scrollView.addView( textView );
			AndroidProxy.setTextViewContext( (Activity)this, textView );
		}

		startService( new Intent(MainActivity.this, AndroidTheaterService.class) );
		handler.post(runnableNqueens);
		handler.post(runnableFib);
		handler.post(runnableBattery);

	}

	@Override
	protected void onStart() {
		// The activity is about to become visible.
		super.onStart();
		debugPrint( "onStart() is called" );

	}

	@Override
	protected void onResume() {
		// The activity has become visible (it is now "resumed").
		super.onResume();
		//SampleBattery();
		debugPrint( "onResume() is called" );
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to be "paused").
		debugPrint( "onPause() is called" );
	}

	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
		debugPrint( "onStop() is called" );
	}

	@Override
	protected void onDestroy() {
		// The activity is about to be destroyed.
		super.onDestroy();
		debugPrint( "onDestroy() is called" );
	}
	
	protected void debugPrint( String str ) {
		Log.i( TAG, str );
		showTextOnUI( str + "\n" );
	}

	protected void SampleBattery() {
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = this.registerReceiver(null, iFilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float)scale;
		HashMap<String, Integer> hashList = UniversalActor.getActiveActors();
		//debugPrint("Battery level is " + batteryPct);

		if(hashList.isEmpty()) {
			appendLog("Battery level is " + batteryPct + " and no active actors");
		}
		else {
			appendLog("Battery level is " + batteryPct + " actor counts- ");
			for (String actor : hashList.keySet()) {
				appendLog(actor + ": " + hashList.get(actor) + ", ");
			}
			appendLog("\n");
		}
	}

	protected void showTextOnUI( String str ) {
		if (scrollView != null) {
			textView.append( str );
			setContentView( scrollView );
        }
    }

	protected void appendLog(String text)
	{
		File logFile = new File("sdcard/log.txt");
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try
		{
			//BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			buf.append(text);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

