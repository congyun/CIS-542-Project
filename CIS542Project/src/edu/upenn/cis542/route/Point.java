package edu.upenn.cis542.route;

import java.io.Serializable;

public class Point implements Serializable {
    private static final long serialVersionUID = 4610178026065000395L;
    
    public String mName;
    public String mDescription;
    public String mIconUrl;
    public double mLatitude;
    public double mLongitude;
}

