package edu.upenn.cis542;

import edu.upenn.cis542.utilities.AppConstants;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Activity to handle the login screen, use SharedPreferences to store user info
 * use intent to switch from login screen to main menu screen
 * @author grace
 *
 */
public class LoginScreen extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /*
         * Check if we successfully logged in before. 
         * If we did, redirect to home page
         */
        SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
		if (settings.getString("logged", "").toString().equals("logged")) {
			Intent intent = new Intent(this, MainMenuScreen.class);
			startActivity(intent);
		}
    }
    
	public void onLoginButtonClick(View view){
		EditText username = (EditText) findViewById(R.id.username);
		EditText password = (EditText) findViewById(R.id.password);

		if(username.getText().toString().length() > 0 && password.getText().toString().length() > 0 ) {
			if(username.getText().toString().equals("test") && password.getText().toString().equals("test")) {
				/*
				 * So login information is correct, 
				 * we will save the Preference data
				 * and redirect to next class / home  
				 */
				SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("logged", "logged");
				editor.commit();

				Intent intent = new Intent(this, MainMenuScreen.class);
				startActivity(intent);
			}
		}
	}
	
	public void onQuitButtonClick(View view){
		finish();
	}
} 
