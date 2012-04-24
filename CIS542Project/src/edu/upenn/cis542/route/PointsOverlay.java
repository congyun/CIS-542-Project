package edu.upenn.cis542.route;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import edu.upenn.cis542.route.mapviewballoons.BalloonItemizedOverlay;

public class PointsOverlay extends BalloonItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> m_overlays = new ArrayList<OverlayItem>();
	static double default_lat = 39.952881;
	static double default_lon = 39.952881;
	private Location currentLocation = new Location("");
	private Context c;

	public PointsOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
	}

	public void setCurrentLocation(GeoPoint p){
		currentLocation.setLatitude(p.getLatitudeE6() / 1e6);
        currentLocation.setLongitude(p.getLongitudeE6() / 1e6);
	}
	public void addOverlay(OverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	//TODO current location and relevant info
	protected boolean onBalloonTap(int index, OverlayItem item) {
		 	String tmp = m_overlays.get(index).getTitle();
		    GeoPoint interestPoint = m_overlays.get(index).getPoint();
		    Location tmpLoc = convertGpToLoc(interestPoint);
		    double distance = ((currentLocation).distanceTo(tmpLoc))*(0.000621371192);
		    DecimalFormat df = new DecimalFormat("#.##");
		    tmp = tmp + " is " + String.valueOf(df.format(distance)) + " miles away.";
		    Toast.makeText(c,tmp,Toast.LENGTH_LONG).show();
		    return true;
		}

	private Location convertGpToLoc(GeoPoint gp) {
		Location convertedLocation = new Location("");
	    convertedLocation.setLatitude(gp.getLatitudeE6() / 1e6);
	    convertedLocation.setLongitude(gp.getLongitudeE6() / 1e6);
	    return convertedLocation;
	}
}


