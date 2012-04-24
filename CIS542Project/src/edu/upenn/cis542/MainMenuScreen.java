package edu.upenn.cis542;

import java.io.InputStream;

import edu.upenn.cis542.route.Point;
import edu.upenn.cis542.route.Road;
import edu.upenn.cis542.route.RoadProvider;
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
    
    Road pastRoad;
    
    // params used to query pastRoad
    private double queriedFromLat, queriedFromLon, queriedToLat, queriedToLon;
    private RoadProvider.Mode queriedMode;
    private Road queriedPastRoad;
    
    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        // Called when a new location is found by the location provider.
        public void onLocationChanged(Location location) {
            Log.d("MainMenu, locationListener", "onLocationChanged");
            Log.d("MainMenu, location.getLongitude()", Double.toString(location.getLongitude()));
            Log.d("MainMenu, location.getLatitude()", Double.toString(location.getLatitude()));
            
            // update pastRoad if it's a new location
            if ((location.getLongitude() != pastRoad.mPoints[pastRoad.mPoints.length - 1].mLongitude) ||
                 (location.getLatitude() != pastRoad.mPoints[pastRoad.mPoints.length - 1].mLatitude)) {
                Log.d("MainMenu, location", "NEW location");
                Log.d("MainMenu, OLD pastRoad.mPoints.length", Integer.toString(pastRoad.mPoints.length));
                
                pastRoad.mEndTime = System.currentTimeMillis();
                
                queriedFromLon = pastRoad.mPoints[pastRoad.mPoints.length - 1].mLongitude;
                queriedFromLat = pastRoad.mPoints[pastRoad.mPoints.length - 1].mLatitude;
                queriedToLon = location.getLongitude();
                queriedToLat = location.getLatitude();
                queriedMode = RoadProvider.Mode.WALKING; // TODO: change to default?
                Log.d("MainMenu, queriedFromLon", Double.toString(queriedFromLon));
                Log.d("MainMenu, queriedFromLat", Double.toString(queriedFromLat));
                Log.d("MainMenu, queriedToLon", Double.toString(queriedToLon));
                Log.d("MainMenu, queriedToLat", Double.toString(queriedToLat));
                if (queriedMode == RoadProvider.Mode.WALKING) {
                    Log.d("MainMenu, queriedMode", "WALKING");
                } else if (queriedMode == RoadProvider.Mode.BICYCLING) {
                    Log.d("MainMenu, queriedMode", "BICYCLING");
                } else if (queriedMode == RoadProvider.Mode.DRIVING) {
                    Log.d("MainMenu, queriedMode", "DRIVING");
                }

                Thread rThread = new Thread() {
                        @Override
                        public void run() {
                                String url = RoadProvider.getUrl(queriedFromLat, queriedFromLon, queriedToLat, queriedToLon, queriedMode);
                                InputStream is = RoadProvider.getConnection(url);
                                queriedPastRoad = RoadProvider.getRoute(is);
                        }
                };
                rThread.start();
                try {
                    rThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                Log.d("MainMenu, queriedPastRoad.mPoints.length", Integer.toString(queriedPastRoad.mPoints.length));
                edu.upenn.cis542.route.Point[] newPoints = new edu.upenn.cis542.route.Point[pastRoad.mPoints.length + queriedPastRoad.mPoints.length + 1];
                // copy old points
                for (int i = 0; i < pastRoad.mPoints.length; i++) {
                    newPoints[i] = pastRoad.mPoints[i];
                }
                // add queried points on the road, not included from and to points
                for (int j = 0; j < queriedPastRoad.mPoints.length; j++) {
                    newPoints[pastRoad.mPoints.length + j] = queriedPastRoad.mPoints[j];
                    Log.d("MainMenu, queriedPastRoad.mPoints", j + ": " + queriedPastRoad.mPoints[j].mDescription);
                    Log.d("MainMenu, queriedPastRoad.mPoints", j + ": " + queriedPastRoad.mPoints[j].mLongitude + " " + queriedPastRoad.mPoints[j].mLatitude);
                }
                // add current(to) location
                newPoints[pastRoad.mPoints.length + queriedPastRoad.mPoints.length] = new edu.upenn.cis542.route.Point();
                newPoints[pastRoad.mPoints.length + queriedPastRoad.mPoints.length].mLongitude = location.getLongitude();
                newPoints[pastRoad.mPoints.length + queriedPastRoad.mPoints.length].mLatitude = location.getLatitude();
                
                pastRoad.mPoints = newPoints;
                Log.d("MainMenu, NEW pastRoad.mPoints.length", Integer.toString(pastRoad.mPoints.length));
            } else {
                Log.d("MainMenu, location", "OLD location");
            }
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
		
		// create new pastRoad object, record start time
		pastRoad = new Road();
		pastRoad.mStartTime = System.currentTimeMillis();
		pastRoad.mEndTime = pastRoad.mStartTime;
		pastRoad.mPoints = new Point[1];
        pastRoad.mPoints[0] = new Point();
        
		// Get LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Get the current GPS location
        String provider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
        if (lastKnownLocation != null)
        {
            Log.d("MainMenu, onCreate", "lastKnownLocation is OK");
            Log.d("lastKnownLocation.getLongitude()", Double.toString(lastKnownLocation.getLongitude()));
            Log.d("lastKnownLocation.getLatitude()", Double.toString(lastKnownLocation.getLatitude()));
            pastRoad.mPoints[0].mLongitude = lastKnownLocation.getLongitude();
            pastRoad.mPoints[0].mLatitude = lastKnownLocation.getLatitude();
        } else {
            Log.e("MainMenu, onCreate", "lastKnownLocation is NULL");
            pastRoad.mPoints[0].mLongitude = -75.209437;
            pastRoad.mPoints[0].mLatitude = 39.952881;
            Toast.makeText(getApplicationContext(), "Can not get your GPS location, using default start location", Toast.LENGTH_LONG).show();            
        }
        Log.d("pastRoad.mPoints[0].mLatitude", Double.toString(pastRoad.mPoints[0].mLatitude));
        Log.d("pastRoad.mPoints[0].mLongitude", Double.toString(pastRoad.mPoints[0].mLongitude));
        
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
	            Log.d("MainMenuScreen", "return from GPSInfoScreen");
	            
                // get the Road from the Intent object
	            Log.d("MainMenu, OLD pastRoad.mPoints.length", Integer.toString(pastRoad.mPoints.length));
	            pastRoad = (Road) (intent.getExtras().get("pastRoad"));
	            Log.d("MainMenu, NEW pastRoad.mPoints.length", Integer.toString(pastRoad.mPoints.length));
	            for (int i = 0; i < pastRoad.mPoints.length; i++) {
                    Log.d("MainMenu, NEW pastRoad.mPoints", i + ": " + pastRoad.mPoints[i].mLongitude + " " + pastRoad.mPoints[i].mLatitude);
                }
	            
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
	    // Get LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Remove the location updates listener
        locationManager.removeUpdates(locationListener);
        Log.d("MainMenuScreen", "Remove locationListener");
        
		SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("logged");
		editor.commit();
		
		// Yufei TODO: 
		// store a pastRoad object into database
		
		
		finish();
	}
}
