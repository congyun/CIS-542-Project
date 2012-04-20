package edu.upenn.cis542;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
	// default initialize params
    double fromLat = 39.952881;
    double fromLon = -75.209437;
    double toLat = 39.952759;
    double toLon = -75.192776;
    RoadProvider.Mode mode = RoadProvider.Mode.WALKING;; // travel mode
    String i_type = "food"; // points of interests type
    
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_info);
		
		// get SharedPreferences
		SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
		
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
        
        
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			// Called when a new location is found by the location provider.
			public void onLocationChanged(Location location) {
				Log.d("GPSInfo, onCreate", "locationListener.onLocationChanged");
				fromLon = location.getLongitude();
				fromLat = location.getLatitude();
				TextView currentPositionTextView = (TextView)findViewById(R.id.currentPosition);
				currentPositionTextView.setText("You are at " + Double.toString(fromLon) + ", " +Double.toString(fromLat));
			}
	
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.d("GPSInfo, onCreate", "locationListener.onStatusChanged");
			}
	
			public void onProviderEnabled(String provider) {
				Log.d("GPSInfo, onCreate", "locationListener.onProviderEnabled");
			}
	
			public void onProviderDisabled(String provider) {
				Log.d("GPSInfo, onCreate", "locationListener.onProviderDisabled");
			}
		};
		// Register listener with Location Manager to receive updates
		locationManager.requestLocationUpdates(
		             LocationManager.GPS_PROVIDER, 
						1000, // time interval
						0, // distance interval
						locationListener);
        
		
/*		// Get Message and destination GPX location from C program
		try {
		    Thread rThread = new Thread(new ReadThread());
	        rThread.start();
			rThread.join();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Can not get Arduino GPS location, using default destination location", Toast.LENGTH_LONG).show();
			toLat = 39.952759;
	        toLon = -75.192776;
		}
*/
		
		toLat = 39.952759;
        toLon = -75.192776;
		TextView destinationPositionTextView = (TextView)findViewById(R.id.destinationPosition);
		destinationPositionTextView.setText("Your destination is " + Double.toString(toLon) + ", " + Double.toString(toLat));
	}	
	
    public class ReadThread implements Runnable {
        public void run() {
            try {
                DeviceConnector c = new DeviceConnector();
                Log.d("ReadThread", "Connecting.");
                c.readData();
                toLon = c.getLongitude();
                toLat = c.getLatitude();                
                Log.d("ReadThread", "Closed.");
            } catch (Exception e) {
                Log.e("ReadThread", "Error", e);
            }
        }
    }
    
    public class travelModeSpinnerOnItemSelectedListener implements OnItemSelectedListener {
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
    
    public class interestTypeSpinnerOnItemSelectedListener implements OnItemSelectedListener {
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
    
    public void onMapRouteButtonClick(View view) {
        Intent intent = new Intent(this, MapRouteScreen.class);
        intent.putExtra("fromLon", fromLon);
        intent.putExtra("fromLat", fromLat);
        intent.putExtra("toLon", toLon);
        intent.putExtra("toLat", toLat);
        intent.putExtra("mode", mode);
        intent.putExtra("i_type", i_type);
        // Siyin TODO:
        // pass pastRoad to MapRouteScreen
        startActivity(intent);
    }
    
    public void onBackToMainButtonClick(View view) {
        finish();
    }
}
