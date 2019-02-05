package com.example.androidtheater5;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import androidsalsa.resources.AndroidProxy;

//import demo1.Nqueens;
//import demo1.Nqueens2;
//import demo1.Fibonacci;
import examples.nqueens.Nqueens;
import examples.numbers.Numbers;
import examples.numbers1.Numbers1;
import examples.ping.Ping;
import examples.testapp.TestApp;
import salsa.language.UniversalActor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;

import android.os.Handler;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.content.res.AssetManager;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import android.content.Intent;
import java.util.concurrent.ThreadLocalRandom;

//import mltk.core.Instance;
//import mltk.core.Instances;
//import mltk.core.io.InstancesReader;
//import mltk.predictor.Learner;
//import mltk.predictor.evaluation.Evaluator;
//import mltk.predictor.evaluation.RMSE;
//import mltk.predictor.gam.GAM;
//import mltk.predictor.gam.GAMLearner;
//import mltk.predictor.gam.SPLAMLearner;

public class MainActivity extends Activity{
	private final String TAG = "AndroidTheater";

	private ScrollView scrollView = null;
	private TextView textView = null;

	private Handler handler = new Handler();

	private Set<List<Double>> computedLoadCache = new HashSet<>();

	//public static final Object LOCK = new Object();

	public int count = 0;
	public boolean isLight = false;
	public boolean isBreak = false;
	public String[] light = {"7","7", "7"};
	public String[] heavy = {"13","13", "10"};

	public TensorFlowInferenceInterface modelPredict;
	public double[] feature = new double[25];
	public long[] shape = {1, feature.length};

	public long predictCount = 0;
	public long possiblePred = 0;

	private Handler pingHandler;
	private Handler batteryHandler;

	public static Object theaterSyncToken = new Object();
	private Object oneAppSyncToken = new Object();

	private String mobileIpAddress = "10.193.73.90";

	// Training data
	private ArrayList<double[]> trainingInput;
	private ArrayList<Double> trainingLabels;
	private Random generator;
	private String trainingInputFile = "overall_nqueens.txt";
	private String m_model_file = "nqueens_model_original.pb";

	private int brightness_val = 3;

	private Runnable runnableSampleBattery = new Runnable(){
		@Override
		public void run() {
			SampleBattery();
			batteryHandler.postDelayed(runnableSampleBattery, 1000);
		}
	};

	private Runnable batteryWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			batteryHandler = new Handler();
			batteryHandler.post(runnableSampleBattery);
			Looper.loop();
		}
	};

	private Runnable pingWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			pingHandler = new Handler();
			pingHandler.post(runnablePing);
			Looper.loop();
		}
	};

	private Runnable runnablePing = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {"HCSB_full.txt", "uan://osl-server1.cs.illinois.edu:3030/myecho", "uan://osl-server1.cs.illinois.edu:3030/myping"};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc");

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler.postDelayed(runnablePing, 600);
		}

	};

	private void waitUntilTheaterStarted() {
		synchronized (MainActivity.theaterSyncToken) {
			try {
				if(!AndroidTheaterService.theaterCreated) {
					MainActivity.theaterSyncToken.wait();
				}
			} catch (InterruptedException e) {
				System.err.println("Something went wrong waiting for theater to start "  + e);
			}

		}

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		debugPrint("onCreate() is called");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		if (scrollView == null) {
			scrollView = new ScrollView( this );
			textView = new TextView( this );
			scrollView.addView( textView );
			AndroidProxy.setTextViewContext((Activity) this, textView);
		}
		AssetManager assetMgr = this.getAssets();
//		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, this.getApplicationContext()));
		System.err.println("Before creating TF inference");
		try {
			modelPredict = new TensorFlowInferenceInterface(assetMgr, m_model_file);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception is " + e.getStackTrace());
		}
		System.err.println("After creating TF inference");
		System.setProperty("netif", AndroidTheaterService.NETWORK_INTERFACE);
		System.setProperty("nodie", "theater");
		System.setProperty("nogc", "theater");
		System.setProperty("port", AndroidTheaterService.THEATER_PORT);
		System.setProperty("output", AndroidTheaterService.STDOUT_CLASS);

		startService(new Intent(MainActivity.this, AndroidTheaterService.class));
		generator = new Random();
		new Thread(pingWorker).start();
		new Thread(batteryWorker).start();

		readInputArgs();
		new Thread(batteryWorker).start();
	}

	@Override
	protected void onStart() {
		// The activity is about to become visible.
		super.onStart();
		AssetManager assetMgr = this.getAssets();
		debugPrint("onStart() is called");
	}

	@Override
	protected void onResume() {
		// The activity has become visible (it is now "resumed").
		super.onResume();
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
		debugPrint("onDestroy() is called");
	}
	
	protected void debugPrint( String str ) {
		Log.i(TAG, str);
		showTextOnUI(str + "\n");
	}

	private void readInputArgs() {
		AssetManager assetManager = getAssets();
		BufferedReader reader = null;
		trainingInput = new ArrayList<>();
		trainingLabels = new ArrayList<>();
		try {
			reader = new BufferedReader(
					new InputStreamReader(getAssets().open(trainingInputFile)));

			// do reading, usually loop until end of file reading
			String mLine;
			while ((mLine = reader.readLine()) != null) {
				String[] fieldStrings = mLine.split(",");
				// Final field is the label
				double[] fields = new double[fieldStrings.length - 1];
				for (int i = 0; i < fields.length; i++) {
					fields[i] = Double.parseDouble(fieldStrings[i]);
				}

				trainingInput.add(fields);

				// Add the battery drop length/label for this observation
				trainingLabels.add(Double.parseDouble(fieldStrings[fieldStrings.length - 1]));
			}
		} catch (IOException e) {
			// log the exception
			System.err.println("File not found: " + e.toString());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					//log the exception
				}
			}
		}
	}

	protected void SampleBattery() {
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = this.registerReceiver(null, iFilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float)scale;
		HashMap<String, Integer> hashList = UniversalActor.getActiveActors();

		Date currentTime = Calendar.getInstance().getTime();
		if(hashList.isEmpty()) {
			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct + " and no active actors");
			feature[0] += 1;
		}
		else {
			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct + " actor counts- ");
			for (String actor : hashList.keySet()) {
				appendLog(actor + ": " + hashList.get(actor) + ", ");
			}
			appendLog("Brightness:  " + brightness_val + "\n");
		}

	}

	protected void showTextOnUI( final String str ) {
		Activity actObj = (Activity) this;
		actObj.runOnUiThread(new Runnable() {
			public void run() {
				if (scrollView != null) {
					textView.append(str);
					setContentView(scrollView);
				}
			}
		});
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

