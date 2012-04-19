package edu.upenn.cis542;

import edu.upenn.cis542.route.Road;
import edu.upenn.cis542.utilities.AppConstants;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
 
public class MainMenuScreen  extends Activity{
    Road pastRoad = new Road();
    
    
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
	}
	
	@Override
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

	public void onGPSInfoButtonClick(View view){
		Intent intent = new Intent(this, GPSInfoScreen.class);
		startActivity(intent);
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
