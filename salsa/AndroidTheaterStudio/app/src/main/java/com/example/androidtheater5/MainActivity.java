package com.example.androidtheater5;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import androidsalsa.resources.AndroidProxy;

//import demo1.Nqueens;
//import demo1.Nqueens2;
//import demo1.Fibonacci;
import examples.fibonacci.Fibonacci;
import examples.numbers.Numbers;
import examples.numbers.Numbers1;
import examples.numbers.SequentialNumbers;
import examples.numbers.UniqueNumbers;
import examples.ping.Ping;
import examples.ping.PingReader;
import examples.numbersDebugVarWld.*;
import salsa.language.UniversalActor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.RandomAccessFile;
import java.util.ArrayList;

import android.os.Handler;

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

	private String mobileIpAddress = "10.195.212.37";

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
	public String[] heavy = {"13","13", "10", "uan://osl-server1.cs.illinois.edu:3030",
							"rmsp://"+mobileIpAddress+":4040"};
	public String[] medium = {"13","7", "10", "uan://osl-server1.cs.illinois.edu:3030",
							"rmsp://"+mobileIpAddress+":4040"};

	public TensorFlowInferenceInterface modelPredict;
	public double[] feature = new double[25];
	public long[] shape = {1, feature.length};

	public long predictCount = 0;
	public long possiblePred = 0;

	private Handler pingHandler;
	private Handler pingReaderHandler;
	private Handler pingHandler1;
	private Handler pingHandler2;
	private Handler pingHandler3;
	private Handler pingHandler4;
	private Handler pingHandler5;
	private Handler pingHandler6;

	private Handler numbersFdHandler;
	private Handler numbersIdleHandler;

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
	private Handler seqNumsHandler;
	private Handler uniqNumsHandler;
	private Handler fibHandler;

	private BroadcastReceiver mReceiver;

	private String network_data = "";
	private Handler batteryHandler;
	private boolean cpuUsage = false;

	public static Object theaterSyncToken = new Object();
	private Object oneAppSyncToken = new Object();

	////////////////// Hardware resource usage code /////////////////////////////////
	private long old_net = 0;
	private CpuUsage previousCpuUsage=null;
	private double currentCpu0Freq;
	private double currentCpu1Freq;
	NetworkStatsManager networkStatsManager;

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

	private long getNetworkDataOld(){
//		long totalRxBytes = TrafficStats.getTotalRxBytes();
		long totalTxBytes = TrafficStats.getTotalTxBytes();
//		return totalRxBytes+totalTxBytes;
		return totalTxBytes;
	}

	@TargetApi(Build.VERSION_CODES.M)
	private long getNetworkData(){
		NetworkStatsManager networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(
				Context.NETWORK_STATS_SERVICE);
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
	private void initializeNetworkStats() {
		networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(
				Context.NETWORK_STATS_SERVICE);
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

	@TargetApi(Build.VERSION_CODES.M)
	private double readUsageActual() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", "r");
			currentCpu0Freq = Double.parseDouble(reader.readLine().split(" +")[0]);
			reader = new RandomAccessFile("/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq", "r");
			currentCpu1Freq = Double.parseDouble(reader.readLine().split(" +")[0]);

			int pid = android.os.Process.myPid();
			RandomAccessFile uptimeReader = new RandomAccessFile("/proc/uptime", "r");
			reader = new RandomAccessFile("/proc/"+ pid +"/stat", "r");
			String loadUptime = uptimeReader.readLine();
			String load = reader.readLine();

			String[] toks = load.split(" +");  // Split on one or more spaces
			String[] toksUptime = loadUptime.split(" +");

			double upTime = Double.parseDouble(toksUptime[0]);
			double total_time = Double.parseDouble(toks[13]) + Double.parseDouble(toks[14])
					+ Double.parseDouble(toks[15]) + Double.parseDouble(toks[16]);
			double startTime = Double.parseDouble(toks[21]);

			// Note that we are fixing the frequency to 100 which seems to be fixed for device side calculation
			double timeSinceStart = upTime - (startTime/100);

			if(previousCpuUsage == null) {
				previousCpuUsage = new CpuUsage();
				previousCpuUsage.totalLifeTime=timeSinceStart;
				previousCpuUsage.totalCpuTime=total_time/100;
				return 100 * ((total_time/100)/timeSinceStart);
			} else {
				double segmentCpuTime = (total_time/100)-previousCpuUsage.totalCpuTime;
				double segmentTotalTime = timeSinceStart - previousCpuUsage.totalLifeTime;
				previousCpuUsage.totalCpuTime = (total_time/100);
				previousCpuUsage.totalLifeTime = timeSinceStart;
				return 100 * segmentCpuTime/segmentTotalTime;
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return 0;
	}

	public int getVoltage()
	{
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent b = this.registerReceiver(null, ifilter);
		return b.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
	}
	/////////////////////////////////////////////////////////////////////////////////

	// Training data
	private ArrayList<double[]> trainingInput;
	private ArrayList<Double> trainingLabels;
	private Random generator;
	private String trainingInputFile = "overall_nqueens.txt";
	private String m_model_file = "nqueens_model_original.pb";

	private int brightness_val = 3;


	private Runnable numbersFdWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numbersFdHandler = new Handler();
			numbersFdHandler.post(runnableNumbersFaceDetect);
			Looper.loop();
		}
	};

	private Runnable numbersIdleWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numbersIdleHandler = new Handler();
			numbersIdleHandler.post(runnableNumbersIdle);
			Looper.loop();
		}
	};

	private Runnable numbersWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			numsHandler = new Handler();
			numsHandler.post(runnableNumbers);
			Looper.loop();
		}
	};

	private Runnable seqNumbersWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			seqNumsHandler = new Handler();
			seqNumsHandler.post(runnableSeqNumbers);
			Looper.loop();
		}
	};

	private Runnable uniqNumbersWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			uniqNumsHandler = new Handler();
			uniqNumsHandler.post(runnableUniqNumbers);
			Looper.loop();
		}
	};


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

	private Runnable pingReaderWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			pingReaderHandler = new Handler();
			pingReaderHandler.post(runnablePingReader);
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

	private Runnable fibWorker = new Runnable() {
		@Override
		public void run() {
			Looper.prepare();
			fibHandler = new Handler();
			fibHandler.post(runnableFib);
			Looper.loop();
		}
	};

	public static final String VIRUS_FOLDER_TO_SCAN = "/VirusScanFolder/filesToScan/filesToScan";
	private void readVirusFolder(String virusFolder){
		File folderToScan = new File(Environment.getExternalStorageDirectory(),
				VIRUS_FOLDER_TO_SCAN);
		File[] filesToScan = folderToScan.listFiles();
		try {
			for (File virusFile : filesToScan) {
				int fileLength = (int) virusFile.length();
				char[] fileBuffer = new char[fileLength];
				FileReader currentFile = new FileReader(virusFile);
				int totalRead = 0;
				int read = 0;
				do {
					totalRead += read;
					read = currentFile.read(fileBuffer, totalRead, fileLength - totalRead);
				} while (read > 0);
				currentFile.close();

				if (totalRead > 0) {
					String uploadString = new String(fileBuffer);
				}
			}
		} catch (IOException ex) {
			System.err.println("File scan failed with exception: " + ex.toString());
		}
	}

	private void read_initial_in(){
		String fileName = "HCSB_full.txt";
		String inputFile = "";
		try {
			InputStream is = Ping.class.getResourceAsStream(fileName);
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			if (in==null) {
				System.err.println("[Custom] The file cannot be found");
			} else {
				String line;
				while ((line = in.readLine()) != null) {
					inputFile = inputFile + line;
				}
				in.close();
			}

		} catch (IOException ioe) {
			System.err.println("Ping: [ERROR] Can't open the file "+fileName+" for reading.");
		}

		// 3.762 MB of data for i = 3
		for (int i = 0; i < 6; i++) {
			network_data += inputFile;
		}
	}

	private int pingInstCount = 0;
	private Runnable runnablePing = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

