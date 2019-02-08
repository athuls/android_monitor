package com.example.androidtheater5;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Build;
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
import examples.numbers1.Numbers1;
import examples.ping.Ping;
import examples.nqueens.Nqueens;
import examples.testapp.TestApp;
import examples.numbers.Numbers;
import salsa.language.UniversalActor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.*;
import java.util.Date;
import java.util.HashMap;
import android.os.Handler;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Random;

import android.content.res.AssetManager;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Calendar;

public class MainActivity extends Activity{
	private final String TAG = "AndroidTheater";
	private long time_init = 0;
	private float last_battery  = (float)1.00;
	private int brightness_val = 0;
	private long old_net = 0;

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

	private Random generator = new Random();
	public TensorFlowInferenceInterface nqueenPredict;
	public double[] feature = new double[25];
	public long[] shape = {1, feature.length};

	public long predictCount = 0;
	public long possiblePred = 0;
	public int numbersCount = 0;
	public int numbers1Count = 0;
	public int modeCount = 0;
	private int initialWaitNqueens = 0;
	private int initialWaitSampleScreen = 0;
	private Thread prev_threadNQ;
	private  Thread prev_threadSc;

	private Handler nqueensHandler;
	private Handler batteryHandler;
	private Handler screenHandler;

	public static Object theaterSyncToken = new Object();
	private Object oneAppSyncToken = new Object();
	private Object oneScreenSyncToken = new Object();

