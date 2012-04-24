package edu.upenn.cis542;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import edu.upenn.cis542.route.MapOverlay;
import edu.upenn.cis542.route.PlacesList;
import edu.upenn.cis542.route.Point;
import edu.upenn.cis542.route.Road;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class HistoryScreen extends MapActivity {

	protected CharSequence[] _options;
	protected boolean[] _selections;
	List<Road> historyRoadList;

	// mapView Variables
	private MapView mapView;
	private MapController mapController;
	//private Road mRoad;
	//private PlacesList mList;
	private Drawable s_marker;
	private Drawable d_marker;
	// private Drawable i_marker;
	//private double fromLat, fromLon, toLat, toLon;
	List<Overlay> listOfOverlays;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		// Yufei:
		// get pastRoads records from database, pass to Road objects

		DatabaseHelper dbHelper = new DatabaseHelper(this);

		historyRoadList = new ArrayList<Road>(); // changed
		// Select All Query
		Cursor cursor = dbHelper.getAllRecords();
		if (cursor.moveToFirst()) {
			do {
				Road newRoad = new Road();
				newRoad.mStartTime = Long.parseLong(cursor.getString(1));
				newRoad.mEndTime = Long.parseLong(cursor.getString(2));
				newRoad.mStartName = cursor.getString(3);
				newRoad.mEndName = cursor.getString(4);
				String pointsInfo = cursor.getString(5);
				newRoad.mPoints = parsePoint(pointsInfo);
				historyRoadList.add(newRoad);
				Log.d("debug", "added a road.");//
			} while (cursor.moveToNext());
		}
		cursor.close();
		Log.d("cursor:",String.valueOf(cursor.isClosed()));
		dbHelper.close();

		// if list not empty ?need to determine?
		int roadNum = historyRoadList.size();
		_options = new CharSequence[roadNum];
		for (int i = 0; i < historyRoadList.size(); i++) {
			Road road = (Road) historyRoadList.get(i);
			_options[i] = roadInfoToDisplay(road);
		}
		_selections = new boolean[_options.length];

		showDialog(0);

		// mapView

		mapView = (MapView) findViewById(R.id.historymapview);
		s_marker = getResources().getDrawable(R.drawable.marker_a);
		d_marker = getResources().getDrawable(R.drawable.marker_b);
		mapView.setBuiltInZoomControls(true);

		mapView.setSatellite(false);
		mapController = mapView.getController();
		mapController.setZoom(13);
		
		listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
				.setTitle("Travel Record, please select:")
				.setMultiChoiceItems(_options, _selections,
						new DialogSelectionClickHandler())
				.setPositiveButton("Show Selected", new DialogButtonClickHandler())
				.setNegativeButton("Delete Selected",
						new DialogButtonClickHandler()).create();
	}

	public class DialogSelectionClickHandler implements
			DialogInterface.OnMultiChoiceClickListener {

		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			Log.i("MEooooo", _options[clicked] + " selected: " + selected);

		}
	}

	public class DialogButtonClickHandler implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int clicked) {
			switch (clicked) {
			case DialogInterface.BUTTON_POSITIVE:
				int selectedRouteCount = 0;
				for(int i=0; i<_options.length; i++){
					if(_selections[i] == true)
						selectedRouteCount++;
				}
				if(selectedRouteCount > 5){
					Context context = getApplicationContext();
					Toast.makeText(context,"Please select no more than 5 routes to display!", Toast.LENGTH_LONG).show();
				}
				else
					printSelectedRoads();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				deleteSelectedRoads();
				break;
			}
		}
	}

	protected void printSelectedRoads() {
		int routeCount = 0;
		for (int i = 0; i < _options.length; i++) {
			Log.i("ME", _options[i] + " selected: " + _selections[i]);
			
			if (_selections[i] == true) {
				Road road = (Road) historyRoadList.get(i);
				double fromLon = road.mPoints[0].mLongitude;
				double fromLat = road.mPoints[0].mLatitude;
				double toLon = road.mPoints[road.mPoints.length - 1].mLongitude;
				double toLat = road.mPoints[road.mPoints.length - 1].mLatitude;
				//Log.d("MapPoint", "Points: " + road.mPoints.length);
				MapOverlay mapOverlay = new MapOverlay(road, mapView, s_marker, d_marker, fromLat, fromLon, toLat, toLon,routeCount);
				listOfOverlays.add(mapOverlay);
				Log.d("MapRoute", "Added road size: " + road.mPoints.length);
				routeCount++;
			}
		}
		
		mapView.invalidate();

	}

	protected void deleteSelectedRoads() {

		DatabaseHelper dbHelper = new DatabaseHelper(this);
		for (int i = 0; i < _options.length; i++) {

			if (_selections[i] == true) {
				dbHelper.deleteRecord(i);
			}
			Log.d("debug", "road deleted from DB.");//
		}
		dbHelper.close();
	}

	public void onBackToMainButtonClick(View view) {
		finish();
	}

	// create this button! and connect
	/*
	 * public void onViewMapButtonClick(View view){
	 * 
	 * Intent intent = new Intent(this, MapRouteHistoryScreen.class);
	 * intent.putExtra("pastRoad", pastRoad[]); startActivity(intent); }
	 */

	// move this to the History Road page !!
	public Point[] parsePoint(String pointString) {

		String[] xyParsed = pointString.split(",");
		Point[] mPoints = new Point[xyParsed.length / 2];

		for (int i = 0; i < mPoints.length; i++) {

			Point newPoint = new Point();
			double lat = Double.parseDouble(xyParsed[2 * i]);
			double lon = Double.parseDouble(xyParsed[2 * i + 1]);

			newPoint.mLatitude = lat;
			newPoint.mLongitude = lon;

			mPoints[i] = newPoint;
		}

		return mPoints;

	}

	public String roadInfoToDisplay(Road road) {

		String roadInfo;
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm a");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(road.mStartTime);
		roadInfo = formatter.format(calendar.getTime()) + "\n"; // "Time:" +

		roadInfo = roadInfo + "From:" + String.format("%.2f",road.mPoints[0].mLatitude) + ","
				+ String.format("%.2f",road.mPoints[0].mLongitude) + "\n"
				+ road.mStartName + "\n";
		int end = road.mPoints.length - 1;
		roadInfo = roadInfo + "To:" + String.format("%.2f",road.mPoints[end].mLatitude) + ","
				+ String.format("%.2f",road.mPoints[end].mLongitude) + "\n"
				+ road.mEndName;

		return roadInfo;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
