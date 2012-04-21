package edu.upenn.cis542;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import edu.upenn.cis542.route.*;
import edu.upenn.cis542.utilities.AppConstants;
import edu.upenn.cis542.utilities.DeviceConnector;

public class GPSInfoScreen  extends Activity {
    public static final int ACTIVITY_CreateNewMapRouteScreen = 1;
    
    Road pastRoad = new Road();
    
	// default initialize params
    double fromLat = 0; // 39.952881
    double fromLon = 0; // -75.209437
    double toLat = 0; // 39.952759
    double toLon = 0; // -75.192776
    RoadProvider.Mode mode = RoadProvider.Mode.WALKING; // travel mode
    String i_type = "food"; // points of interests type
    
    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        // Called when a new location is found by the location provider.
        public void onLocationChanged(Location location) {
            Log.d("GPSInfo, locationListener", "onLocationChanged");
            fromLon = location.getLongitude();
            fromLat = location.getLatitude();
            TextView currentPositionTextView = (TextView)findViewById(R.id.currentPosition);
            currentPositionTextView.setText("You are at " + Double.toString(fromLon) + ", " +Double.toString(fromLat));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("GPSInfo, locationListener", "onStatusChanged");
        }

        public void onProviderEnabled(String provider) {
            Log.d("GPSInfo, locationListener", "onProviderEnabled");
        }

