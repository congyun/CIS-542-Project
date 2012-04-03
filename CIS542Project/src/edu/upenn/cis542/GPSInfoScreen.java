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

		// Get the “current” location
		String provider = LocationManager.GPS_PROVIDER;
		Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
		if (lastKnownLocation != null)
		{
			Log.d("lastKnownLocation", "OK");
			double longitude = lastKnownLocation.getLongitude();
			double latitude = lastKnownLocation.getLatitude();
			TextView currentPositionTextView = (TextView)findViewById(R.id.currentPosition);
			currentPositionTextView.setText("You are at " + Double.toString(longitude) + ", " +Double.toString(latitude));
		} else {
			Log.e("lastKnownLocation", "NULL");
		}
		
		
		
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {

			// Called when a new location is found by the location provider.
			public void onLocationChanged(Location location) {
			      // do whatever with the Location object
				double longitude = location.getLongitude();
				double latitude = location.getLatitude();
				TextView currentPositionTextView = (TextView)findViewById(R.id.currentPosition);
				currentPositionTextView.setText("You are at " + Double.toString(longitude) + ", " +Double.toString(latitude));
			}
	
			public void onStatusChanged(String provider, int status, Bundle extras) {}
	
			public void onProviderEnabled(String provider) {}
	
			public void onProviderDisabled(String provider) {}
		};

		// Register listener with Location Manager to receive updates
		locationManager.requestLocationUpdates(
		             LocationManager.GPS_PROVIDER, 
						1000, // time interval
						0, // distance interval
						locationListener);

		
		// Get Remote String
		Thread cThread = new Thread(new ClientThread());
		cThread.start();

	}	
	
    public class ClientThread implements Runnable {

        public void run() {
            try {
                Log.d("ClientActivity", "C: Connecting...");
            	Socket socket = new Socket("158.130.103.42", 19107);
            	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            	BufferedReader in = new BufferedReader(new InputStreamReader(
                		socket.getInputStream()));
                out.println("Msg from Android.\0");

        		String messageFromCProgram;

        		while ((messageFromCProgram = in.readLine()) != null) {
        			Log.v("GPSInfoScreen","messageFromCProgram:  " + messageFromCProgram);
    				TextView destinationPositionTextView = (TextView)findViewById(R.id.destinationPosition);
    				destinationPositionTextView.setText(messageFromCProgram);
        		}

        		out.close();
        		in.close();
        		socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
            }
        }
    }
	
	public void onBackToMainButtonClick(View view){
		finish();
	}
}
