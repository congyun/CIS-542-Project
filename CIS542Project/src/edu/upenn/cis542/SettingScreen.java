package edu.upenn.cis542;

import edu.upenn.cis542.GPSInfoScreen.interestTypeSpinnerOnItemSelectedListener;
import edu.upenn.cis542.GPSInfoScreen.travelModeSpinnerOnItemSelectedListener;
import edu.upenn.cis542.route.RoadProvider;
import edu.upenn.cis542.utilities.AppConstants;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
 
public class SettingScreen  extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
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
        Log.d("Setting, defaultTravelValue", defaultTravelValue); 
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
        Log.d("Setting, defaultInterestValue", defaultInterestValue); 
        if (defaultInterestValue.equals("Food")) {
            interestSpinner.setSelection(0);
        } else if (defaultInterestValue.equals("Shopping Mall")) {
            interestSpinner.setSelection(1);
        } else if (defaultInterestValue.equals("Subway Station")) {
            interestSpinner.setSelection(2);
        }
        
        // initialize alertDistanceSpinner
        Spinner alertSpinner = (Spinner) findViewById(R.id.alertDistanceSpinner);
        ArrayAdapter<CharSequence> alertAdapter = ArrayAdapter.createFromResource(
                this, R.array.alert_distance_array, android.R.layout.simple_spinner_item);
        alertAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertSpinner.setAdapter(alertAdapter);
        alertSpinner.setOnItemSelectedListener(new alertDistanceSpinnerOnItemSelectedListener());
        int defaultAlertValue = settings.getInt(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, 1);
        Log.d("Setting, defaultAlertValue", Integer.toString(defaultAlertValue)); 
        if (defaultAlertValue == 1) {
            alertSpinner.setSelection(0);
        } else if (defaultAlertValue == 3) {
            alertSpinner.setSelection(1);
        } else if (defaultAlertValue == 5) {
            alertSpinner.setSelection(2);
        }
        
	}

    public class travelModeSpinnerOnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selectedItem = parent.getItemAtPosition(pos).toString();
            SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(AppConstants.DEFAULT_TRAVEL_MODE_KEY, selectedItem);
            editor.commit();
            Log.d("Setting, travelModeSpinner", selectedItem);
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
    
    public class interestTypeSpinnerOnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selectedItem = parent.getItemAtPosition(pos).toString();
            
            SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(AppConstants.DEFAULT_INTEREST_TYPE_KEY, selectedItem);
            editor.commit();
            Log.d("Setting, interestTypeSpinner", selectedItem);
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
    
    public class alertDistanceSpinnerOnItemSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selectedItem = parent.getItemAtPosition(pos).toString();
            
            SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            if (selectedItem.equals("1")) {
                editor.putInt(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, 1);
            } else if (selectedItem.equals("3")) {
                editor.putInt(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, 3);
            } else if (selectedItem.equals("5")) {
                editor.putInt(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, 5);
            }
            editor.commit();
            Log.d("Setting, alertDistanceSpinner", selectedItem);
        }

        public void onNothingSelected(AdapterView<?> parent) {
          // Do nothing.
        }
    }
    
	public void onBackToMainButtonClick(View view){
		finish();
	}
}
