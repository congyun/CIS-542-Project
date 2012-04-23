package edu.upenn.cis542.route;

import java.io.Serializable;

public class Road implements Serializable {
    private static final long serialVersionUID = -5878247963785584891L;
    
    public String mName;
    public String mDescription;
    public int mColor;
    public int mWidth;
    public double[][] mRoute = new double[][] {};
    public Point[] mPoints = new Point[] {};
    public long mStartTime = 0; // get by System.currentTimeMillis();
    public long mEndTime = 0;   // get by System.currentTimeMillis();
}
