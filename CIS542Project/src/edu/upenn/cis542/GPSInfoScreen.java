package edu.upenn.cis542;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import edu.upenn.cis542.route.*;
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
		
		// initialize travelModeSpinner
		Spinner spinner = (Spinner) findViewById(R.id.travelModeSpinner);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.travel_mode_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new travelModeSpinnerOnItemSelectedListener());
	    spinner.setSelection(1);
	    
		
	    // initialize interestTypeSpinner
	    Spinner spinner2 = (Spinner) findViewById(R.id.interestTypeSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.interest_type_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new interestTypeSpinnerOnItemSelectedListener());
        spinner2.setSelection(1);
        
		// Get LocationManager
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Get the current location
		String provider = LocationManager.GPS_PROVIDER;
		Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
		if (lastKnownLocation != null)
		{
			Log.d("onCreate", "lastKnownLocation is OK");
			fromLon = lastKnownLocation.getLongitude();
			fromLat = lastKnownLocation.getLatitude();
			TextView currentPositionTextView = (TextView)findViewById(R.id.currentPosition);
			currentPositionTextView.setText("You are at " + Double.toString(fromLon) + ", " +Double.toString(fromLat));
		} else {
			Log.e("onCreate", "lastKnownLocation is NULL");
		}
		
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			// Called when a new location is found by the location provider.
			public void onLocationChanged(Location location) {
				Log.d("onCreate", "locationListener.onLocationChanged");
				fromLon = location.getLongitude();
				fromLat = location.getLatitude();
				TextView currentPositionTextView = (TextView)findViewById(R.id.currentPosition);
				currentPositionTextView.setText("You are at " + Double.toString(fromLon) + ", " +Double.toString(fromLat));
			}
	
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.d("onCreate", "locationListener.onStatusChanged");
			}
	
			public void onProviderEnabled(String provider) {
				Log.d("onCreate", "locationListener.onProviderEnabled");
			}
	
			public void onProviderDisabled(String provider) {
				Log.d("onCreate", "locationListener.onProviderDisabled");
			}
		};

		// Register listener with Location Manager to receive updates
		locationManager.requestLocationUpdates(
		             LocationManager.GPS_PROVIDER, 
						1000, // time interval
						0, // distance interval
						locationListener);

/*		
		// Get Message and destination GPX coordinates from Device
		Thread rThread = new Thread(new ReadThread());
		rThread.start();
		try {
			rThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
		TextView destinationPositionTextView = (TextView)findViewById(R.id.destinationPosition);
		String msgFromServer = "Your destination is " + Double.toString(toLon) + ", " + Double.toString(toLat);
		destinationPositionTextView.setText(msgFromServer);
	}	
	
    
    public void onMapRouteButtonClick(View view) {
        Intent intent = new Intent(this, MapRouteScreen.class);
        intent.putExtra("fromLon", fromLon);
        intent.putExtra("fromLat", fromLat);
        intent.putExtra("toLon", toLon);
        intent.putExtra("toLat", toLat);
        intent.putExtra("mode", mode);
        intent.putExtra("i_mode", i_type);
        startActivity(intent);
    }
    
	public void onBackToMainButtonClick(View view) {
		finish();
	}
	
	
    public class ReadThread implements Runnable {
        public void run() {
            try {
                Log.d("ReadThread", "Connecting.");

                DeviceConnector c = new DeviceConnector();
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
            Log.d("travelModeSpinner", selectedItem);
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
            } else if (selectedItem.equals("Shopping")) {
                i_type = "shopping";
            }
            Log.d("interestTypeSpinner", selectedItem);
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
}
