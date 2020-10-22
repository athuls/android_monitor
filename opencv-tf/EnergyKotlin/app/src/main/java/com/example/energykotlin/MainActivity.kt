package com.example.energykotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


import kotlinx.coroutines.TheatreMap
import org.tensorflow.lite.examples.classification.ClassifierActivity
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    var cpuUsage :Boolean = false
    private var previousBattLevel = -1f
    private var mReceiver: BroadcastReceiver? = null
    private var batteryHandler: Handler? = null

    private val runnableSampleBattery: Runnable = object : Runnable {
        override fun run() {
            SampleBattery()
            batteryHandler!!.postDelayed(this, 1000)
        }
    }
    private val batteryWorker = Runnable {
        Looper.prepare()
        batteryHandler = Handler()
        batteryHandler!!.post(runnableSampleBattery)
        Looper.loop()
    }




    fun getVoltage(): Int {
        val ifilter =
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val b = this.registerReceiver(null, ifilter)
        return b!!.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
    }
    protected fun SampleBattery() {
//        val runtime = Runtime.getRuntime()
//        val usedMemInMB =
//            (runtime.totalMemory() - runtime.freeMemory()) / 1048576.toDouble()
//        var segmentMemory = 0.0
//        if (previousMemInMB > 0) {
//            segmentMemory = usedMemInMB - previousMemInMB
//        }
//        previousMemInMB = usedMemInMB
//        if (usedMemInMB > 500) {
//            appendLog("Duration and power mode are: $durationActorRunInSec,$numbersFdConfig")
//            throw OutOfMemoryError()
//        }
        val iFilter =
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = this.registerReceiver(null, iFilter)
        val level = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = level / scale.toFloat()

        //val noFibActorsRunning = true
        //val hashList: HashMap<String, Int> = UniversalActor.getActiveActors()
        val currentTime = Calendar.getInstance().time
        appendLog("["+currentTime.toString()+"] Battery level is ${batteryPct} Segment ${TheatreMap.getSegment()} " )

        appendLog("\n")
        //println("RUSKY Time [${currentTime}] Battery is ${batteryPct} Segment is ${TheatreMap.getSegment()}")
        previousBattLevel = batteryPct
    }

    private fun appendLog(text: String?) {
        val logFile = File("sdcard/logDip.txt")
        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) { // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
        try { //BufferedWriter for performance, true to set append to file flag
            val buf =
                BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) { // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.WRITE_SETTINGS),
            0)

        mReceiver = BatteryBroadcastReceiver()
        val btn_click_me = findViewById(R.id.button) as Button

        btn_click_me.setOnClickListener{
            val intent = Intent(this, ClassifierActivity::class.java)
            startActivity(intent)
            println("[Dipayan Output] : ${TheatreMap.getSegment()} ")
            Thread(batteryWorker).start()

        }
    }
    override fun onStart() { // The activity is about to become visible.
        super.onStart()
        val assetMgr = this.assets
        //debugPrint("onStart() is called")
        //		appendLog("Wifi signal strength is " + getWifiSignalStrength());
        registerReceiver(
            mReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }
    override fun onStop() {
        super.onStop()
        // The activity is no longer visible (it is now "stopped")
        //debugPrint("onStop() is called")
        unregisterReceiver(mReceiver)
    }

    private class BatteryBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = currentLevel / scale.toFloat()
            val currentTime = Calendar.getInstance().time
            //appendLog("[DEBUG_BATTERY_LEVEL] $currentTime $batteryPct")
        }

        companion object {
            private const val BATTERY_LEVEL = "level"
        }
    }
}