        public void onProviderDisabled(String provider) {
            Log.d("GPSInfo, locationListener", "onProviderDisabled");
        }
    };
    
    // readRemoteGPS related values
    private Handler readRemoteGPSHandler = new Handler();
    private static final int UPDATE_INTERVAL = 3000;
    private boolean whetherUpdate = false; // for testing, whether the coordinates are updated periodically or not
    private Runnable readRemoteGPSTask = new Runnable() {
        public void run() {
            try {
                Log.d("readRemoteGPSTask", "Connection Start");
                
                DeviceConnector c = new DeviceConnector();
                c.readData();
                
                Log.d("readRemoteGPSTask", "Connection Done");
                
                double new_toLon = c.getLongitude();
                double new_toLat = c.getLatitude();
                
                Log.d("new_toLon", Double.toString(new_toLon));
                Log.d("new_toLat", Double.toString(new_toLat));
                
                if ((new_toLat != 0) && (new_toLon != 0)) {
                    // new GPS location is valid
                    toLon = new_toLon;
                    toLat = new_toLat;
                    TextView destinationPositionTextView = (TextView)findViewById(R.id.destinationPosition);
                    destinationPositionTextView.setText("Your destination is " + Double.toString(toLon) + ", " + Double.toString(toLat));
                } else if ((toLat == 0) && (toLon == 0)) {
                    // first time, set to default detination location
                    toLat = 39.952759;
                    toLon = -75.192776;
                    Toast.makeText(getApplicationContext(), "Can not get destination location, using default location", Toast.LENGTH_SHORT).show();
                }
                
                Log.d("readRemoteGPSTask", "Finished");
                
                if (whetherUpdate) {
                    readRemoteGPSHandler.postDelayed(readRemoteGPSTask, UPDATE_INTERVAL);
                }
            } catch (Exception e) {
                Log.e("readRemoteGPSTask", "Exception");
                
                if ((toLat == 0) && (toLon == 0)) {
                    // first time, set to default detination location
                    toLat = 39.952759;
                    toLon = -75.192776;
                    TextView destinationPositionTextView = (TextView)findViewById(R.id.destinationPosition);
                    destinationPositionTextView.setText("Your destination is " + Double.toString(toLon) + ", " + Double.toString(toLat));

                    Toast.makeText(getApplicationContext(), "Can not get destination location, using default location", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Can not get updated destination location, using last known location", Toast.LENGTH_SHORT).show();
                }
                
                if (whetherUpdate) {
                    readRemoteGPSHandler.postDelayed(readRemoteGPSTask, UPDATE_INTERVAL);
                }
            }
        }
     };
	
     
     
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_info);
		
		// get pastRoad
		//pastRoad = (edu.upenn.cis542.route.Road) getIntent().getExtras().get("pastRoad");
		
		// get SharedPreferences
		SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
		
		// Define a listener class that responds to travelModeSpinner updates
	    class travelModeSpinnerOnItemSelectedListener implements OnItemSelectedListener {
	        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	            String selectedItem = parent.getItemAtPosition(pos).toString();
	            if (selectedItem.equals("Walking")) {
	                mode = RoadProvider.Mode.WALKING;
	            } else if (selectedItem.equals("Bicycling")) {
	                mode = RoadProvider.Mode.BICYCLING;
	            } else if (selectedItem.equals("Driving")) {
	                mode = RoadProvider.Mode.DRIVING;
	            }
	            Log.d("GPSInfo, travelModeSpinner", selectedItem);
	        }

	        public void onNothingSelected(AdapterView<?> parent) {
	          // Do nothing.
	        }
	    }
	    // initialize travelModeSpinner
		Spinner travelSpinner = (Spinner) findViewById(R.id.travelModeSpinner);
	    ArrayAdapter<CharSequence> travelAdapter = ArrayAdapter.createFromResource(
	            this, R.array.travel_mode_array, android.R.layout.simple_spinner_item);
	    travelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    travelSpinner.setAdapter(travelAdapter);
	    travelSpinner.setOnItemSelectedListener(new travelModeSpinnerOnItemSelectedListener());
	    String defaultTravelValue = settings.getString(AppConstants.DEFAULT_TRAVEL_MODE_KEY, "");
	    Log.d("GPSInfo, defaultTravelValue", defaultTravelValue); 
        if (defaultTravelValue.equals("Walking")) {
            travelSpinner.setSelection(0);
        } else if (defaultTravelValue.equals("Bicycling")) {
            travelSpinner.setSelection(1);
        } else if (defaultTravelValue.equals("Driving")) {
            travelSpinner.setSelection(2);
        } else {
            travelSpinner.setSelection(0); // mode = RoadProvider.Mode.WALKING
        }
        
        // Define a listener class that responds to interestTypeSpinner updates
        class interestTypeSpinnerOnItemSelectedListener implements OnItemSelectedListener {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedItem = parent.getItemAtPosition(pos).toString();
                if (selectedItem.equals("Food")) {
                    i_type = "food";
                } else if (selectedItem.equals("Bar")) {
                    i_type = "bar";
                } else if (selectedItem.equals("Store")) {
                    i_type = "store";
                } else if (selectedItem.equals("School")) {
                    i_type = "school";
                }            
                Log.d("GPSInfo, interestTypeSpinner", selectedItem);
            }

            public void onNothingSelected(AdapterView<?> parent) {
              // Do nothing.
            }
        }
	    // initialize interestTypeSpinner
	    Spinner interestSpinner = (Spinner) findViewById(R.id.interestTypeSpinner);
        ArrayAdapter<CharSequence> interestAdapter = ArrayAdapter.createFromResource(
                this, R.array.interest_type_array, android.R.layout.simple_spinner_item);
        interestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interestSpinner.setAdapter(interestAdapter);
        interestSpinner.setOnItemSelectedListener(new interestTypeSpinnerOnItemSelectedListener());
        String defaultInterestValue = settings.getString(AppConstants.DEFAULT_INTEREST_TYPE_KEY, "");
        Log.d("GPSInfo, defaultInterestValue", defaultInterestValue); 
        if (defaultInterestValue.equals("Food")) {
            interestSpinner.setSelection(0);
        } else if (defaultInterestValue.equals("Bar")) {
            interestSpinner.setSelection(1);
        } else if (defaultInterestValue.equals("Store")) {
            interestSpinner.setSelection(2);
        } else if (defaultInterestValue.equals("School")) {
            interestSpinner.setSelection(3);
        } else {
            interestSpinner.setSelection(0); // i_type = "food"
        }
        
        
		// Get LocationManager
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Get the current GPS location
		String provider = LocationManager.GPS_PROVIDER;
		Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
		if (lastKnownLocation != null)
		{
			Log.d("GPSInfo, onCreate", "lastKnownLocation is OK");
			fromLon = lastKnownLocation.getLongitude();
			fromLat = lastKnownLocation.getLatitude();
		} else {
			Log.e("GPSInfo, onCreate", "lastKnownLocation is NULL");
			fromLat = 39.952881;
		    fromLon = -75.209437;
		    Toast.makeText(getApplicationContext(), "Can not get your GPS location, using default start location", Toast.LENGTH_LONG).show();
		}
        TextView currentPositionTextView = (TextView)findViewById(R.id.currentPosition);
        currentPositionTextView.setText("You are at " + Double.toString(fromLon) + ", " +Double.toString(fromLat));
        
        // Register listener with Location Manager to receive updates
		locationManager.requestLocationUpdates(
		             LocationManager.GPS_PROVIDER, 
						1000, // time interval
						0, // distance interval
						locationListener);
		Log.d("GPSInfoScreen", "Register locationListener");
		
		
		// start readRemoteGPSTask
		readRemoteGPSHandler.removeCallbacks(readRemoteGPSTask);
		readRemoteGPSHandler.postDelayed(readRemoteGPSTask, 0);
	}
	
    public void onMapRouteButtonClick(View view) {
        // Get LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Remove the location updates listener
        locationManager.removeUpdates(locationListener);
        Log.d("GPSInfoScreen", "Remove locationListener");
        
        Intent intent = new Intent(this, MapRouteScreen.class);
        intent.putExtra("fromLon", fromLon);
        intent.putExtra("fromLat", fromLat);
        intent.putExtra("toLon", toLon);
        intent.putExtra("toLat", toLat);
        intent.putExtra("mode", mode);
        intent.putExtra("i_type", i_type);
        //intent.putExtra("pastRoad", pastRoad);
        startActivityForResult(intent, GPSInfoScreen.ACTIVITY_CreateNewMapRouteScreen);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // the requestCode lets us know which Activity it was
        switch(requestCode) {
            case ACTIVITY_CreateNewMapRouteScreen:
                Log.d("GPDInfoScreen", "return from MapRouteScreen");
                // get the Road from the Intent object
                //Road updated_pastRoad = (Road) (intent.getExtras().get("pastRoad"));
                //pastRoad = updated_pastRoad;
                
                // Get LocationManager
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                // Register listener with Location Manager to receive updates
                locationManager.requestLocationUpdates(
                             LocationManager.GPS_PROVIDER, 
                                1000, // time interval
                                0, // distance interval
                                locationListener);
                Log.d("GPSInfoScreen", "Register again locationListener");
                break;
        }        
    }
    
    public void onBackToMainButtonClick(View view) {
        // Get LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Remove the location updates listener
        locationManager.removeUpdates(locationListener);
        
        // stop readRemoteGPSTask
        readRemoteGPSHandler.removeCallbacks(readRemoteGPSTask);
        
        // create the Intent object to send BACK to the caller
        Intent i = new Intent();
        // put the CalendarEvent object into the Intent
        //i.putExtra("pastRoad", pastRoad);
        setResult(RESULT_OK, i);
        
        // exit GPSInfoScreen
        finish();
    }
}