	private void AskPerm(){
		Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
		startActivity(intent);
		return;
	} // to ask for permission PACKAGE_USAGE_STAT
	@TargetApi(Build.VERSION_CODES.M)
	private boolean CheckPerm(){
		AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
		int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
				android.os.Process.myUid(), getPackageName());
		if (mode == AppOpsManager.MODE_ALLOWED) {
			return true;
		}
		return false;
	}
	@TargetApi(Build.VERSION_CODES.M)
	private long getNetworkData(){
		NetworkStatsManager networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
		NetworkStats.Bucket bucket;
		try {
			bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
					"",
					System.currentTimeMillis()-1000,
					System.currentTimeMillis());
		} catch (Exception e) {
			return -1;
		}
		long rcv_bt = bucket.getRxBytes();
		long snt_bt = bucket.getTxBytes();
		return rcv_bt+snt_bt;
	} // Need to ask Atul
	private long getNetworkDataOld(){
		long totalRxBytes = TrafficStats.getTotalRxBytes();
		long totalTxBytes = TrafficStats.getTotalTxBytes();
		return totalRxBytes+totalTxBytes;
	}
	private float readUsage() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();

			String[] toks = load.split(" +");  // Split on one or more spaces

			long idle1 = Long.parseLong(toks[4]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
					+ Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			try {
				Thread.sleep(360);
			} catch (Exception e) {}

			reader.seek(0);
			load = reader.readLine();
			reader.close();

			toks = load.split(" +");

			long idle2 = Long.parseLong(toks[4]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
					+ Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return 0;
	} // reads usage but waits 360 ms, need to fix that

	private String mobileIpAddress = "130.126.152.33";
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
				Numbers1.main(heavy);
			}

			//int randomDelay = generator.nextInt(5001) + 20000;
			synchronized (oneScreenSyncToken) {
				if (numbers1Count > 0) {
					numbers1Count -= 1;
					screenHandler.postDelayed(runnableSampleScreen, 1000);
				}
			}
		}
	};



	private Runnable nqueensWorker = new Runnable() {
		@Override
		public void run() {
			//synchronized (oneScreenSyncToken) {
				Looper.prepare();
				nqueensHandler = new Handler();
				nqueensHandler.postDelayed(runnableNqueens, initialWaitNqueens);
				Looper.loop();
			//}
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
			//synchronized (oneScreenSyncToken) {
				Looper.prepare();
				screenHandler = new Handler();
				screenHandler.postDelayed(runnableSampleScreen, initialWaitSampleScreen);
				Looper.loop();
			//}
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
			//int randomDelay = generator.nextInt(5001) + 20000;
			synchronized (oneScreenSyncToken) {
				//if (numbersCount > 0) {
				//	numbersCount -= 1;
					nqueensHandler.postDelayed(runnableNqueens, 2000);
				//}
			}
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
		if (CheckPerm() == false ) {
			AskPerm();
		}
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
		ContentResolver cResolver = this.getContentResolver();
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float)scale;
		long netVal = 0;
		long currNetVal = 0;
		try {
			//brightness_val = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
			netVal = getNetworkData();
			//netVal = getNetworkDataOld(); // Test for older phones
			currNetVal = netVal - old_net;
		}catch( Exception e){
			System.err.println("Error in brightness");
		}
		HashMap<String, Integer> hashList = UniversalActor.getActiveActors();
		// This is where testApp actor will be called when there is a battery percent drop
//		if(Math.abs(last_battery - batteryPct) > 0.009){
//			last_battery = batteryPct;
//
//			//synchronized (oneScreenSyncToken) {
//				//numbers1Count = generator.nextInt(20) + 50;
//			if(prev_threadSc != null){
//				prev_threadSc.stop();
//			}
//			if (prev_threadNQ != null){
//				prev_threadNQ.stop();
//			}
//				numbers1Count = 30;
//				//numbersCount = generator.nextInt(20) + 50;
//				numbersCount = 30;
//				//initialWaitNqueens = generator.nextInt(25) * 1000;
//				initialWaitNqueens = 10000;
//				modeCount++;
//				if(modeCount /20 == 0) {
//					initialWaitSampleScreen = initialWaitNqueens;
//				} else if(modeCount /20 == 1) {
//					//initialWaitSampleScreen = (numbersCount - (  generator.nextInt(numbersCount -1))) + initialWaitNqueens;
//					initialWaitSampleScreen = initialWaitNqueens+ numbersCount -15;
//				} else {
//					initialWaitSampleScreen = numbersCount + 1 + initialWaitNqueens;
//				}
//
//
//
//				prev_threadNQ = new Thread(nqueensWorker);
//				prev_threadNQ.start();
//				prev_threadSc =  new Thread(screenWorker);
//				prev_threadSc.start();
//			//}
//
//			//Switch the brightness level
//			/*if(brightness_val < 50) brightness_val = 255;
//			else brightness_val = 3;
//			// Call Actor and set its brightness level in appropriate values
//			synchronized (oneAppSyncToken) {
//				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mydip");
//				//System.setProperty("uan", "uan://192.168.0.102:3030/mynqueens");
//
//				// Note that the IP address is the IP address of the smartphone
//				System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mydiploc");
//				System.setProperty("nogc", "theater");
//				String[] newBright = {Integer.toString(brightness_val)};
//				TestApp.main(newBright);
//			}
//*/
//
//
//		}

		// if(initialWait > 0 ) intialWait -= 1;
		Date currentTime = Calendar.getInstance().getTime();
		if(hashList.isEmpty()) {
//			if(brightness_val > 10) {
//				brightness_val = 3;
//				synchronized (oneAppSyncToken) {
//					System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mydip");
//					//System.setProperty("uan", "uan://192.168.0.102:3030/mynqueens");
//
//					// Note that the IP address is the IP address of the smartphone
//					System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mydiploc");
//					System.setProperty("nogc", "theater");
//					String[] newBright = {Integer.toString(brightness_val)};
//					TestApp.main(newBright);
//				}
//			}
			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct +" Brightness "+ currNetVal+ " and no active actors");
			feature[0] += 1;
			// Use battery switch to turn on or off the brightness if empty set low

		}
		else {
//			if(brightness_val < 100) {
//				brightness_val = 255;
//				synchronized (oneAppSyncToken) {
//					System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mydip");
//					//System.setProperty("uan", "uan://192.168.0.102:3030/mynqueens");
//
//					// Note that the IP address is the IP address of the smartphone
//					System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mydiploc");
//					System.setProperty("nogc", "theater");
//					String[] newBright = {Integer.toString(brightness_val)};
//					TestApp.main(newBright);
//				}
//			}
			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct +" Brightness "+ currNetVal+ " actor counts- ");
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

		old_net = netVal;

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

