package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
 
public class HelpScreen  extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		TextView infoTextView = (TextView)findViewById(R.id.info);
		String infoString = "CIS542 Final Project\n\n"
		                    + "Group 14\n"
		                    + "Congyun Gu (congyun@seas.upenn.edu)\n"
		                    + "Siyin Gu (gusiyin@seas.upenn.edu)\n"
		                    + "Yue Ning (yning@seas.upenn.edu)\n"
		                    + "Yufei Wang (wangyufei2009@gmail.com)\n\n"
		                    + "Please contact us if you have any problem.";
        infoTextView.setText(infoString);
	}

	public void onBackToMainButtonClick(View view){
		finish();
	}	
}