//			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
//				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho", "uan://osl-server1.cs.illinois.edu:3030/myping"+pingInstCount};
				String[] args = {VIRUS_FOLDER_TO_SCAN, "uan://osl-server1.cs.illinois.edu:3030/myecho", "uan://osl-server1.cs.illinois.edu:3030/myping"+pingInstCount};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping"+pingInstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc"+pingInstCount);
				pingInstCount++;

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
//			}

//			pingHandler.postDelayed(runnablePing, 5000);
		}

	};

	private int pingReaderInstCount = 0;
	private Runnable runnablePingReader = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

//			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
//				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho", "uan://osl-server1.cs.illinois.edu:3030/myping"+pingInstCount};
				String[] args = {VIRUS_FOLDER_TO_SCAN, "uan://osl-server1.cs.illinois.edu:3030/myping"+(pingInstCount-1),
						"uan://osl-server1.cs.illinois.edu:3030/myecho"};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mypingreader"+pingReaderInstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingreaderloc"+pingReaderInstCount);
				pingReaderInstCount++;

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				PingReader.main(args);
//			}

//			pingHandler.postDelayed(runnablePing, 5000);
		}

	};

	int ping1InstCount=0;
	private Runnable runnablePing1 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho1", "uan://osl-server1.cs.illinois.edu:3030/myping1"+ping1InstCount};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping1"+ping1InstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc1"+ping1InstCount);
				ping1InstCount++;

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler1.postDelayed(runnablePing1, 4000);
		}

	};

	private long fibInstCount = 0;

	private Runnable runnableFib = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/fib" + fibInstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/fibloc" + fibInstCount);
				String[] args = {"11","uan://osl-server1.cs.illinois.edu:3030",
						"rmsp://"+mobileIpAddress+":4040"};
				Fibonacci.main(args);
				fibInstCount++;
			}


