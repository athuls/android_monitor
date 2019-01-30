package com.example.androidtheater5;

import salsa.language.RunTime;
import salsa.language.ServiceFactory;
import salsa.messaging.TheaterService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AndroidTheaterService extends Service {
	private final String TAG = "SalsaTheaterService";
	public static final String NETWORK_INTERFACE = "tun0";
	public static final String THEATER_PORT = "4040";
	public static final String STDOUT_CLASS = "androidsalsa.resources.StandardOutput";
	
	private TheaterService theater;

	public static boolean theaterCreated = false;
	
    /** Called when the activity is first created. */
	@Override
	public void onCreate() {
		debugPrint("AndroidTheaterService onCreate() is called" );
		super.onCreate();
		createTheater();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		debugPrint("onBind() is called");
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// The activity is about to become visible.
		debugPrint( "onStart() is called" );		
		super.onStart(intent, 0);
	}

	@Override
	public void onDestroy() {
		// The activity is about to be destroyed.
		debugPrint( "onDestroy() is called" );		
		super.onDestroy();
		
	}
	
	protected void createTheater() {
		System.setProperty("nogc", "theater");
	    System.setProperty( "nodie", "theater" );
	    System.setProperty( "netif", NETWORK_INTERFACE );
	    System.setProperty( "port", THEATER_PORT );
	    System.setProperty("output", STDOUT_CLASS);
//	    ServiceFactory.isApplet();

        RunTime.receivedUniversalActor();
        theater = ServiceFactory.getTheater();
        debugPrint("Theater listening on: " + theater.getLocation());

		synchronized (MainActivity.theaterSyncToken) {
			AndroidTheaterService.theaterCreated = true;
			MainActivity.theaterSyncToken.notifyAll();
		}
	}
	
	protected void debugPrint( String str ) {
		Log.i( TAG, str );
	}
	
}