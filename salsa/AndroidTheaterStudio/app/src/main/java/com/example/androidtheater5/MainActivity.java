package com.example.androidtheater5;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Build;
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
import examples.numbers.Numbers1;
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

import java.io.RandomAccessFile;
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
import android.app.AppOpsManager;


import android.content.res.AssetManager;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import android.content.Intent;
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
	private Handler pingHandler1;
	private Handler pingHandler2;
	private Handler pingHandler3;
	private Handler pingHandler4;
	private Handler pingHandler5;
	private Handler pingHandler6;
	private Handler numsHandler;
	private Handler numsHandler1;
	private Handler numsHandler2;
	private Handler numsHandler3;
	private Handler numsHandler4;
	private Handler numsHandler5;
	private Handler numsHandler6;
	private Handler numsHandler7;
	private Handler numsHandler8;
	private Handler numsHandler9;
	private Handler numsHandler10;


	private String network_data = "";
	private Handler batteryHandler;
	private boolean cpuUsage = false;

	public static Object theaterSyncToken = new Object();
	private Object oneAppSyncToken = new Object();

	////////////////// Hardware resource usage code /////////////////////////////////
	private long old_net = 0;

	private void askPerm(){
		Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
		startActivity(intent);
		return;
	} // to ask for permission PACKAGE_USAGE_STAT

	@TargetApi(Build.VERSION_CODES.M)
	private boolean checkPerm(){
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
// 					0,
					System.currentTimeMillis());
		} catch (Exception e) {
			return -1;
		}
//		long rcv_bt = bucket.getRxBytes();
		long snt_bt = bucket.getTxBytes();
//		return rcv_bt+snt_bt;
//		appendLog("Received bytes is " + bucket.getRxBytes());
		return snt_bt;
	}

	@TargetApi(Build.VERSION_CODES.M)
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
	/////////////////////////////////////////////////////////////////////////////////

	private String mobileIpAddress = "10.194.109.237";

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

	private Runnable pingWorker1 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			pingHandler1 = new Handler();
			pingHandler1.post(runnablePing1);
			Looper.loop();
		}
	};

	private Runnable pingWorker2 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			pingHandler2 = new Handler();
			pingHandler2.post(runnablePing2);
			Looper.loop();
		}
	};

	private Runnable pingWorker3 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			pingHandler3 = new Handler();
			pingHandler3.post(runnablePing3);
			Looper.loop();
		}
	};

	private Runnable pingWorker4 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			pingHandler4 = new Handler();
			pingHandler4.post(runnablePing4);
			Looper.loop();
		}
	};

	private Runnable pingWorker5 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			pingHandler5 = new Handler();
			pingHandler5.post(runnablePing5);
			Looper.loop();
		}
	};

	private Runnable pingWorker6 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			pingHandler6 = new Handler();
			pingHandler6.post(runnablePing6);
			Looper.loop();
		}
	};

	private Runnable numsWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler = new Handler();
			numsHandler.post(runnableNumbers);
			Looper.loop();
		}
	};

	private Runnable numsWorker1 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler1 = new Handler();
			numsHandler1.post(runnableNumbers1);
			Looper.loop();
		}
	};

	private Runnable numsWorker2 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler2 = new Handler();
			numsHandler2.post(runnableNumbers2);
			Looper.loop();
		}
	};

	private Runnable numsWorker3 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler3 = new Handler();
			numsHandler3.post(runnableNumbers3);
			Looper.loop();
		}
	};

	private Runnable numsWorker4 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler4 = new Handler();
			numsHandler4.post(runnableNumbers4);
			Looper.loop();
		}
	};

	private Runnable numsWorker5 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler5 = new Handler();
			numsHandler5.post(runnableNumbers5);
			Looper.loop();
		}
	};

	private Runnable numsWorker6 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler6 = new Handler();
			numsHandler6.post(runnableNumbers6);
			Looper.loop();
		}
	};

	private Runnable numsWorker7 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler7 = new Handler();
			numsHandler7.post(runnableNumbers7);
			Looper.loop();
		}
	};

	private Runnable numsWorker8 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler8 = new Handler();
			numsHandler8.post(runnableNumbers8);
			Looper.loop();
		}
	};

	private Runnable numsWorker9 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler9 = new Handler();
			numsHandler9.post(runnableNumbers9);
			Looper.loop();
		}
	};

	private Runnable numsWorker10 = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler10 = new Handler();
			numsHandler10.post(runnableNumbers10);
			Looper.loop();
		}
	};

	private void read_initial_in(){
		String fileName = "HCSB_full.txt";
		String inputFile = "";
		try {
			InputStream is = Ping.class.getResourceAsStream(fileName);
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			if (in==null) {
				System.err.println("[Custom] The file cannot be found");
			}
			else {
				String line;
				while ((line = in.readLine()) != null) {
					inputFile = inputFile + line;
				}
				in.close();
			}

		} catch (IOException ioe) {
			System.err.println("Ping: [ERROR] Can't open the file "+fileName+" for reading.");
		}

		for (int i = 0; i < 3; i++) {
			network_data += inputFile;
		}
	}

	private Runnable runnablePing = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho", "uan://osl-server1.cs.illinois.edu:3030/myping"};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc");

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler.postDelayed(runnablePing, 1000);
		}

	};

	private Runnable runnablePing1 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho1", "uan://osl-server1.cs.illinois.edu:3030/myping1"};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping1");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc1");

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler1.postDelayed(runnablePing1, 1000);
		}

	};

	private Runnable runnableNumbers = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler.postDelayed(runnableNumbers, 1000);
		}

	};

	private Runnable runnableNumbers1 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers1");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc1");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler1.postDelayed(runnableNumbers1, 1000);
		}

	};

	private Runnable runnableNumbers2 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers2");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc2");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler2.postDelayed(runnableNumbers2, 1000);
		}

	};

	private Runnable runnableNumbers3 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers3");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc3");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler3.postDelayed(runnableNumbers3, 1000);
		}

	};

	private Runnable runnableNumbers4 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers4");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc4");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler4.postDelayed(runnableNumbers4, 1000);
		}

	};

	private Runnable runnableNumbers5 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers5");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc5");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler5.postDelayed(runnableNumbers5, 1000);
		}

	};

	private Runnable runnableNumbers6 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers6");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc6");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler6.postDelayed(runnableNumbers6, 1000);
		}

	};

	private Runnable runnableNumbers7 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers7");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc7");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler7.postDelayed(runnableNumbers7, 1000);
		}

	};

	private Runnable runnableNumbers8 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers8");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc8");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler8.postDelayed(runnableNumbers8, 1000);
		}

	};

	private Runnable runnableNumbers9 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers9");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc9");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler9.postDelayed(runnableNumbers9, 1000);
		}

	};

	private Runnable runnableNumbers10 = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers10");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc10");
				String[] args = {""};
				Numbers1.main(args);
			}