//			fibHandler.postDelayed(runnableFib, 1000);
		}

	};

	private long numbersInstCount = 0;

//	private Runnable runnableNumbers = new Runnable(){
//		@Override
//		public void run() {
//			synchronized (oneAppSyncToken) {
//				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
//				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers"+numbersInstCount);
//
//				// Note that the IP address is the IP address of the smartphone
//				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc"+numbersInstCount);
//				numbersInstCount++;
//				String[] args = {"1"};
//				Numbers1.main(args);
//			}
//
////			numsHandler.postDelayed(runnableNumbers, 1200);
//		}
//
//	};


	private long numbersFaceDetectInstCount = 0;
	private Runnable runnableNumbersFaceDetect = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbersnewfd"+numbersFaceDetectInstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumberslocnewfd"+numbersFaceDetectInstCount);
				numbersFaceDetectInstCount++;
				String[] args = {"6", Integer.toString(durationActorRunInSec * 1000), mobileIpAddress};
				NumbersDebug1.main(args);
//				if(numbersFdConfig.equals("high")) {
//					String[] args = {"6", "100000", mobileIpAddress};
//					NumbersDebug1.main(args);
//				} else {
//					String[] args = {"6", "70000", mobileIpAddress};
//					NumbersDebug1.main(args);
//				}
			}
//			numbersFdHandler.postDelayed(runnableNumbersFaceDetect, 6000);
		}
	};

	private long numbersIdleInstCount = 0;
	private Runnable runnableNumbersIdle = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbersnewidle"+numbersIdleInstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersnewidleloc"+numbersIdleInstCount);
				numbersIdleInstCount++;
				String[] args = {"2", "180000000", mobileIpAddress};
				NumbersDebug2.main(args);
			}
