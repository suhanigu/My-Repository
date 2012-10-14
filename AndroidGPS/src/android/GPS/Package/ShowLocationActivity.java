package android.GPS.Package;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ShowLocationActivity extends Activity implements LocationListener {
  private TextView latituteField;
  private TextView longitudeField;
  private TextView mytext;
  private LocationManager locationManager;
  private String provider;
  private static Location startingPoint;

	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // ms
																	
  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    latituteField = (TextView) findViewById(R.id.TextView02);
    longitudeField = (TextView) findViewById(R.id.TextView04);
    mytext = (TextView) findViewById(R.id.mytext);

    // Get the location manager
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    // Define the criteria how to select the locatioin provider -> use
    // default
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
			MINIMUM_TIME_BETWEEN_UPDATES,
			MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new MyLocationListener());
    Location location = locationManager
			.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//    Criteria criteria = new Criteria();
//    provider = locationManager.getBestProvider(criteria, false);
//    Location location = locationManager.getLastKnownLocation(provider);

    // Initialize the location fields
    if (location != null) {
      System.out.println("Provider " + provider + " has been selected.");
      onLocationChanged(location);
    } else {
      latituteField.setText("Location not available");
      longitudeField.setText("Location not available");
    }
  }

  
//Start a location listener
	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			String latlong = "Lat: " + loc.getLatitude() + " Long: "
					+ loc.getLongitude();
			System.out.println("The new location is " + latlong);
			if (loc.distanceTo(startingPoint) >= 50) {
				System.out.println("The new location is " + latlong
						+ "The Timer is stopping now");
				mytext.setText("Timer should stop");
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
  
  /* Request updates at startup */
  @Override
  protected void onResume() {
    super.onResume();
    locationManager.requestLocationUpdates(provider, 400, 1, this);
  }

  /* Remove the locationlistener updates when Activity is paused */
  @Override
  protected void onPause() {
    super.onPause();
    locationManager.removeUpdates(this);
  }

  public void onLocationChanged(Location location) {
    int lat = (int) (location.getLatitude());
    int lng = (int) (location.getLongitude());
    latituteField.setText(String.valueOf(lat));
    longitudeField.setText(String.valueOf(lng));
  }

  public void onStatusChanged(String provider, int status, Bundle extras) {
    // TODO Auto-generated method stub

  }

  public void onProviderEnabled(String provider) {
    Toast.makeText(this, "Enabled new provider " + provider,
        Toast.LENGTH_SHORT).show();

  }

  public void onProviderDisabled(String provider) {
    Toast.makeText(this, "Disabled provider " + provider,
        Toast.LENGTH_SHORT).show();
  }
} 