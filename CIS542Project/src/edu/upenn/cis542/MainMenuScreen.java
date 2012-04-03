package edu.upenn.cis542;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
 
public class MainMenuScreen  extends Activity{
	public static final String PREFS_NAME = "LoginPrefs";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater Inflater = getMenuInflater();
		Inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.logout) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.remove("logged");
			editor.commit();
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	
	public void onGPSInfoButtonClick(View view){
		
	}
	public void onHistoryButtonClick(View view){
		
	}
	
	public void onSettingsButtonClick(View view){
		
	}
	
	public void onHelpButtonClick(View view){
		
	}
	
	public void onLogoutButtonClick(View view){
		
	}
}