//			numbersIdleHandler.postDelayed(runnableNumbersIdle, 5000);
		}
	};


	private long numbers1InstCount = 0;
	volatile boolean thread1RunFlag = false;
	private Runnable runnableNumbers1 = new Runnable(){
		@Override
		public void run() {
//			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers1"+numbers1InstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc1"+numbers1InstCount);
				numbers1InstCount++;
				String[] args = {"10"};

				// Commented because I want to run actor locally but it is trying to create SALSA actor with
				// WWCNamingService and so it fails
//				Numbers1.main(args);

				Random randomno = new Random();
				long val1 = 0;
				do {
					val1 += randomno.nextLong();
					//currentTime=System.currentTimeMillis();
				} while(thread1RunFlag);
//			}

//			numsHandler1.postDelayed(runnableNumbers1, 1200);
		}

	};

	volatile boolean thread2RunFlag = false;
	private Runnable runnableNumbers2 = new Runnable(){
		@Override
		public void run() {
//			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers2");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc2");
				String[] args = {"10"};
//				Numbers1.main(args);

				Random randomno = new Random();
				long val1 = 0;
				do {
					val1 += randomno.nextLong();
					//currentTime=System.currentTimeMillis();
				} while(thread2RunFlag);
//			}

//			numsHandler2.postDelayed(runnableNumbers2, 1100);
		}

	};

	volatile boolean thread3RunFlag = false;
	private Runnable runnableNumbers3 = new Runnable(){
		@Override
		public void run() {
//			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers3");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc3");
				String[] args = {""};
//				Numbers1.main(args);
				Random randomno = new Random();
				long val1 = 0;
				do {
					val1 += randomno.nextLong();
					//currentTime=System.currentTimeMillis();
				} while(thread3RunFlag);
			}

//			numsHandler3.postDelayed(runnableNumbers3, 1100);
//		}

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

//			numsHandler4.postDelayed(runnableNumbers4, 1100);
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

//			numsHandler5.postDelayed(runnableNumbers5, 1100);
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

//			numsHandler6.postDelayed(runnableNumbers6, 1100);
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

//			numsHandler7.postDelayed(runnableNumbers7, 1100);
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

	int ping2InstCount = 0;
	private Runnable runnablePing2 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho2", "uan://osl-server1.cs.illinois.edu:3030/myping2"+ping2InstCount};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping2"+ping2InstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc2"+ping2InstCount);

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler2.postDelayed(runnablePing2, 4000);
		}

	};

	int ping3InstCount = 0;
	private Runnable runnablePing3 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho3", "uan://osl-server1.cs.illinois.edu:3030/myping3"+ping3InstCount};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping3"+ping3InstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc3"+ping3InstCount);

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler3.postDelayed(runnablePing3, 4000);
		}

	};

	int ping4InstCount = 0;
	private Runnable runnablePing4 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho4", "uan://osl-server1.cs.illinois.edu:3030/myping4"+ping4InstCount};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping4"+ping4InstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc4"+ping4InstCount);

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler4.postDelayed(runnablePing4, 4000);
		}

	};

	int ping5InstCount = 0;
	private Runnable runnablePing5 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho5", "uan://osl-server1.cs.illinois.edu:3030/myping5"+ping5InstCount};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping5"+ping5InstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc5"+ping5InstCount);

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler5.postDelayed(runnablePing5, 3000);
		}

	};

	int ping6InstCount = 0;
	private Runnable runnablePing6 = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho6", "uan://osl-server1.cs.illinois.edu:3030/myping6"+ping6InstCount};

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping6"+ping6InstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc6"+ping6InstCount);

				System.clearProperty("netif");
				System.clearProperty("port");
				System.clearProperty("nodie");
				Ping.main(args);
			}

			pingHandler6.postDelayed(runnablePing6, 3000);
		}

	};

	private Runnable runnableNumbers = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynumbers"+numbersInstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mynumbersloc"+numbersInstCount);
				numbersInstCount++;

				String[] args = {""};
				Numbers.main(args);
			}

			numsHandler.postDelayed(runnableNumbers, 400);
		}

	};

	private int seqInstCount=0;

	private Runnable runnableSeqNumbers = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myseqnumbers"+seqInstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/myseqnumbersloc"+seqInstCount);

				seqInstCount++;
				String[] args = {""};
				SequentialNumbers.main(args);
			}

			seqNumsHandler.postDelayed(runnableSeqNumbers, 400);
		}

	};

	private int uniqInstCount=0;
	private Runnable runnableUniqNumbers = new Runnable(){
		@Override
		public void run() {
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myuniqnumbers"+uniqInstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/myuniqnumbersloc"+uniqInstCount);
				uniqInstCount++;

				String[] args = {""};
				UniqueNumbers.main(args);
			}

			uniqNumsHandler.postDelayed(runnableUniqNumbers, 400);
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

	private class BatteryBroadcastReceiver extends BroadcastReceiver {
		private final static String BATTERY_LEVEL = "level";

		@Override
		public void onReceive(Context context, Intent intent) {
			int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			float batteryPct = currentLevel / (float)scale;

			Date currentTime = Calendar.getInstance().getTime();
			appendLog("[DEBUG_BATTERY_LEVEL] " + currentTime.toString() + " " + batteryPct);
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

        mReceiver = new BatteryBroadcastReceiver();

        if (scrollView == null) {
            scrollView = new ScrollView(this);
            textView = new TextView(this);
            scrollView.addView(textView);
            scrollView.setKeepScreenOn(true);
            AndroidProxy.setTextViewContext((Activity) this, textView);
        }
        AssetManager assetMgr = this.getAssets();
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this, this.getApplicationContext()));
        System.err.println("Before creating TF inference");
