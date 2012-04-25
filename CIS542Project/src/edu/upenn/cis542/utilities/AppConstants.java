package edu.upenn.cis542.utilities;

public class AppConstants {
    public static final String PREFS_NAME = "GPSPrefs";
    
    public static final double UPDATE_DISTANCE_THRESHOLD = 0.0003;
    public static final int UPDATE_INTERVAL = 5000;
    public static final int UPDATE_INTERVAL_FOR_CONNECTION_LOST = 20000;
    
    public static final String DEFAULT_TRAVEL_MODE_KEY = "travel_mode";
    public static final String DEFAULT_TRAVEL_MODE_INITIAL_VALUE = "Walking";
    
    public static final String DEFAULT_INTEREST_TYPE_KEY = "interest_type";
    public static final String DEFAULT_INTEREST_TYPE_INITIAL_VALUE = "Food";
    
    public static final String DEFAULT_ALERT_DISTANCE_KEY = "alert_distance";
    public static final float DEFAULT_ALERT_DISTANCE_INITIAL_VALUE = (float) 0.2;
    
    // 43th Street & Spruce Street
    public static final double DEFAULT_FROM_LAT = 39.952881;
    public static final double DEFAULT_FROM_LON = -75.209437;
    
    // 34th Street & Walnut Street
    public static final double DEFAULT_TO_LAT = 39.952759;
    public static final double DEFAULT_TO_LON = -75.192776;
    
/*  
    <name>Saad's Halal Restaurant</name>
    <vicinity>4500 Walnut Street, Philadelphia</vicinity>
    <lat>39.9550990</lat>
    <lng>-75.2118280</lng>
    
    <name>Green Line Cafe</name>
    <vicinity>4239 Baltimore Avenue, Philadelphia</vicinity>
    <lat>39.9497100</lat>
    <lng>-75.2091510</lng>
    
    <name>The Fresh Grocer</name>
    <vicinity>4001 Walnut Street, Philadelphia</vicinity>
    <lat>39.9543680</lat>
    <lng>-75.2029830</lng>
    
    <name>Bobby's Burger Palace</name>
    <vicinity>3925 Walnut Street, Philadelphia</vicinity>
    <lat>39.9540160</lat>
    <lng>-75.2008730</lng>

    <name>Distrito Restaurant</name>
    <vicinity>3945 Chestnut Street, Philadelphia</vicinity>
    <lat>39.9560470</lat>
    <lng>-75.2020340</lng>
    
    <name>Abner's of University City</name>
    <vicinity>38 and Chestnut Streets, Philadelphia</vicinity>
    <lat>39.9552910</lat>
    <lng>-75.1989470</lng>
*/

/*  Real testing routes
    square middle:              toLon = -75.190935          toLat = 39.95213833333333
    square door near Levine:    toLon = -75.191235          toLat = 39.95221
    Levine door near 34th St:   toLon = -75.19159166666667  toLat = 39.95214166666667

 */
}
