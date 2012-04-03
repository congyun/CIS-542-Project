package edu.upenn.cis542;

import android.app.Activity;
import android.content.Intent;
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
		setContentView(R.layout.main_menu);
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
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
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
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("logged");
		editor.commit();
		finish();		
	}
}