//		try {
//			modelPredict = new TensorFlowInferenceInterface(assetMgr, m_model_file);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.err.println("Exception is " + e.getStackTrace());
//		}
        System.err.println("After creating TF inference");
        System.setProperty("netif", AndroidTheaterService.NETWORK_INTERFACE);
        System.setProperty("nodie", "theater");
//		System.setProperty("nogc", "theater");
        System.setProperty("port", AndroidTheaterService.THEATER_PORT);
        System.setProperty("output", AndroidTheaterService.STDOUT_CLASS);

        // Turning off AndroidTheaterService so that we can run app without network access
//		startService(new Intent(MainActivity.this, AndroidTheaterService.class));

//		if(checkPerm() == false) {
//			askPerm();
//		}

        generator = new Random();

        cpuUsage = true;




//		new Thread(numbersFdWorker).start();

//		new Thread(numbersIdleWorker).start();

//		new Thread(fibWorker).start();
//		new Thread(numbersWorker).start();
//		new Thread(seqNumbersWorker).start();
//		new Thread(uniqNumbersWorker).start();
//		new Thread(fibWorker).start();
//		new Thread(numsWorker).start();

//		new Thread(numsWorker3).start();
//		new Thread(numsWorker4).start();
//		new Thread(numsWorker5).start();
//		new Thread(numsWorker6).start();
//		new Thread(numsWorker7).start();
//		new Thread(numsWorker8).start();
//		new Thread(numsWorker9).start();
//		new Thread(numsWorker10).start();

//		read_initial_in();
//		initializeNetworkStats();

        // Reduce the size of the network data
//		int mid_network_data = network_data.length()/2;
//		network_data = network_data.substring(0, mid_network_data);
        // Reduce the size of the network data

//		new Thread(pingWorker).start();

//		new Thread(pingWorker1).start();
//		new Thread(pingWorker2).start();
//		new Thread(pingWorker3).start();
//		new Thread(pingWorker4).start();
//		new Thread(pingWorker5).start();
//		new Thread(pingWorker6).start();

		BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
			int scale = -1;
			int level = -1;
			int voltage = -1;
			int temp = -1;
			@Override
			public void onReceive(Context context, Intent intent) {
				level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
				voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
				appendLog("[Test] BatteryManager: level is "+level+"/"+scale+", temp is "+temp+", voltage is "+voltage);
			}
		};
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryReceiver, filter);

		//		new Thread(batteryWorker).start();
