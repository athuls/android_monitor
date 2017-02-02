package com.example.androidtheater5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import androidsalsa.resources.AndroidProxy;
import com.example.androidtheater5.AndroidTheaterService;

public class MainActivity extends Activity {
	private final String TAG = "AndroidTheater";
	
	private ScrollView scrollView = null;
	private TextView textView = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		debugPrint( "onCreate() is called" );		
		super.onCreate(savedInstanceState);

		if (scrollView == null) {
			scrollView = new ScrollView( this );
			textView = new TextView( this );
			scrollView.addView( textView );
			AndroidProxy.setTextViewContext( (Activity)this, textView );
		}

		startService( new Intent(MainActivity.this, AndroidTheaterService.class) );
	}

	@Override
	protected void onStart() {
		// The activity is about to become visible.
		super.onStart();
		debugPrint( "onStart() is called" );
	}

	@Override
	protected void onResume() {
		// The activity has become visible (it is now "resumed").
		super.onResume();
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
	
	protected void showTextOnUI( String str ) {
		if (scrollView != null) {
			textView.append( str );
			setContentView( scrollView );
        }
    }
}

