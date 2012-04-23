package edu.upenn.cis542;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.upenn.cis542.route.*;
import edu.upenn.cis542.utilities.DeviceConnector;

public class MapRouteScreen extends MapActivity {

        private LinearLayout linearLayout;
        private MapView mapView;
        private Road mRoad;
        private PlacesList mList;
        private Drawable s_marker;
        private Drawable d_marker;
        private Drawable i_marker;
        
        private String roadInfoToC; // msg will be sent to C program
        
        /*Params that need to be passed from main program*/
        private double fromLat, fromLon, toLat, toLon;
        private RoadProvider.Mode mode;
        private String i_type;
        private Road pastRoad; // contains at least the current position

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            // Called when a new location is found by the location provider.
            public void onLocationChanged(Location location) {
                Log.d("MapRoute, locationListener", "onLocationChanged");
            }
    
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("MapRoute, locationListener", "onStatusChanged");
            }
    
            public void onProviderEnabled(String provider) {
                Log.d("MapRoute, locationListener", "onProviderEnabled");
            }
    
            public void onProviderDisabled(String provider) {
                Log.d("MapRoute, locationListener", "onProviderDisabled");
            }
        };
        
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.map_route);
                mapView = (MapView) findViewById(R.id.mapview);
                s_marker = getResources().getDrawable(R.drawable.marker_a);
                d_marker = getResources().getDrawable(R.drawable.marker_b);
                i_marker = getResources().getDrawable(R.drawable.heart);
                mapView.setBuiltInZoomControls(true);
               
                
                // get params from GPSInfoScreen
                fromLon = getIntent().getDoubleExtra("fromLon", 0.0);
                fromLat = getIntent().getDoubleExtra("fromLat", 0.0);
                toLon = getIntent().getDoubleExtra("toLon", 0.0);
                toLat = getIntent().getDoubleExtra("toLat", 0.0);
                mode = (edu.upenn.cis542.route.RoadProvider.Mode) getIntent().getExtras().get("mode");
                i_type = getIntent().getStringExtra("i_type");
                Log.d("MapRoute, fromLon", Double.toString(fromLon));
                Log.d("MapRoute, fromLat", Double.toString(fromLat));
                Log.d("MapRoute, toLon", Double.toString(toLon));
                Log.d("MapRoute, toLat", Double.toString(toLat));
                if (mode == RoadProvider.Mode.WALKING) {
                    Log.d("MapRoute, mode", "WALKING");
                } else if (mode == RoadProvider.Mode.BICYCLING) {
                    Log.d("MapRoute, mode", "BICYCLING");
                } else if (mode == RoadProvider.Mode.DRIVING) {
                    Log.d("MapRoute, mode", "DRIVING");
                }
                Log.d("MapRoute, i_type", i_type);

                // get pastRoad, contains at least the current position coordinates, mStartTime, mEndTime
                pastRoad = (edu.upenn.cis542.route.Road) getIntent().getExtras().get("pastRoad");
                
                // Congyun TODO:
                // query? and draw pastRoad
                
                Thread rThread = new Thread() {
                        @Override
                        public void run() {
                                String url = RoadProvider.getUrl(fromLat, fromLon, toLat, toLon,mode);
                                InputStream is = RoadProvider.getConnection(url);
                                mRoad = RoadProvider.getRoute(is);
                                mHandler.sendEmptyMessage(0);
                        }
                };
                rThread.start();
                try {
                    rThread.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                SearchPlaces search = new SearchPlaces();
                mList = search.getNearByPlaces(fromLat, fromLon, i_type);
        		Log.v("SearchPlaces", String.valueOf(mList.results.size()));
        		for(int i = 0; i < mList.results.size(); i++)
        		{
        			Log.v("SearchPlaces", mList.results.get(i).toString());
        		}
        		
        		// parse mRoad.mDescription to get roadInfoToC
        		String[] infos = mRoad.mDescription.split("[ )]");
        		if (infos.length > 4) {
        		    // Known description format: "Distance: 1.0mi (about 19 mins)"
        		    roadInfoToC = infos[1] + "," + infos[3] + " " + infos[4];
        		} else {
        		    // Unknown description format
        		    roadInfoToC = mRoad.mDescription;
        		}
        		
        		Log.d("mRoad.mDescription", mRoad.mDescription);
        		Log.d("roadInfoToC", roadInfoToC);
        		
        		// Send Message to Device
        	    Thread sThread = new Thread(new SendThread());
                sThread.start();
        
        		
        		// Get LocationManager
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                // Register listener with Location Manager to receive updates
                locationManager.requestLocationUpdates(
                             LocationManager.GPS_PROVIDER, 
                                1000, // time interval
                                0, // distance interval
                                locationListener);
                Log.d("MapRouteScreen", "Register locationListener");
        }

        // this handle change the description and mapview widgets
        Handler mHandler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                        TextView textView = (TextView) findViewById(R.id.description);
                        textView.setText(mRoad.mName + ", " + mRoad.mDescription);
                        MapOverlay mapOverlay = new MapOverlay(mRoad, mList,mapView, s_marker,d_marker, i_marker, fromLat, fromLon, toLat,toLon);
                        List<Overlay> listOfOverlays = mapView.getOverlays();
                        listOfOverlays.clear();
                        listOfOverlays.add(mapOverlay);
                        mapView.invalidate();
                };
        };

        @Override
        protected boolean isRouteDisplayed() {
                return false;
        }
        
        
        // Conyun TODO:
        // provide a function like destinationChange(double toLat, double toLon)
        // which will be called when the destination position changed.
        // may need to requery the route and repaint
        // Not very important since temp don't know how to test this....
        
        
        public void onBackToMainButtonClick(View view) {
            // Get LocationManager
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Remove the location updates listener
            locationManager.removeUpdates(locationListener);
            Log.d("MapRouteScreen", "Remove locationListener");
            
            // create the Intent object to send BACK to the caller
            Intent i = new Intent();
            // put the pastRoad object into the Intent
            i.putExtra("pastRoad", pastRoad);
            setResult(RESULT_OK, i);
            
            finish();
        }
        
        
        public class SendThread implements Runnable {
            public void run() {
                try {
                    Log.d("SendThread", "Connecting");
                    
                    DeviceConnector c = new DeviceConnector();
                    c.sendMessage(roadInfoToC);

                    Log.d("SendThread", "Finished");
                } catch (Exception e) {
                    Log.e("SendThread", "Exception");
                }
            }
        }
}


