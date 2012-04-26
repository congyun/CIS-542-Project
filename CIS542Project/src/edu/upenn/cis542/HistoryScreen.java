package edu.upenn.cis542;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import edu.upenn.cis542.route.MapOverlay;
import edu.upenn.cis542.route.Point;
import edu.upenn.cis542.route.Road;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HistoryScreen extends MapActivity {

	protected CharSequence[] _options;
	protected boolean[] _selections;
	List<Road> historyRoadList;
	protected int[] selectedIndex = { 0, 0, 0, 0, 0 };

	// mapView Variables
	private MapView mapView;
	private MapController mapController;
	private Drawable s_marker;
	private Drawable d_marker;
	List<Overlay> listOfOverlays;
	int colors[] = { Color.BLUE, Color.GRAY, Color.RED, Color.CYAN,
			Color.GREEN, Color.YELLOW };

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
				newRoad.mId = cursor.getString(0);
				newRoad.mStartTime = Long.parseLong(cursor.getString(1));
				newRoad.mEndTime = Long.parseLong(cursor.getString(2));
				newRoad.mStartName = cursor.getString(3);
				newRoad.mEndName = cursor.getString(4);
				String pointsInfo = cursor.getString(5);
				newRoad.mPoints = parsePoint(pointsInfo);
				historyRoadList.add(newRoad);
				Log.d("HistoryScreen, added a road", "id="+newRoad.mId+", "+newRoad.mStartTime+", "+newRoad.mEndTime+", "+newRoad.mStartName+", "+newRoad.mEndName+": "+pointsInfo);//
			} while (cursor.moveToNext());
		}
		cursor.close();
		Log.d("HistoryScreen, cursor.isClosed()", String.valueOf(cursor.isClosed()));
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
				.setPositiveButton("Show Selected",
						new DialogButtonClickHandler())
				.setNegativeButton("Delete Selected",
						new DialogButtonClickHandler()).create();
	}

	public class DialogSelectionClickHandler implements
			DialogInterface.OnMultiChoiceClickListener {

		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			Log.i("DialogSelectionClickHandler", _options[clicked] + " selected: " + selected);

		}
	}

	public class DialogButtonClickHandler implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int clicked) {
			switch (clicked) {
			case DialogInterface.BUTTON_POSITIVE:
				int selectedRouteCount = 0;
				for (int i = 0; i < _options.length; i++) {
					if (_selections[i] == true) {
						selectedRouteCount++;
					}
				}
				if (selectedRouteCount > 5) {
					Context context = getApplicationContext();
					Toast.makeText(context,
							"Please select no more than 5 routes to display!",
							Toast.LENGTH_LONG).show();
					// exit history screen
					finish();
				} else {
				    printSelectedRoads();
				}
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				deleteSelectedRoads();
				// exit history screen
				finish();
				break;
			}
		}
	}

	private int findButtonId(int id) {
		int buttonId;
		switch (id) {
		case 1:
			buttonId = R.id.route1Button;
			break;
		case 2:
			buttonId = R.id.route2Button;
			break;
		case 3:
			buttonId = R.id.route3Button;
			break;
		case 4:
			buttonId = R.id.route4Button;
			break;
		case 5:
		    buttonId = R.id.route5Button;
            break;
		default:
			buttonId = R.id.route5Button;
			break;
		}

		return buttonId;
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
				// Log.d("MapPoint", "Points: " + road.mPoints.length);
				MapOverlay mapOverlay = new MapOverlay(road, mapView, s_marker,
						d_marker, fromLat, fromLon, toLat, toLon, routeCount);
				listOfOverlays.add(mapOverlay);
				Log.d("MapRoute", "Added road size: " + road.mPoints.length);

				// show buttons
				selectedIndex[routeCount] = i;
				int buttonId = findButtonId(routeCount + 1);
				Button button = (Button) findViewById(buttonId);
				button.setVisibility(0);
				button.setBackgroundColor(colors[routeCount]);

				routeCount++;
			}
		}

		mapView.invalidate();

	}

	protected void deleteSelectedRoads() {
		DatabaseHelper dbHelper = new DatabaseHelper(this);
		for (int i = 0; i < _options.length; i++) {
			if (_selections[i] == true) {
			    String selectRoadId = historyRoadList.get(i).mId;
				dbHelper.deleteRecord(selectRoadId);
	            Log.d("HistoryScreen, deleteded a road", "id="+selectRoadId);
			}
		}
		dbHelper.close();
	}

	public void onBackToMainButtonClick(View view) {
		finish();
	}

	public void onroute1ButtonClick(View view) {
		Road road = (Road) historyRoadList.get(selectedIndex[0]);
		routeInfoOnButtonClick(road);
	}

	public void onroute2ButtonClick(View view) {
		Road road = (Road) historyRoadList.get(selectedIndex[1]);
		routeInfoOnButtonClick(road);
	}

	public void onroute3ButtonClick(View view) {
		Road road = (Road) historyRoadList.get(selectedIndex[2]);
		routeInfoOnButtonClick(road);
	}

	public void onroute4ButtonClick(View view) {
		Road road = (Road) historyRoadList.get(selectedIndex[3]);
		routeInfoOnButtonClick(road);
	}

	public void onroute5ButtonClick(View view) {
		Road road = (Road) historyRoadList.get(selectedIndex[4]);
		routeInfoOnButtonClick(road);
	}

	public void routeInfoOnButtonClick(Road road) {

		String roadInfo;

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm a");
		Calendar calendar = Calendar.getInstance();

		calendar.setTimeInMillis(road.mStartTime);
		roadInfo = "Start Time: " + formatter.format(calendar.getTime()) + "\n";

		roadInfo = roadInfo + "Start From: "
				+ String.format("%.2f", road.mPoints[0].mLatitude) + ","
				+ String.format("%.2f", road.mPoints[0].mLongitude) + "\n"
				+ road.mStartName + "\n";

		int end = road.mPoints.length - 1;
		calendar.setTimeInMillis(road.mEndTime);
		roadInfo = roadInfo + "End Time: "
				+ formatter.format(calendar.getTime()) + "\n";
		roadInfo = roadInfo + "End At: "
				+ String.format("%.2f", road.mPoints[end].mLatitude) + ","
				+ String.format("%.2f", road.mPoints[end].mLongitude) + "\n"
				+ road.mEndName + "\n";

		long elapseTime = road.mEndTime - road.mStartTime;
		int ss = (int)(elapseTime/1000);
		int mm = (int)(ss/60);
		int hh = (int)(mm/60);
		ss = ss - mm*60;
		mm = mm - hh*60;
		
		roadInfo = roadInfo + "Travel Time: " + String.format("%02d:%02d:%02d", hh,mm,ss) ;

		// zoom to selected route
		mapView = (MapView) findViewById(R.id.historymapview);
		int moveToLat = (int) ((road.mPoints[0].mLatitude + road.mPoints[end].mLatitude) * 1000000 / 2);
		int moveToLong = (int) ((road.mPoints[0].mLongitude + road.mPoints[end].mLongitude) * 1000000 / 2);
		GeoPoint moveTo = new GeoPoint(moveToLat, moveToLong);

		MapController mapController = mapView.getController();
		mapController.animateTo(moveTo);
		mapController.setZoom(15);

		// mapController.zoomToSpan(Math.abs(maxLatitude - minLatitude),
		// Math.abs(maxLongitude - minLongitude));

		// show route info in Toast
		Context context = getApplicationContext();
		Toast.makeText(context, roadInfo, Toast.LENGTH_LONG).show();
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

		roadInfo = roadInfo + "From:"
				+ String.format("%.2f", road.mPoints[0].mLatitude) + ","
				+ String.format("%.2f", road.mPoints[0].mLongitude) + "\n"
				+ road.mStartName + "\n";
		int end = road.mPoints.length - 1;
		roadInfo = roadInfo + "To:"
				+ String.format("%.2f", road.mPoints[end].mLatitude) + ","
				+ String.format("%.2f", road.mPoints[end].mLongitude) + "\n"
				+ road.mEndName;

		return roadInfo;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