//		new Thread(initWorkload).start();
    }

	private Runnable initWorkload = new Runnable(){
		@Override
		public void run() {
			try {
				appendLog("sleeping to delay load 1");
				Thread.sleep(5000);
				while(true) {
					thread1RunFlag = true;
					thread2RunFlag = true;
					thread3RunFlag = true;
					Thread t1 = new Thread(runnableNumbers1);
					Thread t2 = new Thread(runnableNumbers2);
					Thread t3 = new Thread(runnableNumbers3);
					appendLog("3 threads starting");
					t1.start();
					t2.start();
					t3.start();
//		new Thread(runnableNumbers1).start();
//					Thread.sleep(60000);

//					t2.start();
//		new Thread(runnableNumbers2).start();
					Thread.sleep(60000);
//					appendLog("Sleep done, stopping numsWorker1");
					appendLog("Switching off 3 threads");
					thread1RunFlag = false;
					thread2RunFlag = false;
					thread3RunFlag = false;
					Thread.sleep(60000);
				}
			} catch (Exception ex) {
				System.err.println("Exception thrown: " + ex.toString());
			}
		}
	};

	@Override
	protected void onStart() {
		// The activity is about to become visible.
		super.onStart();
		AssetManager assetMgr = this.getAssets();
		debugPrint("onStart() is called");
//		appendLog("Wifi signal strength is " + getWifiSignalStrength());
//		registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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
		debugPrint("onPause() is called");
	}

	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
		debugPrint("onStop() is called");
