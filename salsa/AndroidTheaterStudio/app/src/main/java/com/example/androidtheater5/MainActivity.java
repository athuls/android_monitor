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
import android.view.WindowManager;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	private long counter_num = 0;

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
	public String[] nqueensArgs;
	private String network_data_heavy = "";
	private String network_data_light = "";
	private String network_data="";
	private String num_state = "Low";

	private Random generator = new Random();
	public TensorFlowInferenceInterface nqueenPredict;
	public double[] feature = new double[25];
	public long[] shape = {1, feature.length};

	public long predictCount = 0;
	public long possiblePred = 0;
	public int numbersCount = 0;
	public int numbers1Count = 0;
	public int modeCount = 0;
	private boolean switchVal = Boolean.TRUE;
	private boolean pswitchVal = Boolean.TRUE;
	private boolean nswitchVal = Boolean.TRUE;
	private int initialWaitNqueens = 0;
	private int initialWaitSampleScreen = 10000;
	private int initialWaitNumbers = 0;
	private Thread prev_threadNQ;
	private  Thread prev_threadSc;

	private String num_heavy = "60000";
	private String num_light = "2000";
	private String num_arg_ct="2";
	private String num_arg;

	private long finalTime;
	private long pfinalTime;
	private long nfinalTime;
	private long numSleep;
	private boolean rQueens = Boolean.TRUE;
	private boolean rPing = Boolean.TRUE;
	private boolean rNum = Boolean.TRUE;
	private boolean rScrn = Boolean.TRUE;
	private boolean rScrnF = Boolean.TRUE;
	private volatile boolean noIdle = Boolean.FALSE;
	private long sequenceCounter = 0;

	private Handler nqueensHandler;
    private Handler nqueensHandler1;
	private Handler batteryHandler;
	private Handler screenHandler;
	private Handler pingHandler;
	private Handler numbersHandler;
	private Handler combinedHandler;

	private volatile long sCounter = 0;

	public static Object theaterSyncToken = new Object();
	private Object oneAppSyncToken = new Object();
	private Object oneScreenSyncToken = new Object();

	/* to ask for permission PACKAGE_USAGE_STAT*/
	private void AskPerm(){
		Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
		startActivity(intent);
		return;
	}

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
	}

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

	private String mobileIpAddress = "10.194.109.237";

	private void ImageCapture(){

	}
	private Runnable runnableSampleBattery = new Runnable(){
		@Override
		public void run() {
			SampleBattery();
			batteryHandler.postDelayed(runnableSampleBattery, 1000);
		}
	};

	private int runnableScreenInstCount = 0;


	private void ScreenCapture(){ // This function is a test for gathering the frames from the mobile camera for basic modification


	}

	private Runnable runnableSampleScreen = new Runnable(){
		@Override
		public void run() {

			//Switch the brightness level
			//if(brightness_val < 50) brightness_val = 255;
			//else brightness_val = 255;

			waitUntilTheaterStarted();

			// There are 2 phases of sleeping, one in low energy mode and one in high energy mode
			int sleep1 = 0, sleep2 = 0;

			// Call Actor and set its brightness level in appropriate values
			synchronized (oneScreenSyncToken) {
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myscreen1"+runnableScreenInstCount);
				//System.setProperty("uan", "uan://10.193.66.174:3030/mydip1");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/myscreenloc1"+runnableScreenInstCount );
				// System.setProperty("nogc", "theater");

				runnableScreenInstCount++;
				// Generate wait time before invoking actor
                int brightnessApp = 10 ;
                int low_brightness = 10;
                int high_brightness = 255;
//                double rVal = Math.random();
//				if(rVal > 0.4) {
//					noIdle = Boolean.FALSE;
//					// This is low energy mode
//					sleep1 = generator.nextInt(100000)+100000;
//					brightnessApp = high_brightness;
//					rScrn = Boolean.TRUE;
//					//rScrnF = Boolean.FALSE;
//					//sleep2 = 0;
//				} else if( rVal > 0.5) {
//					//sleep1 = 20000;
//                    brightnessApp = low_brightness;
//					sleep1 = generator.nextInt(100000)+100000;
//					rScrn = Boolean.TRUE;
//					//rScrnF = Boolean.FALSE;
//				}
//				else{
//					noIdle = Boolean.TRUE;
//					brightnessApp = 10;
//					sleep1 = generator.nextInt(100000)+100000;
//					rScrn = Boolean.FALSE;
//					if(rScrnF){
//						rScrnF = Boolean.FALSE;
//						String[] args = {Integer.toString(1000),
//								Integer.toString(brightnessApp),
//						};
//						TestApp.main(args);
//					}
//				}
				if(sCounter == 1){
					rScrn = Boolean.TRUE;
					brightnessApp = high_brightness;
					sleep1 = 5000;

				}
				else{
					rScrn = Boolean.TRUE;
					if(sequenceCounter == 0){
						brightnessApp = low_brightness;
					}
					else{
						brightnessApp = high_brightness;
					}
					sequenceCounter = (sequenceCounter+1)%2;
					sleep1 = 5000;

				}


				if(rScrn) {
					//rScrnF = Boolean.FALSE;
					String[] args = {Integer.toString(sleep1),
							Integer.toString(brightnessApp),
					};
					TestApp.main(args);
				}
			}

			screenHandler.postDelayed(runnableSampleScreen, sleep1);
		}
	};



	private Runnable nqueensWorker = new Runnable() {
		@Override
		public void run() {
			//synchronized (oneScreenSyncToken) {
				Looper.prepare();
				nqueensHandler1 = new Handler();
				nqueensHandler1.postDelayed(runnableNqueens1, initialWaitNqueens);
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
			synchronized (oneScreenSyncToken) {
				Looper.prepare();
				screenHandler = new Handler();
				screenHandler.postDelayed(runnableSampleScreen, initialWaitSampleScreen);
				Looper.loop();
			}
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

	private Runnable numbersWorker = new Runnable() {
		@Override
		public void run() {
			//synchronized (oneScreenSyncToken) {
			Looper.prepare();
			numbersHandler = new Handler();
			numbersHandler.postDelayed(runnableNumbers, initialWaitNumbers);
			Looper.loop();
			//}
		}
	};

	private Runnable combinedWorker = new Runnable() {
		@Override
		public void run() {
			//synchronized (oneScreenSyncToken) {
			Looper.prepare();
			combinedHandler = new Handler();
			combinedHandler.postDelayed(runnableCombined, initialWaitNumbers);
			Looper.loop();
			//}
		}
	};



	private Runnable runnableNqueens = new Runnable(){
        @Override
        public void run() {
			waitUntilTheaterStarted();
            synchronized (oneAppSyncToken) {
                // The host name osl-server1.cs.illinois.edu is where the nameserver is running
                System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynqueens");
                //System.setProperty("uan", "uan://192.168.0.102:3030/mynqueens");

                // Note that the IP address is the IP address of the smartphone
                System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mynqueensloc");
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
                nqueensHandler1.postDelayed(runnableNqueens1, 1000);
                //}
            }
        }

    };

	private int runnableNumbersInstCount = 0;

	private Runnable runnableNumbers = new Runnable(){
		@Override
		public void run() {
			waitUntilTheaterStarted();
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynqueens" + runnableNumbersInstCount );
				//System.setProperty("uan", "uan://192.168.0.102:3030/mynqueens");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mynqueensloc" + runnableNumbersInstCount );
				runnableNumbersInstCount ++;
//				System.setProperty("nogc", "theater");

				if(nswitchVal){
					double Rval = Math.random();
					if(Rval > 0.4){
						num_arg = num_heavy;
						num_arg_ct = "7";
						num_state = "high";
						// Get a time till which it will run
						Random r = new Random();
						long RTime =  generator.nextInt(300000)+100000;
						numSleep = RTime;
						nfinalTime = System.currentTimeMillis() + RTime;
						nswitchVal = Boolean.FALSE;
						rNum = Boolean.TRUE;

					}
					else if (Rval > 0.5){
						num_arg = num_light;
						num_arg_ct = "3";
						num_state = "low";
						Random r = new Random();
						long RTime = generator.nextInt(300000)+100000;
						numSleep = RTime;
						nfinalTime = System.currentTimeMillis() + RTime;
						nswitchVal = Boolean.FALSE;
						rNum = Boolean.TRUE;

					}
					else{
						num_arg = num_light;
						num_state = "none";
						Random r = new Random();
						int RTime = generator.nextInt(100000)+100000;
						numSleep = RTime;
						nfinalTime = System.currentTimeMillis() + RTime;
						nswitchVal = Boolean.FALSE;
						rNum = Boolean.FALSE;

					}
				}
				String[] args = {num_arg,"500",num_arg_ct,Long.toString(numSleep),num_state,mobileIpAddress};

				if(System.currentTimeMillis() < nfinalTime){
					nswitchVal = Boolean.FALSE;
				}
				else {
					nswitchVal = Boolean.TRUE;
				}

				if(rNum){
					Numbers.main(args);
					counter_num++;
				}
			}


			numbersHandler.postDelayed(runnableNumbers, 1000);

		}

	};

	private Runnable runnableCombined = new Runnable(){
		@Override
		public void run() {
			waitUntilTheaterStarted();
			synchronized (oneAppSyncToken) {
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynqueens" + runnableNumbersInstCount );
				//System.setProperty("uan", "uan://192.168.0.102:3030/mynqueens");

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mynqueensloc" + runnableNumbersInstCount );
				runnableNumbersInstCount ++;
//				System.setProperty("nogc", "theater");
				int brightnessApp = 10 ;
				int low_brightness = 150;
				int high_brightness = 255;
				int RTime = 10;
				if(nswitchVal){
					double Rval = Math.random();
					if(Rval > 0.4){
						num_arg = num_heavy;
						brightnessApp = high_brightness;
						num_arg_ct = "7";
						num_state = "high";
						// Get a time till which it will run
						Random r = new Random();
						RTime =  generator.nextInt(300000)+100000;
						numSleep = RTime;
						nfinalTime = System.currentTimeMillis() + RTime;
						nswitchVal = Boolean.FALSE;
						rNum = Boolean.TRUE;
						rScrn = Boolean.TRUE;
					}
					else if (Rval > 0.5){
						num_arg = num_light;
						num_arg_ct = "3";
						num_state = "low";
						brightnessApp = low_brightness;
						Random r = new Random();
						RTime = generator.nextInt(300000)+100000;
						numSleep = RTime;
						nfinalTime = System.currentTimeMillis() + RTime;
						nswitchVal = Boolean.FALSE;
						rNum = Boolean.TRUE;
						rScrn = Boolean.TRUE;
					}
					else{
						num_arg = num_light;
						brightnessApp = 10;
						num_state = "none";
						Random r = new Random();
						RTime = generator.nextInt(100000)+100000;
						numSleep = RTime;
						nfinalTime = System.currentTimeMillis() + RTime;
						nswitchVal = Boolean.FALSE;
						rNum = Boolean.FALSE;
						rScrn = Boolean.FALSE;
						if(rScrnF){
							rScrnF = Boolean.FALSE;
							String[] sargs = {Integer.toString(1000),
									Integer.toString(brightnessApp),
							};
							TestApp.main(sargs);
						}

					}
				}
				String[] args = {num_arg,"500",num_arg_ct,Long.toString(numSleep),num_state,mobileIpAddress};


				if(System.currentTimeMillis() < nfinalTime){
					nswitchVal = Boolean.FALSE;
				}
				else {
					nswitchVal = Boolean.TRUE;
				}
				if(rScrn) {
					//rScrnF = Boolean.FALSE;
					rScrn = Boolean.FALSE;
					String[] Sargs = {Integer.toString(RTime),
							Integer.toString(brightnessApp),
					};
					TestApp.main(Sargs);
				}

				if(rNum){
					Numbers.main(args);
					counter_num++;
				}
			}


			combinedHandler.postDelayed(runnableCombined, 1000);

		}

	};
	private int runnableNqInstCount = 0;
    private Runnable runnableNqueens1 = new Runnable(){
        @Override
        public void run() {
            waitUntilTheaterStarted();
            synchronized (oneAppSyncToken) {
                // The host name osl-server1.cs.illinois.edu is where the nameserver is running
                System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/mynqueens"+runnableNqInstCount);
                //System.setProperty("uan", "uan://192.168.0.102:3030/mynqueens");

                // Note that the IP address is the IP address of the smartphone
                System.setProperty("ual", "rmsp://" + mobileIpAddress +":4040/mynqueensloc"+runnableNqInstCount);
//                System.setProperty("nogc", "theater");
				runnableNqInstCount++;
                if(switchVal){
                    double Rval = Math.random();
                    if(Rval > 0.7){
                        nqueensArgs = heavy;
                        // Get a time till which it will run
                        Random r = new Random();
                        int RTime =  generator.nextInt(200000)+100000;
                        finalTime = System.currentTimeMillis() + RTime;
                        switchVal = Boolean.FALSE;
                        rQueens = Boolean.TRUE;
                    }
                    else if (Rval > 0.2){
                        nqueensArgs = light;
                        Random r = new Random();
                        int RTime = generator.nextInt(200000)+100000;
                        finalTime = System.currentTimeMillis() + RTime;
                        switchVal = Boolean.FALSE;
                        rQueens = Boolean.TRUE;
                    }
                    else{
                        Random r = new Random();
                        int RTime = generator.nextInt(200000)+100000;
                        finalTime = System.currentTimeMillis() + RTime;
                        switchVal = Boolean.FALSE;
                        rQueens = Boolean.FALSE;
                    }
                }

				if(System.currentTimeMillis() < finalTime){
					switchVal = Boolean.FALSE;
				}
				else {
					switchVal = Boolean.TRUE;
				}
				if(rQueens){
					Nqueens.main(nqueensArgs);
				}


            }


            nqueensHandler1.postDelayed(runnableNqueens1, 1000);

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
		//network_data_light = inputFile;
		for (int i = 0; i < 3; i++) {
			if(i < 3){
				network_data_light += inputFile;
			}
			network_data_heavy += inputFile;
		}
	}

	private int runnablePingInstCount = 0;
	private Runnable runnablePing = new Runnable(){
		@Override
		public void run() {

			waitUntilTheaterStarted();

			synchronized (oneAppSyncToken) {
				// Ping program
				// The host name osl-server1.cs.illinois.edu is where the nameserver is running

				// The host name osl-server1.cs.illinois.edu is where the nameserver is running
				System.setProperty("uan", "uan://osl-server1.cs.illinois.edu:3030/myping"+runnablePingInstCount);

				// Note that the IP address is the IP address of the smartphone
				System.setProperty("ual", "rmsp://" + mobileIpAddress + ":4040/mypingloc"+runnablePingInstCount);
				runnablePingInstCount++;
				System.clearProperty("netif");
				System.clearProperty("port");
				//System.clearProperty("nodie");

				//String  network_data = "";

				if(pswitchVal){
					double Rval = Math.random();
					if(Rval > 0.4){
						network_data = network_data_heavy;
						num_state="high";
						// Get a time till which it will run
						//Random r = new Random();
						int RTime =  generator.nextInt(50000)+100000;
						pfinalTime = System.currentTimeMillis() + RTime;
						pswitchVal = Boolean.FALSE;
						rPing = Boolean.TRUE;
					}
					else if (Rval > 0.5){
						network_data = network_data_light;
						num_state="low";
						//Random r = new Random();
						int RTime = generator.nextInt(300000)+100000;
						pfinalTime = System.currentTimeMillis() + RTime;
						pswitchVal = Boolean.FALSE;
						rPing = Boolean.TRUE;
					}
					else{
						//Random r = new Random();
						network_data = "";
						num_state="none";
						int RTime = generator.nextInt(300000)+100000;
						pfinalTime = System.currentTimeMillis() + RTime;
						pswitchVal = Boolean.FALSE;
						rPing = Boolean.FALSE;
					}
				}
				String[] args = {network_data, "uan://osl-server1.cs.illinois.edu:3030/myecho", "uan://osl-server1.cs.illinois.edu:3030/myping"+Integer.toString(runnablePingInstCount-1)};

				if(System.currentTimeMillis() < pfinalTime){
					pswitchVal = Boolean.FALSE;
				}
				else {
					pswitchVal = Boolean.TRUE;
				}
				if(rPing){
					Ping.main(args);
				}
			}

			pingHandler.postDelayed(runnablePing, 2000);
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
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if (scrollView == null) {
			scrollView = new ScrollView( this );
			textView = new TextView( this );
			scrollView.addView( textView );
			scrollView.setKeepScreenOn(true);
			AndroidProxy.setTextViewContext((Activity) this, textView);
		}
		AssetManager assetMgr = this.getAssets();
		MyExceptionHandler exp = new MyExceptionHandler(this, this.getApplicationContext());
		Thread.setDefaultUncaughtExceptionHandler(exp);

//		nqueenPredict = new TensorFlowInferenceInterface(assetMgr, "nqueens_model.pb");

		System.setProperty( "netif", AndroidTheaterService.NETWORK_INTERFACE);
		//System.setProperty( "nodie", "theater" );
		//System.setProperty("nogc", "theater");
		System.setProperty("port", AndroidTheaterService.THEATER_PORT);
		System.setProperty("output", AndroidTheaterService.STDOUT_CLASS);

		startService(new Intent(MainActivity.this, AndroidTheaterService.class));
		if (CheckPerm() == false ) {
			AskPerm();
		}
		read_initial_in();
		new Thread(batteryWorker).start();
		//SampleScreen();

//		Thread nQ = new Thread(nqueensWorker);
//		nQ.setUncaughtExceptionHandler(exp);
//		nQ.start();
//		Thread nW = new Thread(numbersWorker);
//		nW.setUncaughtExceptionHandler(exp);
//		nW.start();
//		Thread cW = new Thread(combinedWorker);
//		cW.setUncaughtExceptionHandler(exp);
//		cW.start();
		Thread sW = new Thread(screenWorker);
		sW.setUncaughtExceptionHandler(exp);
		sW.start();
//		Thread nP = new Thread(pingWorker);
//		nP.setUncaughtExceptionHandler(exp);
//		nP.start();
		//new Thread(nqueensWorker).start();
		//new Thread(pingWorker).start();



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

	/* This method is not used, for controlling brightness anymore, use TestApp.salsa */
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

	// This is to set the initial delay before starting NQueens, Screen actors
	private void setInitialWaitTimes(float batteryPct) {
		// This is where testApp actor will be called when there is a battery percent drop
		if(Math.abs(last_battery - batteryPct) > 0.009){
			last_battery = batteryPct;

			//synchronized (oneScreenSyncToken) {
				//numbers1Count = generator.nextInt(20) + 50;
			if(prev_threadSc != null){
				prev_threadSc.stop();
			}
			if (prev_threadNQ != null){
				prev_threadNQ.stop();
			}
				numbers1Count = 30;
				//numbersCount = generator.nextInt(20) + 50;
				numbersCount = 30;
				//initialWaitNqueens = generator.nextInt(25) * 1000;
				initialWaitNqueens = 10000;
				modeCount++;
				if(modeCount /20 == 0) {
					initialWaitSampleScreen = initialWaitNqueens;
				} else if(modeCount /20 == 1) {
					//initialWaitSampleScreen = (numbersCount - (  generator.nextInt(numbersCount -1))) + initialWaitNqueens;
					initialWaitSampleScreen = initialWaitNqueens+ numbersCount -15;
				} else {
					initialWaitSampleScreen = numbersCount + 1 + initialWaitNqueens;
				}



				prev_threadNQ = new Thread(nqueensWorker);
				prev_threadNQ.start();
				prev_threadSc =  new Thread(screenWorker);
				prev_threadSc.start();
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
			brightness_val = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
			//netVal = getNetworkData();
			//netVal = getNetworkDataOld(); // Test for older phones
			currNetVal = netVal - old_net;
		}catch( Exception e){
			System.err.println("Error in brightness");
		}

		if(Math.abs(last_battery - batteryPct) > 0.009) {
			last_battery = batteryPct;
			sCounter = (sCounter+1)%2;
		}

		HashMap<String, Integer> hashList = UniversalActor.getActiveActors();

//		setInitialWaitTimes(batteryPct);

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
			//appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct + ", brightness=" + brightness_val+ "Time Sleep "+ numSleep+" Actor state "+num_state+ " and no active actors");
			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct + ", brightness=" + brightness_val+" Actor state "+num_state+ " Num_counter "+ counter_num+ " and no active actors");

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
			appendLog("[" + currentTime.toString() + "] Battery level is " + batteryPct + ", brightness=" + brightness_val + "Time Sleep "+ numSleep+" Actor state "+num_state+ " Num_counter "+ counter_num+" actor counts- ");
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

		//old_net = netVal;

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