class MapOverlay extends com.google.android.maps.Overlay {
        Road mRoad;
        PlacesList mList;
        ArrayList<GeoPoint> mPoints;
        Drawable sMarker;
        Drawable dMarker;
        Drawable iMarker;
        double m_fromLat;
        double m_fromLon;
        double m_toLat;
        double m_toLon;

        public MapOverlay(Road road, PlacesList list, MapView mv, Drawable s_marker, Drawable d_marker, Drawable i_marker, double fromLat, double fromLon, double toLat, double toLon) {
                mRoad = road;
                mList = list;
                sMarker = s_marker;
                dMarker = d_marker;
                iMarker = i_marker;
                m_fromLat = fromLat;
                m_toLat = toLat;
                m_fromLon = fromLon;
                m_toLon = toLon;
                
                if (road.mRoute.length > 0) {
                        mPoints = new ArrayList<GeoPoint>();
                        for (int i = 0; i < road.mRoute.length; i++) {
                                mPoints.add(new GeoPoint((int) (road.mRoute[i][1] * 1000000),
                                                (int) (road.mRoute[i][0] * 1000000)));
                        }
                        int moveToLat = (mPoints.get(0).getLatitudeE6() + (mPoints.get(
                                        mPoints.size() - 1).getLatitudeE6() - mPoints.get(0)
                                        .getLatitudeE6()) / 2);
                        int moveToLong = (mPoints.get(0).getLongitudeE6() + (mPoints.get(
                                        mPoints.size() - 1).getLongitudeE6() - mPoints.get(0)
                                        .getLongitudeE6()) / 2);
                        GeoPoint moveTo = new GeoPoint(moveToLat, moveToLong);

                        MapController mapController = mv.getController();
                        mapController.animateTo(moveTo);
                        mapController.setZoom(7);
                }
        }

