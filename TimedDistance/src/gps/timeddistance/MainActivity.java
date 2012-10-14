package gps.timeddistance;

import java.util.Timer;
import java.util.TimerTask;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi", "NewApi" })
public class MainActivity extends Activity {

	
	private static TextView displayText;
	private Button btntimer;
	private static EditText displayDist;
	private static EditText displayTime;
	private static Location startingPos;
	private static Location currentPos;
	
	public LocationManager locationManager;
	static String display = "";
	static String displaydist = "";
	static String displaytime = "";
	
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 10; // meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 5000; // ms
//	private Timer timer;
//	private task tt;
//	private int i = 0;
//	private int j = 0;
	private long starttime = 0;
	private boolean timervalue = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();  
	  	   StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		displayText = (TextView) findViewById(R.id.displaytext);
		btntimer = (Button) findViewById(R.id.btntimer);
		displayDist = (EditText) findViewById(R.id.displaydist);
		displayTime = (EditText) findViewById(R.id.displaytime);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
		
		btntimer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timervalue = true;
				starttime = System.currentTimeMillis();
				new Thread(new LocationThread()).start();
			}
		});
		
		displayText.setText("Press Start Timer Button");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    class LocationThread implements Runnable
	{
		public void run() {
			
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		startingPos = location;
		
		if (location != null) {
			String message = String.format("Current Location \n Longitude: %1$s \n Latitude: %2$s",location.getLongitude(), location.getLatitude());
			System.out.println("Current Location " + location.getLatitude() + " and " + location.getLongitude());
			Message mesg = new Message();
	        display = "Timer Started. \n" + message;
	        displaydist = "";
			myUpdateHandler.sendMessage(mesg);
		}
		else
		{
			System.out.println("location is null");	
		}
		}
	}

	
	// Start a location listener
	private class MyLocationListener implements LocationListener {
		
		
		public void onLocationChanged(Location loc) {
			
			String latlong = "\n Lat: " + loc.getLatitude() + "Long: "
					+ loc.getLongitude();
			currentPos = loc;
			float dist = loc.distanceTo(startingPos);
			System.out.println("Location changed to: " + latlong + "\n distance from start: " + dist);
			Message mesg = new Message();
	        display = "Location changed to: " + latlong;
	        displaydist =  "distance from start: " + dist;// + "Time elapsed: " + stopwatch.getElapsedTimeSecs() + "sec";
			myUpdateHandler.sendMessage(mesg);
			
			if (dist >= 50) {
				
				timervalue = false;
				System.out.println("Final location is " + latlong + "\n distance from start: " + dist + "\n Timer Stopped");
				Message msg = new Message();
		        display = "Final location is " + latlong + "\n Timer Stopped" ;;
		        displaydist = "\n distance from start: " + dist;// + "Time elapsed: " + stopwatch.getElapsedTimeSecs() + "sec";
				myUpdateHandler.sendMessage(msg);
			}
		}

		public void onProviderDisabled(String provider) {
			// required for interface, not used
		}

		public void onProviderEnabled(String provider) {
			// required for interface, not used
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// required for interface, not used
		}
	};

	// pauses listener while app is inactive
	@Override
	public void onPause() {
		super.onPause();
	}

	// reactivates listener when app is resumed
	@Override
	public void onResume() {
		super.onResume();
	}
	
	 static Handler myUpdateHandler = new Handler() {
	 	    public void handleMessage(Message mesg) {
	 	                    displayText.setText(display);
	 	                    displayDist.setText(displaydist);
	 	                    displayTime.setText(displaytime);
	 	            super.handleMessage(mesg);
	 	    }
	 	};

    
}
