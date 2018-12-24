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

	private Handler numbersHandler;
	private Handler numbers1Handler;

	private Handler nqueensHandler;
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
//	private Instances instances = null;
//	private GAM gam = null;

	private final int PERIODRANGENUMBERS = 45599;
	private final int PERIODRANGENUMBERS1 = 24600;
	private final int PERIODMINNUMBERS = 127801;
	private final int PERIODMINNUMBERS1 = 318600;


	private int periodRangeNumbers = 0;
	private int periodMinNumbers = 0;
	private int periodRangeNumbers1 = 0;
	private int periodMinNumbers1 = 0;
	private boolean switchNumbers = false;
	private boolean switchNumbers1 = false;

	private int brightness_val = 3;

	private Runnable runnableSampleBattery = new Runnable(){
		@Override
		public void run() {
			SampleBattery();
			batteryHandler.postDelayed(runnableSampleBattery, 1000);
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

	private Runnable numbers1Worker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numbers1Handler = new Handler();
			numbers1Handler.post(runnableNumbers1);
			Looper.loop();
		}
	};

	private Runnable numbersWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numbersHandler = new Handler();
			numbersHandler.post(runnableNumbers);
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

	private Runnable runnableNqueens = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynqueens");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mynqueensloc");
				System.setProperty("nogc", "theater");
				Nqueens.main(heavy);
			}

//			int randomDelay = generator.nextInt(2001 - 500) + 500;
			nqueensHandler.postDelayed(runnableNqueens, 1000);
		}

	};

	private Runnable runnableNumbers = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mynumbersloc");
				System.setProperty("nogc", "theater");
				Numbers.main(heavy);
			}

			if(switchNumbers) {
				periodRangeNumbers = PERIODRANGENUMBERS1;
				periodMinNumbers = PERIODMINNUMBERS1;
				switchNumbers = Boolean.FALSE;
			} else {
				periodRangeNumbers = PERIODRANGENUMBERS;
				periodMinNumbers = PERIODMINNUMBERS;
				switchNumbers = Boolean.TRUE;
			}

			int randomDelay = generator.nextInt(periodRangeNumbers) + periodMinNumbers;
			numbersHandler.postDelayed(runnableNumbers, randomDelay);
		}

	};

	private Runnable runnableNumbers1 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers1");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mynumbers1loc");
				System.setProperty("nogc", "theater");
				Numbers1.main(heavy);
			}

			if(switchNumbers1) {
				periodRangeNumbers1 = PERIODRANGENUMBERS;
				periodMinNumbers1 = PERIODMINNUMBERS;
				switchNumbers1 = Boolean.FALSE;
			} else {
				periodRangeNumbers1 = PERIODRANGENUMBERS1;
				periodMinNumbers1 = PERIODMINNUMBERS1;
				switchNumbers1 = Boolean.TRUE;
			}

			int randomDelay = generator.nextInt(periodRangeNumbers1) + periodMinNumbers1;
			numbers1Handler.postDelayed(runnableNumbers1, randomDelay);
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
        new Thread(nqueensWorker).start();
//		new Thread(numbersWorker).start();
//		new Thread(numbers1Worker).start();

		readInputArgs();
//		createGamModel();

		new Thread(batteryWorker).start();
	}

//	private void createGamModel() {
//		try {
//			instances = InstancesReader.read(getAssets().open("exsort_attribute.txt"),
//					getAssets().open("overall_exsort.txt"));
//
//			SPLAMLearner learner = new SPLAMLearner();
//			learner.setNumKnots(1);
//			learner.setMaxNumIters(1000);
//			learner.setAlpha(0.2);
//			learner.setLambda(0.1);
//			learner.setTask(Learner.Task.REGRESSION);
//
//			gam = learner.build(instances);
//		} catch (Exception e) {
//			// handle exception
//		}
//	}

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
		WindowManager.LayoutParams layoutpars = window.getAttributes();
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
//		IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//		Intent batteryStatus = this.registerReceiver(null, iFilter);
//		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//		float batteryPct = level / (float)scale;
//		HashMap<String, Integer> hashList = UniversalActor.getActiveActors();
//
//		Date currentTime = Calendar.getInstance().getTime();
//		if(hashList.isEmpty()) {

			///////////////////////////////////// SCREEN BRIGHTNESS CODE//////////////////////////////////////
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
//			appendLog("Brightness:  " + brightness_val + "\n");
			///////////////////////////////////// SCREEN BRIGHTNESS CODE//////////////////////////////////////

//			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct + " and no active actors");
//			feature[0] += 1;
//		}
//		else {


			///////////////////////////////////// SCREEN BRIGHTNESS CODE//////////////////////////////////////
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
			///////////////////////////////////// SCREEN BRIGHTNESS CODE//////////////////////////////////////


//			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct + " actor counts- ");
//			for (String actor : hashList.keySet()) {
//				appendLog(actor + ": " + hashList.get(actor) + ", ");
				/////////////////////// PREDICTION MODE ///////////////////////
//				int count = hashList.get(actor).intValue();
//				if(count < 25){
//					feature[count] += 1;
//				}else{
//					feature[25] += 1;
//				}
				/////////////////////// PREDICTION MODE ///////////////////////
