package com.example.androidtheater5;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ContentResolver;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;
import androidsalsa.resources.AndroidProxy;

//import demo1.Nqueens;
//import demo1.Nqueens2;
//import demo1.Fibonacci;
import demo_test.Trap;
import examples.Heat.DistributedHeat;
import examples.exsort.Exp_Starter;
import examples.ping.Ping;
import examples.nqueens.Nqueens;
import examples.testapp.TestApp;
import examples.numbers.Numbers;
import salsa.language.UniversalActor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;
import java.util.Date;
import java.util.HashMap;
import android.os.Handler;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


import android.content.res.AssetManager;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Calendar;

public class MainActivity extends Activity{
	private final String TAG = "AndroidTheater";
	private long time_init = 0;
	private float last_battery  = (float)1.00;
	private int brightness_val = 3;

	private ScrollView scrollView = null;
	private TextView textView = null;

	private Handler handler = new Handler();

	private Set<List<Double>> computedLoadCache = new HashSet<>();

	//public static final Object LOCK = new Object();

	public int count = 0;
	public boolean isLight = false;
	public boolean isBreak = false;
	public String[] light = {"7","7", "7"};
	public String[] heavy = {"13","13","10"};
	public String[] appTime = {"10000"};

	public TensorFlowInferenceInterface nqueenPredict;
	public double[] feature = new double[25];
	public long[] shape = {1, feature.length};

	public long predictCount = 0;
	public long possiblePred = 0;

	private Handler nqueensHandler;
	private Handler batteryHandler;
	private Handler screenHandler;

	public static Object theaterSyncToken = new Object();
	private Object oneAppSyncToken = new Object();
	private Object oneScreenSyncToken = new Object();

	private String mobileIpAddress = "10.194.109.237";

	private Runnable runnableSampleBattery = new Runnable(){
		@Override
		public void run() {
			SampleBattery();
			batteryHandler.postDelayed(runnableSampleBattery, 1000);
		}
	};
	
	private Runnable runnableSampleScreen = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mydip");
				//System.setProperty("uan", "uan://192.168.0.102:3030/mynqueens");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mydiploc");
				System.setProperty("nogc", "theater");
				TestApp.main(appTime);
			}
			screenHandler.postDelayed(runnableSampleScreen, 20000);
		}
	};



	private Runnable nqueensWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			nqueensHandler = new Handler();
			nqueensHandler.post(runnableNqueens);
			Looper.loop();
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

	private Runnable screenWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			screenHandler = new Handler();
			screenHandler.post(runnableSampleScreen);
			Looper.loop();
		}
	};

	private Runnable runnableNqueens = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynqueens");
				//System.setProperty("uan", "uan://192.168.0.102:3030/mynqueens");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mynqueensloc");
				System.setProperty("nogc", "theater");
				Numbers.main(heavy);

			}
			time_init += 2000;
			if(time_init >= 600000){
				time_init = 0;

					throw new RuntimeException();



			}
			nqueensHandler.postDelayed(runnableNqueens, 1000);
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
		MyExceptionHandler exp = new MyExceptionHandler(this, this.getApplicationContext());
		Thread.setDefaultUncaughtExceptionHandler(exp);

		nqueenPredict = new TensorFlowInferenceInterface(assetMgr, "nqueens_model.pb");

		System.setProperty( "netif", AndroidTheaterService.NETWORK_INTERFACE);
		System.setProperty( "nodie", "theater" );
		System.setProperty("nogc", "theater");
		System.setProperty("port", AndroidTheaterService.THEATER_PORT);
		System.setProperty("output", AndroidTheaterService.STDOUT_CLASS);

		startService(new Intent(MainActivity.this, AndroidTheaterService.class));

		Thread qn =  new Thread(nqueensWorker);
		qn.setUncaughtExceptionHandler(exp);
		qn.start();

		new Thread(batteryWorker).start();
		//SampleScreen();
		//Thread tap =  new Thread(screenWorker);
		//tap.setUncaughtExceptionHandler(exp);
		//tap.start();
		//new Thread(screenWorker).start();


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

	protected void SampleScreen() {
		// Get the context
		ContentResolver cResolver = this.getContentResolver();
//Window object, that will store a reference to the current window
		Window window = this.getWindow();
		int brightness;
		try{
               // To handle the auto
                Settings.System.putInt(cResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
 //Get the current system brightness
                brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                //Throw an error case it couldn't be retrieved
                Log.e("Error", "Cannot access system brightness");
                e.printStackTrace();
            }
          brightness = 3;
          Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
            //Get the current window attributes
          LayoutParams layoutpars = window.getAttributes();
            //Set the brightness of this window
          layoutpars.screenBrightness = brightness / (float)255;
		System.err.println("Starting Down Switch ");
            //Apply attribute changes to this window
          window.setAttributes(layoutpars);
		System.err.println("Finished Down Switch Part 1");
          try {
			  Thread.sleep(20000);
		  }catch(Exception e){

		  }
		  System.err.println("Just after sleep");
          brightness = 255;
          Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
		System.err.println("Setting the brightness");
            //Get the current window attributes
         layoutpars = window.getAttributes();
            //Set the brightness of this window
          layoutpars.screenBrightness = brightness / (float)255;
		System.err.println("Assigning the parameter");
            //Apply attribute changes to this window
          window.setAttributes(layoutpars);
		System.err.println("Finished Up Switch");
		try {
			Thread.sleep(20000);
		}catch(Exception e){

		}


		
	}

	protected void SampleBattery() {
		IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = this.registerReceiver(null, iFilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float)scale;
		HashMap<String, Integer> hashList = UniversalActor.getActiveActors();
		// This is where testApp actor will be called when there is a battery percent drop
		if(Math.abs(last_battery - batteryPct) > 0.01){
			last_battery = batteryPct;
			//Switch the brightness level
			if(brightness_val < 50) brightness_val = 255;
			else brightness_val = 3;
			// Call Actor and set its brightness level in appropriate values
			synchronized (oneAppSyncToken) {
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mydip");
				//System.setProperty("uan", "uan://192.168.0.102:3030/mynqueens");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mydiploc");
				System.setProperty("nogc", "theater");
				String[] newBright = {Integer.toString(brightness_val)};
				TestApp.main(newBright);
			}


		}

		Date currentTime = Calendar.getInstance().getTime();
		if(hashList.isEmpty()) {
			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct + " and no active actors"+" Brightness "+ brightness_val);
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
			appendLog(" Brightness "+ brightness_val +"\n");
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

