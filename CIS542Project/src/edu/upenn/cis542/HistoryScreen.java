package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
 
public class HistoryScreen  extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		
		//Yufei:
		// get pastRoads records from database, pass to Road objects
		
	}

	public void onBackToMainButtonClick(View view){
		finish();
	}
}
