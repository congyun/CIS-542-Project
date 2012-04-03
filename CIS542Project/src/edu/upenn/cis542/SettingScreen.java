package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
 
public class SettingScreen  extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
	}

	public void onBackToMainButtonClick(View view){
		finish();
	}
}
