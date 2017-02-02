package androidsalsa.resources;

import android.app.Activity;
import android.os.Handler;
import android.widget.TextView;
import android.util.Log;

public class AndroidProxy {
    private static Activity context = null;
    private static TextView textView = null;
    //private static StandardOutput.State stdoutput_1, stdoutput_2;
    private  static final String TAG = "AndroidProxy";

    // to be called from Android Apps
    public static void setTextViewContext( Activity context, TextView textView ) {
        AndroidProxy.context = context;
        AndroidProxy.textView = textView;
    }

    // to be called from Salsa Apps
    public static boolean postText( final String str ) {
        if ((AndroidProxy.context == null) || (AndroidProxy.textView == null)){
            System.err.println( "valid context not set in AndroidProxy class" );
            return false;
        }

        context.runOnUiThread( new Runnable() {
            public void run() {
                AndroidProxy.textView.append( str );
            }
        });

        return true;
    }
    /*
      //called from Salsa's StandardOutput to start battey profiling:
      public static void startBatteryStatusBroadcast(StandardOutput.State s){
        debugPrint( "startBatteryStatusBroadcast() is called\n" );

          stdoutput_1 = s;
      }

      //called from Salsa's StandardOutput to start receiving Energy profiling updates:
      public static void startEnergyProfilerUpdateBroadcast(StandardOutput.State s){
        debugPrint( "startEnergyProfilerUpdateBroadcast() is called\n" );

        stdoutput_2 = s;
      }



      // to be called from Android App's AndroidBatteryStatusService() whenever there is a battery update:
        public static boolean broadcastBatteryStatusUpdate( final boolean isCharging, final boolean usbCharge, final boolean acCharge,
                                 final int batteryLevel, final int voltage, final int temp ) {
        debugPrint( "broadcastBatteryStatusUpdate() is called\n" );

        return true;
        }

      // to be called from Android App's AndroidEnergyProfilerService() to report energy update on fixed interval (every 1 sec):
      // reporting_title:  shows what we are reporting (currently: Energy Usage)
      // reporting_time_inteval: shows the duration for reported energies. Currently it is cumulative ("all time")
      // battery_temp:    Battery temperature sensor reading in celisius
      // battery_voltage: Battery voltage sensor reading in Volt
      // battery_charge:  Battery charge sensor reading reported in mAh
      // battery_perc:    percentage of battery remained
      // instant_power:   Weighted average of power consumption over the last five minutes (Reported in mW)
      // avg_power:       Average power consumption since profiler started (reported in mW)
      // current:         Battery current sensor reading (reported in mA)
      // apps_name:       name of running process that consumes energy
      // apps_energy_perc:percentage of energy used by this component
      // apps_duration    Duration for profiling this component (this is cummulative from the start of profiling) (reported in Sec)
      // apps_energy_value: the cummulative energy consumed by this specific component from start of profiling (reported in mJ)
      // apps_comm_name:       name of running process that consumes Communication energy
      // apps_comm_energy_perc:percentage of Communication energy used by this component
      // apps_comm_duration    Duration for profiling this component (this is cummulative from the start of profiling) (reported in Sec)
      // apps_comm_energy_value: the cummulative Communication energy consumed by this specific component from start of profiling (reported in mJ)
      public static boolean broadcastEnergyProfilerUpdate( final String reporting_title, final String reporting_time_interval,
                                                           final double battery_temp, final double battery_voltage, final double battery_charge,
                                                           final double battery_perc, final double instant_power,   final double avg_power,
                                                           final double current, final double total_energy,
                                                           final String[] apps_name, final double[] apps_energy_perc,
                                                           final long[] apps_duration, final double[] apps_energy_value,
                                                           final String[] apps_comm_name, final double[] apps_comm_energy_perc,
                                                           final long[] apps_comm_duration, final double[] apps_comm_energy_value ) {
        debugPrint( "broadcastEnergyProfilerUpdate() is called\n" );

        if (stdoutput_2!=null)
           stdoutput_2.broadcastEnergyProfilerUpdate( reporting_title, reporting_time_interval, battery_temp, battery_voltage,  battery_charge,
                                                      battery_perc, instant_power, avg_power, current, total_energy,
                                                      apps_name, apps_energy_perc, apps_duration, apps_energy_value,
                                                      apps_comm_name, apps_comm_energy_perc, apps_comm_duration, apps_comm_energy_value );

        return true;
      }
    */
    protected static void debugPrint(String str) {
        Log.i(TAG, str);
    }

}