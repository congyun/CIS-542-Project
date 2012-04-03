package edu.upenn.cis542;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
 
public class GPSInfoScreen  extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_info);
		
		// Get LocationManager
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Get the current location
		String provider = LocationManager.GPS_PROVIDER;
		Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
		if (lastKnownLocation != null)
		{
			Log.d("onCreate", "lastKnownLocation is OK");
			double longitude = lastKnownLocation.getLongitude();
			double latitude = lastKnownLocation.getLatitude();
			TextView currentPositionTextView = (TextView)findViewById(R.id.currentPosition);
			currentPositionTextView.setText("You are at " + Double.toString(longitude) + ", " +Double.toString(latitude));
		} else {
			Log.e("onCreate", "lastKnownLocation is NULL");
		}
		
		
		
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {

			// Called when a new location is found by the location provider.
			public void onLocationChanged(Location location) {
				Log.d("onCreate", "locationListener.onLocationChanged");
				double longitude = location.getLongitude();
				double latitude = location.getLatitude();
				TextView currentPositionTextView = (TextView)findViewById(R.id.currentPosition);
				currentPositionTextView.setText("You are at " + Double.toString(longitude) + ", " +Double.toString(latitude));
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

		
		// Get Message from Device
		Thread rThread = new Thread(new ReadThread());
		rThread.start();
		
		// Send Message to Device
		Thread sThread = new Thread(new SendThread());
		sThread.start();
	}	
	
    public class ReadThread implements Runnable {
        public void run() {
            try {
                Log.d("ReadThread", "Connecting.");

                DeviceConnector c = new DeviceConnector("158.130.103.42", 19107);
                c.readData();
                TextView currentPositionTextView = (TextView)findViewById(R.id.currentPosition);
				currentPositionTextView.setText("Your destination is " + Long.toString(c.longitude) + ", " + Long.toString(c.latitude));
                
                Log.d("ReadThread", "Closed.");
            } catch (Exception e) {
                Log.e("ReadThread", "Error", e);
            }
        }
    }

    public class SendThread implements Runnable {
        public void run() {
            try {
                Log.d("SendThread", "Connecting.");
                
                DeviceConnector c = new DeviceConnector("158.130.103.42", 19107);
                c.sendMessage("Msg from Android app.\0");
                
                Log.d("SendThread", "Closed.");
            } catch (Exception e) {
                Log.e("SendThread", "Error", e);
            }
        }
    }
    
	public void onBackToMainButtonClick(View view){
		finish();
	}
}
