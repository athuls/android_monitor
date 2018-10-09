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

//import demo1.Nqueens;
//import demo1.Nqueens2;
//import demo1.Fibonacci;
import demo_test.Trap;
import examples.Heat.DistributedHeat;
import examples.exsort.Exp_Starter;
import examples.ping.Ping;
import examples.nqueens.Nqueens;
import salsa.language.UniversalActor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import android.os.Handler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.res.AssetManager;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


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



//	private Runnable runnableBattery = new Runnable(){
//		@Override
//		public void run() {
//			synchronized (LOCK) {
//				try {
//					Thread t = new Thread(runnableSampleBattery);
//					t.start();
//					LOCK.wait();
//					handler.postDelayed(runnableBattery, 1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//
//
//		}
//	};

	private Runnable runnableSampleBattery = new Runnable(){
		@Override
		public void run() {
			SampleBattery();
//			count = (count+1)%1800;
//			if(count == 0) {
//				isLight = !isLight;
//				isBreak = true;
//			}
//			if(count == 30 && isBreak){
//				isBreak = false;
//				count = 0;
//			}
			handler.postDelayed(runnableSampleBattery, 1000);
		}
	};

	private Runnable runnableNqueens = new Runnable(){
		@Override
		public void run() {
			Nqueens.main(heavy);
			handler.postDelayed(runnableNqueens, 750);
		}

	};

//	private Runnable runnableFib = new Runnable(){
//		@Override
//		public void run() {
//			String[] args = {"10"};
//			Fibonacci.main(args);
//			handler.postDelayed(runnableFib, 1000);
//		}
//
//	};

//	private Runnable runnableTrap = new Runnable(){
//		@Override
//		public void run() {
//			// Trap program
//			String[] args = {"0", "1", "1024", "2", "10.195.15.219", "localhost:4040"};
//			Trap.main(args);
//
//			handler.postDelayed(runnableTrap, 2500);
//		}
//
//	};

//	private Runnable runnableHeat = new Runnable(){
//		@Override
//		public void run() {
//			// Heat program
//			// Create an output file first
//			String outputFile = "sdcard/log.txt";
//			File logFile = new File(outputFile);
//			if (!logFile.exists())
//			{
//				try
//				{
//					logFile.createNewFile();
//				}
//				catch (IOException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//			String[] args = {"100", "120", "120", "2", "128.174.244.87", "localhost:4040,128.174.244.87:5050", outputFile};
//			DistributedHeat.main(args);
//
////			handler.postDelayed(runnableHeat, 2500);
//		}
//
//	};

	private Runnable runnablePing = new Runnable(){
		@Override
		public void run() {
			// Ping program
			// The host name osl-server1.cs.illinois.edu is where the nameserver is running
			String[] args = {"HCSB_full.txt", "uan://osl-server1.cs.illinois.edu:3030/myecho", "uan://osl-server1.cs.illinois.edu:3030/myping"};

			// The host name osl-server1.cs.illinois.edu is where the nameserver is running
			System.setProperty( "uan", "uan://osl-server1.cs.illinois.edu:3030/myping" );

			// Note that the IP address is the IP address of the smartphone
			System.setProperty( "ual", "rmsp://192.17.148.55:4040/mypingloc" );


			System.clearProperty("netif");
			System.clearProperty("port");
			System.clearProperty("nodie");
			Ping.main(args);

			handler.postDelayed(runnablePing, 6000);
		}

	};

	private Runnable runnableExsort = new Runnable(){
		@Override
		public void run() {
			// ExSort program
			// The first argument is the nameserver URL.
			// The second, third and final arguments contain the URLs for the Android theater. So the IP address in the URL should be replaced
			// with the IP address of the ANdroid phone
			String[] args = {"uan://osl-server1.cs.illinois.edu:3030/", "rmsp://192.17.150.98:4040/", "rmsp://192.17.150.98:4040/",
			"2", "10", "big.txt", "big_out.txt", "report_on", "rmsp://192.17.150.98:4040/"};
			Exp_Starter.main(args);

			handler.postDelayed(runnableExsort, 3000);
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
		AssetManager assetMgr = this.getAssets();
		nqueenPredict = new TensorFlowInferenceInterface(assetMgr, "nqueens_model.pb");

		startService(new Intent(MainActivity.this, AndroidTheaterService.class));
		handler.post(runnablePing);
//		handler.post(runnableNqueens);
		handler.post(runnableSampleBattery);
	}

	@Override
	protected void onStart() {
		// The activity is about to become visible.
		super.onStart();
		AssetManager assetMgr = this.getAssets();
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
		appendLog("Number of predictions is " + count);
		// The activity is no longer visible (it is now "stopped")
		debugPrint( "onStop() is called" );
	}

	@Override
	protected void onDestroy() {
		// The activity is about to be destroyed.
		super.onDestroy();
		appendLog("Number of predictions is " + count);
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
//		debugPrint(hashList.toString());
		Integer hashListSize = hashList.size();
//		debugPrint(hashListSize.toString());

		if(hashList.isEmpty()) {
			appendLog("Battery level is " + batteryPct + " and no active actors");
			feature[0] += 1;
		}
		else {
			appendLog("Battery level is " + batteryPct + " actor counts- ");
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

