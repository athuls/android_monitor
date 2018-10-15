package com.example.androidtheater5;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import androidsalsa.resources.AndroidProxy;

import salsa.language.UniversalActor;
import demo_test.Trap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import android.os.Handler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.res.AssetManager;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Calendar;

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

	public TensorFlowInferenceInterface nqueenPredict;
	public double[] feature = new double[25];
	public long[] shape = {1, feature.length};

	public long predictCount = 0;
	public long possiblePred = 0;

	private Handler batteryHandler;
	private Handler trapHandler;

	public static Object theaterSyncToken = new Object();
	private Object oneAppSyncToken = new Object();

	private String mobileIpAddress = "10.194.206.182";

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

	private Runnable trapWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			trapHandler = new Handler();
			trapHandler.post(runnableTrap);
			Looper.loop();
		}
	};

	private Runnable runnableTrap = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// ExSort program

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mytrap");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://"+mobileIpAddress+":4040/mytraploc");


				// Trap program
				String[] args = {"0", "1", "1024", "2", "osl-server1.cs.illinois.edu", mobileIpAddress + ":4040"};
				Trap.main(args);
			}

			trapHandler.postDelayed(runnableTrap, 1300);
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
		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, this.getApplicationContext()));

		nqueenPredict = new TensorFlowInferenceInterface(assetMgr, "nqueens_model.pb");

		System.setProperty( "netif", AndroidTheaterService.NETWORK_INTERFACE);
		System.setProperty( "nodie", "theater" );
		System.setProperty( "port", AndroidTheaterService.THEATER_PORT );
		System.setProperty("output", AndroidTheaterService.STDOUT_CLASS);

		startService(new Intent(MainActivity.this, AndroidTheaterService.class));

		new Thread(trapWorker).start();

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
		debugPrint("onDestroy() is called");
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
		Integer hashListSize = hashList.size();

		Date currentTime = Calendar.getInstance().getTime();
		if(hashList.isEmpty()) {
			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct + " and no active actors");
			feature[0] += 1;
		}
		else {
			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct + " actor counts- ");
			for (String actor : hashList.keySet()) {
				appendLog(actor + ": " + hashList.get(actor) + ", ");
				/////////////////////// PREDICTION MODE ///////////////////////
//				int count = hashList.get(actor).intValue();
//				if(count < 25){
//					feature[count] += 1;
//				}else{
//					feature[25] += 1;
//				}
				/////////////////////// PREDICTION MODE ///////////////////////
			}
			appendLog("\n");
		}

		/////////////////////// PREDICTION MODE ///////////////////////
//		count += 1;
//		if(count % 10 == 0){
//			appendLog("Number of predictions is " + predictCount);
//			debugPrint("Number of predictions is " + predictCount);
//			appendLog("Possible predictions is " + possiblePred);
//			debugPrint("Possible predictions is " + possiblePred);
//		}
//		if(count % 3 == 0){
//			List<Double> featureList = Doubles.asList(feature);
//			possiblePred++;
//			if(!computedLoadCache.contains(featureList)) {
//				nqueenPredict.feed("Placeholder:0", feature, shape); // INPUT_SHAPE is an int[] of expected shape, input is a float[] with the input data
//				String [] output_node = new String[]{"dnn/logits/BiasAdd:0"};
//				nqueenPredict.run(output_node);
//
//				float [] output =  new float[1];
//				nqueenPredict.fetch("dnn/logits/BiasAdd:0", output);
//				// debugPrint(Arrays.toString(output));
//
//				computedLoadCache.add(featureList);
//				feature = new double[25];
//				predictCount += 1;
//			}
//		}


		/////////////////////// PREDICTION MODE ///////////////////////
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