//		unregisterReceiver(mReceiver);
	}

	@Override
	protected void onDestroy() {
		// The activity is about to be destroyed.
		super.onDestroy();
		debugPrint("onDestroy() is called");
	}

	private int getWifiSignalStrength() {
		int numberOfLevels=5;
		WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int level=WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
		return level;
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


	private double previousMemInMB = 0;
//	private volatile int durationActorRunInSec = 110;
	private volatile int durationActorRunInSec = 120;
	private int reqFdInstCount = durationActorRunInSec;
	private int currentFdInstCount = 0;
	private long timeSinceLastFd = 0;
	private volatile String numbersFdConfig = "high";
	private float previousBattLevel = -1;


	protected void SampleBattery() {
		final Runtime runtime = Runtime.getRuntime();
		final double usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / (double) 1048576;
		double segmentMemory = 0;
		if(previousMemInMB > 0) {
			segmentMemory = usedMemInMB - previousMemInMB;
		}

		previousMemInMB = usedMemInMB;
		if(usedMemInMB > 500) {
			appendLog("Duration and power mode are: " + durationActorRunInSec + "," + numbersFdConfig);
			throw new OutOfMemoryError();
		}

		IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = this.registerReceiver(null, iFilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float)scale;

		///////////////// Hardware resource usage code ////////////////////////////////
//		ContentResolver cResolver = this.getContentResolver();
//		long netVal = 0;
//		long currNetVal = 0;
//		try {
//			//brightness_val = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
//			netVal = getNetworkData();
//			currNetVal = netVal - old_net;
//			appendLog("Network usage: " + currNetVal);
//		}catch( Exception e){
//			System.err.println("Error in network reading");
//		}
//
//		old_net = netVal;

		if(cpuUsage) {
			try {
//				double cpuUsageVal = readUsage();
				int voltageVal = getVoltage();
//				appendLog("CPU usage: " + cpuUsageVal + ", CPU freq= " + currentCpu0Freq + " " + currentCpu1Freq
//						+ ", Voltage= " + voltageVal + ", Mem= " + segmentMemory);
//				appendLog("CPU usage: " + cpuUsageVal + ", Voltage= " + voltageVal + ", Mem= " + segmentMemory);
				appendLog("Voltage= " + voltageVal + ", Mem= " + segmentMemory);
			} catch (Exception e) {
				System.err.println("Error in CPU reading");
			}
		}
		///////////////////////////////////////////////////////////////////////////////

		boolean noFibActorsRunning = true;
		HashMap<String, Integer> hashList = UniversalActor.getActiveActors();
		Date currentTime = Calendar.getInstance().getTime();
		if (hashList.isEmpty()) {
			appendLog("Battery level is " + batteryPct + " config: " +
					numbersFdConfig + " and no active actors");
			feature[0] += 1;
		} else {
			appendLog("Battery level is " + batteryPct + " config: " +
					numbersFdConfig + " actor counts- ");
			for (String actor : hashList.keySet()) {
				appendLog(actor + ": " + hashList.get(actor) + ", ");
//				if(actor.contains("Ping")) {
//					noFibActorsRunning = false;
//				}
			}

			appendLog("\n");
		}

        ///////////////////////////////CODE for aligning actor run durations with battery drop sizes////////////////////////////
//		if ((previousBattLevel != -1) && (previousBattLevel > batteryPct)) {
//			synchronized (oneAppSyncToken) {
//				if (numbersFdConfig.equals("low")) {
//					numbersFdConfig = "high";
//					durationActorRunInSec = 120;
//
//					reqFdInstCount = durationActorRunInSec;
//				} else {
//					numbersFdConfig = "low";
//					durationActorRunInSec = 10;
//					reqFdInstCount = durationActorRunInSec;
//				}
//			}
//
//			checkpointCount(Integer.toString(durationActorRunInSec), numbersFdConfig);
////			currentFdInstCount = 1;
////			timeSinceLastFd = 0;
//		} else if(previousBattLevel == -1) {
//			Pair<Integer, String> checkpointInfo = readCheckpointCount();
//			if (checkpointInfo.first > 0) {
//				durationActorRunInSec = checkpointInfo.first;
//				numbersFdConfig = checkpointInfo.second;
//			}
//		}
        ///////////////////////////////CODE for aligning actor run durations with battery drop sizes////////////////////////////

//		else {
//			System.err.println("rime since last fd " + timeSinceLastFd + " current fd count : " + currentFdInstCount
//					+ " reqd fd count: " + reqFdInstCount);
//			if ((timeSinceLastFd % 1 == 0) && (currentFdInstCount < reqFdInstCount)) {
//				System.err.println("Going to schedule fd");
//				numbersFdHandler.post(runnableNumbersFaceDetect);
//				currentFdInstCount++;
//			}
//		}
//
//		timeSinceLastFd++;

        ///////////////////////////////CODE for aligning actor run durations with battery drop sizes////////////////////////////
//		if((previousBattLevel != -1) && noFibActorsRunning  && durationActorRunInSec > 0) {
//			pingHandler1.post(runnablePing1);
//		} else {
//			durationActorRunInSec--;
//			System.err.println(durationActorRunInSec);
//			if(durationActorRunInSec % 5 == 0) {
//				checkpointCount(Integer.toString(durationActorRunInSec), numbersFdConfig);
//			}
//		}
        ///////////////////////////////CODE for aligning actor run durations with battery drop sizes////////////////////////////

		previousBattLevel = batteryPct;
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

	protected Pair<Integer, String> readCheckpointCount()
	{
		File logFile = new File("sdcard/countCheckpoint.txt");

		int count = 0;
		String powerMode = "";
		try
		{
			//BufferedWriter for performance, true to set append to file flag
			BufferedReader buf = new BufferedReader(new FileReader(logFile));
			String line = buf.readLine();
			String[] countAndMode = line.split(",");
			count = Integer.parseInt(countAndMode[0].trim());
			powerMode = countAndMode[1].trim();
			buf.close();
		}
		catch (IOException e)
		{
			System.err.println("File doesn't exist: " + e.toString());
		}

		return new Pair(count, powerMode);
	}

	protected void checkpointCount(String durationCount, String powerMode)
	{
		File logFile = new File("sdcard/countCheckpoint.txt");
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
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile));
			buf.append(durationCount + "," + powerMode);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void appendLog(String text)
	{
		Date currentTime = Calendar.getInstance().getTime();

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
			buf.append("[" + currentTime.toString() + "] " + text);
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