//			numsHandler10.postDelayed(runnableNumbers10, 1000);
		}

	};

	private Runnable runnablePing2 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho2", "uan://osl-server1.cs.illinois.edu:3030/myping2"};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping2");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc2");

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler2.postDelayed(runnablePing2, 1000);
		}

	};

	private Runnable runnablePing3 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho3", "uan://osl-server1.cs.illinois.edu:3030/myping3"};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping3");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc3");

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler3.postDelayed(runnablePing3, 1000);
		}

	};

	private Runnable runnablePing4 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho4", "uan://osl-server1.cs.illinois.edu:3030/myping4"};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping4");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc4");

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler4.postDelayed(runnablePing4, 1000);
		}

	};

	private Runnable runnablePing5 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho5", "uan://osl-server1.cs.illinois.edu:3030/myping5"};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping5");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc5");

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler5.postDelayed(runnablePing5, 1000);
		}

	};

	private Runnable runnablePing6 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho6", "uan://osl-server1.cs.illinois.edu:3030/myping6"};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping6");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc6");

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler6.postDelayed(runnablePing6, 1000);
		}

	};

	private void waitUntilTheaterStarted() {
		synchronized (MainActivity.theaterSyncToken) {
			try {
				if(!AndroidTheaterService.theaterCreated) {
					MainActivity.theaterSyncToken.wait();
				}
			} catch (InterruptedException e) {
				System.err.println("Something went wrong waiting for the theater to start "  + e);
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
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if (scrollView == null) {
			scrollView = new ScrollView( this );
			textView = new TextView( this );
			scrollView.addView( textView );
			scrollView.setKeepScreenOn(true);
			AndroidProxy.setTextViewContext((Activity) this, textView);
		}
		AssetManager assetMgr = this.getAssets();
		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, this.getApplicationContext()));
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
//		System.setProperty("nogc", "theater");
		System.setProperty("port", AndroidTheaterService.THEATER_PORT);
		System.setProperty("output", AndroidTheaterService.STDOUT_CLASS);

		startService(new Intent(MainActivity.this, AndroidTheaterService.class));

		if(checkPerm() == false) {
			askPerm();
		}

		generator = new Random();

		cpuUsage = false;
//		new Thread(numsWorker).start();
//		new Thread(numsWorker1).start();
//		new Thread(numsWorker2).start();
//		new Thread(numsWorker3).start();
//		new Thread(numsWorker4).start();
//		new Thread(numsWorker5).start();
//		new Thread(numsWorker6).start();
//		new Thread(numsWorker7).start();
//		new Thread(numsWorker8).start();
//		new Thread(numsWorker9).start();
//		new Thread(numsWorker10).start();

		read_initial_in();

		// Reduce the size of the network data
//		int mid_network_data = network_data.length()/2;
//		network_data = network_data.substring(0, mid_network_data);
		// Reduce the size of the network data

		new Thread(pingWorker).start();
		new Thread(pingWorker1).start();
		new Thread(pingWorker2).start();
//		new Thread(pingWorker3).start();
//		new Thread(pingWorker4).start();
//		new Thread(pingWorker5).start();
//		new Thread(pingWorker6).start();
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
		debugPrint("onResume() is called");
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

		///////////////// Hardware resource usage code ////////////////////////////////
		ContentResolver cResolver = this.getContentResolver();
		long netVal = 0;
		long currNetVal = 0;
		try {
			//brightness_val = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
			netVal = getNetworkData();
			currNetVal = netVal - old_net;
			appendLog("Network usage: " + currNetVal);
		}catch( Exception e){
			System.err.println("Error in network reading");
		}

		if(cpuUsage) {
			try {
				double cpuUsageVal = readUsage();
				appendLog("CPU usage: " + cpuUsageVal);
			} catch (Exception e) {
				System.err.println("Error in CPU reading");
			}
		}
		///////////////////////////////////////////////////////////////////////////////

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
			appendLog("\n");
		}

		old_net = netVal;
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

