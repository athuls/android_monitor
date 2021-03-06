package controlbatteryprofiler.salsa.android_monitor.controlbatteryprofiler;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

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

    private Runnable runnableCode = new Runnable(){
        @Override
        public void run() {
            SampleBattery();
            handler.postDelayed(runnableCode, 1000);
        }
    };

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
        }

        handler.post(runnableCode);
    }

    @Override
    protected void onStart() {
        // The activity is about to become visible.
        super.onStart();
        debugPrint( "onStart() is called" );
        //HelloWorld.main(null);
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
        appendLog("Battery level is " + batteryPct + " and no active actors");
    }

    protected void showTextOnUI( String str ) {
        if (scrollView != null) {
            textView.append( str );
            setContentView( scrollView );
        }
    }

    protected void appendLog(String text)
    {
        //File logFile = new File(Environment
        //        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        //        + "/Filename.xml");
        File logFile = new File("/sdcard/log.txt");
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
