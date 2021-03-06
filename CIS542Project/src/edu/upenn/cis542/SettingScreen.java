package edu.upenn.cis542;


import edu.upenn.cis542.utilities.AppConstants;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
 
public class SettingScreen  extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		// get SharedPreferences
		SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);

		// Define a listener class that responds to travelModeSpinner updates
	    class travelModeSpinnerOnItemSelectedListener implements OnItemSelectedListener {
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
        } else {
            travelSpinner.setSelection(0); // "Walking"
        }

        // Define a listener class that responds to interestTypeSpinner updates
        class interestTypeSpinnerOnItemSelectedListener implements OnItemSelectedListener {
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
        } else if (defaultInterestValue.equals("Bar")) {
            interestSpinner.setSelection(1);
        } else if (defaultInterestValue.equals("Store")) {
            interestSpinner.setSelection(2);
        } else if (defaultInterestValue.equals("School")) {
            interestSpinner.setSelection(3);
        } else {
            interestSpinner.setSelection(0); // "Food"
        }
        
        // Define a listener class that responds to alertDistanceSpinner updates
        class alertDistanceSpinnerOnItemSelectedListener implements OnItemSelectedListener {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedItem = parent.getItemAtPosition(pos).toString();
                
                SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                if (selectedItem.equals("0.2")) {
                    editor.putFloat(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, (float) 0.2);
                } else if (selectedItem.equals("0.4")) {
                    editor.putFloat(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, (float) 0.4);
                } else if (selectedItem.equals("0.8")) {
                    editor.putFloat(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, (float) 0.8);
                } else if (selectedItem.equals("1.5")) {
                    editor.putFloat(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, (float) 1.5);
                } else if (selectedItem.equals("3")) {
                    editor.putFloat(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, (float) 3);
                }
                editor.commit();
                Log.d("Setting, alertDistanceSpinner", selectedItem);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        }
        // initialize alertDistanceSpinner
        Spinner alertSpinner = (Spinner) findViewById(R.id.alertDistanceSpinner);
        ArrayAdapter<CharSequence> alertAdapter = ArrayAdapter.createFromResource(
                this, R.array.alert_distance_array, android.R.layout.simple_spinner_item);
        alertAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertSpinner.setAdapter(alertAdapter);
        alertSpinner.setOnItemSelectedListener(new alertDistanceSpinnerOnItemSelectedListener());
        float defaultAlertValue = settings.getFloat(AppConstants.DEFAULT_ALERT_DISTANCE_KEY, (float) 0.2);
        Log.d("Setting, defaultAlertValue", Float.toString(defaultAlertValue)); 
        if (Math.abs(defaultAlertValue - 0.2) < 0.00001) {
            Log.d("Setting, defaultAlertValue", "=0.2");
            alertSpinner.setSelection(0);
        } else if (Math.abs(defaultAlertValue - 0.4) < 0.00001) {
            Log.d("Setting, defaultAlertValue", "=0.4");
            alertSpinner.setSelection(1);
        } else if (Math.abs(defaultAlertValue - 0.8) < 0.00001) {
            Log.d("Setting, defaultAlertValue", "=0.8");
            alertSpinner.setSelection(2);
        } else if (Math.abs(defaultAlertValue - 1.5) < 0.00001) {
            Log.d("Setting, defaultAlertValue", "=1.5");
            alertSpinner.setSelection(3);
        } else if (Math.abs(defaultAlertValue - 3) < 0.00001) {
            Log.d("Setting, defaultAlertValue", "=3");
            alertSpinner.setSelection(4);
        } else {
            Log.d("Setting, defaultAlertValue", "= Not Match, choose 0.2");
            alertSpinner.setSelection(0); // "0.2"
        }
	}

	public void onClearTravelHistoryButtonClick(View view){
	    DatabaseHelper dbHelper = new DatabaseHelper(this);
	    dbHelper.deleteAllRecords();
	    Toast.makeText(getApplicationContext(), "Clear All Travel History OK!", Toast.LENGTH_SHORT).show();
    }
	
	public void onBackToMainButtonClick(View view){
		finish();
	}
}
