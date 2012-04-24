package edu.upenn.cis542.route;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;


public class MapOverlay extends com.google.android.maps.Overlay {
    Road mRoad;
    ArrayList<GeoPoint> mPoints;
    Drawable sMarker;
    Drawable dMarker;
    double m_fromLat;
    double m_fromLon;
    double m_toLat;
    double m_toLon;
    public static int pastRoadColor = Color.DKGRAY;
    public static int suggestedRoadColor = Color.BLUE;
    boolean m_isSuggestedRoad;                               //boolean to tell if the route is past or suggested

    public MapOverlay(Road road, MapView mv, Drawable s_marker, Drawable d_marker, double fromLat, double fromLon, double toLat, double toLon, boolean isSuggested) {
            mRoad = road;
            sMarker = s_marker;
            dMarker = d_marker;
            m_fromLat = fromLat;
            m_toLat = toLat;
            m_fromLon = fromLon;
            m_toLon = toLon;
            m_isSuggestedRoad = isSuggested;
            
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
            /*road can also be initialized as a an array of Points, like in the pastRoad*/
            else if(road.mPoints.length >0)
            {
            	mPoints = new ArrayList<GeoPoint>();
            	for(int i = 0; i < road.mPoints.length; i++)
            	{
            		mPoints.add(new GeoPoint((int)(road.mPoints[i].mLatitude * 1000000),
            				(int)(road.mPoints[i].mLongitude * 1000000)));
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
         
            
	}

	public void drawPath(MapView mv, Canvas canvas) {
            int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
            Paint paint = new Paint();
            if(m_isSuggestedRoad)
            {
            	paint.setColor(suggestedRoadColor);
            }
            else
            {
            	paint.setColor(pastRoadColor);
            }
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
	
}
