package edu.upenn.cis542.utilities;

import java.io.*;
import java.net.*;

import android.util.Log;

public class DeviceConnector {
    private String serverAddr = "158.130.103.42";
    private int serverPort = 19108;
    public long latitude = 0;
    public long longitude = 0;
    public boolean isPositiveLat = true;
    public boolean isPositiveLong = true;
    

    /**
     * Calculate the latitude according to the arduino code.
     * Unit: degree
     */
    public double getLatitude() {
    	int itgr = (int) (this.latitude / 1000000);
    	double frac = ((this.latitude - itgr * 1000000) / 10000.0) / 60.0;
    	double res = (double)itgr + frac;
    	if (!this.isPositiveLat)
    		res *= -1;
    	return res;
    }
    
    /**
     * Calculate the longitude according to the arduino code.
     * Unit: degree
     */
    public double getLongitude() {
    	int itgr = (int) (this.longitude / 1000000);
    	double frac = ((this.longitude - itgr * 1000000) / 10000.0) / 60.0;
    	double res = (double)itgr + frac;
    	if (!this.isPositiveLong)
    		res *= -1;
    	return res;
    }

    public void readData() throws IOException {
    	Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
        	socket = new Socket(serverAddr, serverPort);
       		out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
        	Log.e("readData", "Don't know about host");
        } catch (IOException e) {
        	Log.e("readData", "Couldn't get I/O for the connection.");
        }

		out.println("GPSDATA\0");
		String msg = in.readLine();
		Log.d("readData", "msg:" + msg);
		
		//for testing
		//msg = "+39571276 -75114442";
		//Log.d("readData", "changed msg to:" + msg);
		
		if (msg != null) {
			parseData(msg);
		} else {
			this.isPositiveLat = true;
			this.latitude = 0;
			this.isPositiveLong = true;
			this.longitude = 0;
		}
	
		out.close();
		in.close();
		socket.close();

    }

    private void parseData(String input) {
    	if (input.charAt(0) == '+')
    		this.isPositiveLat = true;
    	else
    		this.isPositiveLat = false;
    	int sepIndex = input.indexOf(" ");
    	if (sepIndex < 0) {
    		Log.e("parseData", "Parser Error: wrong format.");
    	}
    	if (input.charAt(sepIndex+1) == '+')
    		this.isPositiveLong = true;
    	else
    		this.isPositiveLong = false;
    	String lat = input.substring(1, sepIndex);
    	this.latitude = Long.valueOf(lat);
    	String longi = input.substring(sepIndex + 2);
    	this.longitude = Long.valueOf(longi);
    }

    public void sendMessage(String msg) throws IOException {
    	Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
        	socket = new Socket(serverAddr, serverPort);
       		out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
        	Log.e("sendData", "Don't know about host");
        } catch (IOException e) {
        	Log.e("sendData", "Couldn't get I/O for the connection.");
        }

		out.println("MESSAGE\0");
		out.println(msg+"\0");
	
		out.close();
		in.close();
		socket.close();
    }
}