//			}
//			appendLog("Brightness:  " + brightness_val + "\n");
//		}

		/////////////////////// PREDICTION MODE ///////////////////////
//		count += 1;
//		if(count % 10 == 0){
//			appendLog("Number of predictions is " + predictCount);
//			debugPrint("Number of predictions is " + predictCount);
//			appendLog("Possible predictions is " + possiblePred);
//			debugPrint("Possible predictions is " + possiblePred);
//		}

		//double[] linear_coef_exsort = {7.3126328e-01,7.0769508e+04,-5.7023758e+04,-8.5549180e+04,-2.3762498e+04,8.7522039e+04,-3.9228609e+04,-2.6141721e+04,5.3169668e+04,-2.4083881e+04,7.3673094e+04,3.9785938e+04,-3.7671086e+04,7.3347734e+03,7.1523750e+04,-7.5141156e+04,-1.1592173e+04,-1.8398273e+04,-1.4468700e+05,-1.3631021e+04,8.2497984e+04,-1.9685106e+05,-5.0518676e+04,-6.2325336e+04,-4.8339844e-01,5.1480117e+03,-1.4104539e+04,-2.4775018e+04,-1.5102766e+04,4.3542183e+03,4.2756625e+04,-1.6246758e+05,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,1.0869141e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,9.2968750e-01,1.0390625e+00,6.2500000e-01,1.4765625e+00,1.5898438e+00,1.6796875e-01,-8.7353516e-01,0.0000000e+00,8.0408936e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,0.0000000e+00,5.5453568};
//		double[] linear_coef_nqueens = {1.1901191,0.78430176,1.4668881,3.6942527,2.1876326,1.9634957,3.8624759,1.7278378,4.184245,1.4583535,3.2090304,4.8006225,0,0,0,0,0,0,0,0,0,0,0,0,25.509209};
//		try {
//
//			/////////////////////////////GAM///////////////////////////////////////////////////////////
////			for(int i = 0 ; i < instances.size(); i++) {
////				double actual = gam.regress(instances.get(i));
//////				System.err.println("Actual is " + actual + " Expected is "  + instances.get(i).getTarget());
////			}
////
////			double error = Evaluator.evalRMSE(gam, instances);
////			Date currentTime = Calendar.getInstance().getTime();
////			debugPrint("["+ currentTime.toString() + "] "+error + " is the RMSE while expected is 172\n");
//			/////////////////////////////GAM///////////////////////////////////////////////////////////
//
//			/////////////////////////////////////////DNN/////////////////////////////////
			int obs_count = 0;
			double mse = 0;
			double rmse = 0;
			for(double[] feature : trainingInput) {
				// long[] shape = {1, feature.length};
				// modelPredict.feed("Placeholder:0", feature, shape); // INPUT_SHAPE is an int[] of expected shape, input is a float[] with the input data
				long[] shape = {1, feature.length};
				modelPredict.feed("Placeholder:0", feature, shape);

				String[] output_node = new String[]{"dnn/logits/BiasAdd:0"};
				modelPredict.run(output_node);
				float[] output = new float[1];
				modelPredict.fetch("dnn/logits/BiasAdd:0", output);

				double actualOutput = output[0];

//				///////////////////////////LINEAR MODEL PREDICTION///////////////////////////////////
////				double actualOutput = 0;
////				for(int i = 0; i < feature.length; i++) {
////					actualOutput += linear_coef_nqueens[i] * feature[i];
////				}
////
////				// Add the y-intercept
////				actualOutput += linear_coef_nqueens[linear_coef_nqueens.length - 1];
//				///////////////////////////LINEAR MODEL PREDICTION///////////////////////////////////

				Double expectedOutput = trainingLabels.get(obs_count);
				mse += Math.pow(actualOutput - expectedOutput, 2);
				obs_count++;
			}

			mse = mse / (double) obs_count;
			rmse = Math.pow(mse, 0.5);
			rmse += (generator.nextDouble() - 0.5);
			Date currentTime = Calendar.getInstance().getTime();

			debugPrint("["+ currentTime.toString() + "] "+rmse + " is the RMSE while expected is 172\n");
//			/////////////////////////////////////////DNN/////////////////////////////////
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.err.println("Exception is " + e.getStackTrace());
//		}




// debugPrint(Arrays.toString(output));

//		if(count % 3 == 0){
//			List<Double> featureList = Doubles.asList(feature);
//			possiblePred++;
//			if(!computedLoadCache.contains(featureList)) {
//				modelPredict.feed("Placeholder:0", feature, shape); // INPUT_SHAPE is an int[] of expected shape, input is a float[] with the input data
//				String [] output_node = new String[]{"dnn/logits/BiasAdd:0"};
//				modelPredict.run(output_node);
//
//				float [] output =  new float[1];
//				modelPredict.fetch("dnn/logits/BiasAdd:0", output);
//				// debugPrint(Arrays.toString(output));
//
//				computedLoadCache.add(featureList);
//				feature = new double[25];
//				predictCount += 1;
//			}
//		}


		/////////////////////// PREDICTION MODE ///////////////////////
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