        @Override
        public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
                super.draw(canvas, mv, shadow);
                drawPath(mv, canvas);
                drawMarker(mv, canvas);
                return true;
        }

        private void drawMarker(MapView mv, Canvas canvas) {
        	/*draw route markers */
        	 GeoPoint s_p = new GeoPoint( (int) (m_fromLat * 1E6), (int) (m_fromLon * 1E6));
        	 Point s_screenPts = new Point();
             mv.getProjection().toPixels(s_p, s_screenPts);
             sMarker.setBounds(s_screenPts.x-10, s_screenPts.y-10, s_screenPts.x+10, s_screenPts.y+10);
             sMarker.draw(canvas);
        	// Log.v("DrawMarker", m_toLat+ " "+m_toLon);
             GeoPoint d_p = new GeoPoint( (int) (m_toLat * 1E6), (int) (m_toLon * 1E6));
            // Log.v("DrawMarker", d_p.getLatitudeE6()+ " "+d_p.getLongitudeE6());
        	 Point d_screenPts = new Point();
             mv.getProjection().toPixels(d_p, d_screenPts);
             dMarker.setBounds(d_screenPts.x-10, d_screenPts.y-10, d_screenPts.x+10, d_screenPts.y+10);
            
             dMarker.draw(canvas);         
             
             /*draw points of interest markers*/
             ArrayList<GeoPoint> i_plist = new ArrayList<GeoPoint>();
             for(int i = 0; i < mList.results.size(); i++)
             {
            	 GeoPoint i_p = new GeoPoint( (int) (mList.results.get(i).latitude * 1E6), (int) (mList.results.get(i).longtitude * 1E6));
            	 Point i_screenPts = new Point();
                 mv.getProjection().toPixels(i_p, i_screenPts);
                 iMarker.setBounds(i_screenPts.x-10, i_screenPts.y-10, i_screenPts.x+10, i_screenPts.y+10);
                 iMarker.draw(canvas);     
                 
       
             }
             
             
                
		}

		public void drawPath(MapView mv, Canvas canvas) {
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
                Paint paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                for (int i = 0; i < mPoints.size(); i++) {
                        Point point = new Point();
                        mv.getProjection().toPixels(mPoints.get(i), point);
                        x2 = point.x;
                        y2 = point.y;
                        if (i > 0) {
                                canvas.drawLine(x1, y1, x2, y2, paint);
                        }
                        x1 = x2;
                        y1 = y2;
                }
        }
		
		/*Response when tap a marker on screen*/
		public boolean onTap(GeoPoint p, MapView mapView)
		{
	 
			String i_name = null;
			String i_vicinity = null;
			double i_rating;
			
			for(int i = 0; i < mList.results.size()-3; i++)
            {
				GeoPoint dp = new GeoPoint((int) (mList.results.get(i).latitude *1E6), (int)(mList.results.get(i).longtitude *1E6));
				if(dp.equals(p))
				{
					Log.v("onTap", "hit!");
					i_name = mList.results.get(i).name;
					i_vicinity = mList.results.get(i).vicinity;
					i_rating = mList.results.get(i).rating;
					
					SimpleItemizedOverlay itemizedOverlay;
		            itemizedOverlay = new SimpleItemizedOverlay(iMarker, mapView);
		      		OverlayItem overlayItem = new OverlayItem(p, i_name, i_vicinity); 
		      		itemizedOverlay.addOverlay(overlayItem);
		      		List<Overlay> listOfOverlays = mapView.getOverlays();
		      		listOfOverlays.add(itemizedOverlay);
		      		mapView.invalidate();
				}
            }
			 
			return false;
		}
}