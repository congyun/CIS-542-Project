package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
 
public class HistoryScreen  extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
	}

	public void onBackToMainButtonClick(View view){
		finish();
	}
}
