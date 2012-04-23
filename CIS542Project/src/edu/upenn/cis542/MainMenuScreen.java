package edu.upenn.cis542;

import edu.upenn.cis542.route.Road;
import edu.upenn.cis542.utilities.AppConstants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainMenuScreen  extends Activity{
    public static final int ACTIVITY_CreateNewGPSInfoScreen = 1;
    
    Road pastRoad = new Road();
    
    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        // Called when a new location is found by the location provider.
        public void onLocationChanged(Location location) {
            Log.d("MainMenu, locationListener", "onLocationChanged");
            Log.d("fromLon", Double.toString(location.getLongitude()));
            Log.d("fromLat", Double.toString(location.getLatitude()));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("MainMenu, locationListener", "onStatusChanged");
        }

        public void onProviderEnabled(String provider) {
            Log.d("MainMenu, locationListener", "onProviderEnabled");
        }

        public void onProviderDisabled(String provider) {
            Log.d("MainMenu, locationListener", "onProviderDisabled");
        }
    };
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		// Check whether we have default settings. If not, add to shared preferences.
		SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		String defaultTravelValue = settings.getString(AppConstants.DEFAULT_TRAVEL_MODE_KEY, "");
		if (defaultTravelValue.length() == 0) {
		    editor.putString(AppConstants.DEFAULT_TRAVEL_MODE_KEY, AppConstants.DEFAULT_TRAVEL_MODE_INITIAL_VALUE);
            Log.d("MainMenu, defaultTravelValue", "No Set, set to " + AppConstants.DEFAULT_TRAVEL_MODE_INITIAL_VALUE);
		} else {
		    Log.d("MainMenu, defaultTravelValue", defaultTravelValue);
		}
		
		String defaultInterestValue = settings.getString(AppConstants.DEFAULT_INTEREST_TYPE_KEY, "");
		if (defaultInterestValue.length() == 0) {
		    editor.putString(AppConstants.DEFAULT_INTEREST_TYPE_KEY, AppConstants.DEFAULT_INTEREST_TYPE_INITIAL_VALUE);
		    Log.d("MainMenu, defaultInterestValue", "No Set, set to " + AppConstants.DEFAULT_INTEREST_TYPE_INITIAL_VALUE);
		} else {
		    Log.d("MainMenu, defaultInterestValue", defaultInterestValue);
		}
		
		int defaultAlertValue = settings.getInt(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, -1);
		if (defaultAlertValue == -1) {
		    editor.putInt(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, AppConstants.DEFAULT_ALERT_DISTANCE_INITIAL_VALUE);
		    Log.d("MainMenu, defaultAlertValue", "No Set, set to " + Integer.toString(AppConstants.DEFAULT_ALERT_DISTANCE_INITIAL_VALUE));
		} else {
		    Log.d("MainMenu, alertDefaultValue", Integer.toString(defaultAlertValue));
		}

		editor.commit();
		
		
		// Get LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Get the current GPS location
        String provider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
        if (lastKnownLocation != null)
        {
            Log.d("MainMenu, onCreate", "lastKnownLocation is OK");
            Log.d("fromLon", Double.toString(lastKnownLocation.getLongitude()));
            Log.d("fromLat", Double.toString(lastKnownLocation.getLatitude()));
        } else {
            Log.e("MainMenu, onCreate", "lastKnownLocation is NULL");
        }
        
        // Register listener with Location Manager to receive updates
        locationManager.requestLocationUpdates(
                     LocationManager.GPS_PROVIDER, 
                        1000, // time interval
                        0, // distance interval
                        locationListener);
        Log.d("MainMenuScreen", "Register locationListener");
	}
	
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater Inflater = getMenuInflater();
		Inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.logoutOption) {
			SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.remove("logged");
			editor.commit();
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
*/
	public void onGPSInfoButtonClick(View view){
	    // Get LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Remove the location updates listener
        locationManager.removeUpdates(locationListener);
        Log.d("MainMenuScreen", "Remove locationListener");
	    
		Intent intent = new Intent(this, GPSInfoScreen.class);
		intent.putExtra("pastRoad", pastRoad);
		startActivityForResult(intent, MainMenuScreen.ACTIVITY_CreateNewGPSInfoScreen);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    super.onActivityResult(requestCode, resultCode, intent);

	    // the requestCode lets us know which Activity it was
	    switch(requestCode) {
	        case ACTIVITY_CreateNewGPSInfoScreen:
	            Log.d("GPDInfoScreen", "return from GPSInfoScreen");
	            // get the Road from the Intent object
	            Road updated_pastRoad = (Road) (intent.getExtras().get("pastRoad"));
	            pastRoad = updated_pastRoad;
	            
	            // Get LocationManager
	            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	            // Register listener with Location Manager to receive updates
	            locationManager.requestLocationUpdates(
	                         LocationManager.GPS_PROVIDER, 
	                            1000, // time interval
	                            0, // distance interval
	                            locationListener);
	            Log.d("MainMenuScreen", "Register again locationListener");
	            break;
	    }        
	}
	
	public void onHistoryButtonClick(View view){
		Intent intent = new Intent(this, HistoryScreen.class);
		startActivity(intent);
	}
	
	public void onSettingButtonClick(View view){
		Intent intent = new Intent(this, SettingScreen.class);
		startActivity(intent);
	}
	
	public void onHelpButtonClick(View view){
		Intent intent = new Intent(this, HelpScreen.class);
		startActivity(intent);
	}
	
	public void onLogoutButtonClick(View view){
		SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("logged");
		editor.commit();
		
		// Yufei TODO: 
		// store a pastRoad object into database
		
		
		finish();
	}
}
