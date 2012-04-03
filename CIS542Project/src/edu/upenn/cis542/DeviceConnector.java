package edu.upenn.cis542;

import java.io.*;
import java.net.*;

import android.util.Log;

public class DeviceConnector {
    
    private String serverAddr;
    private int serverPort;
    public long latitude = 0;
    public long longitude = 0;
    public boolean isPositiveLat = true;
    public boolean isPositiveLong = true;

    DeviceConnector(String addr, int port) {
    	this.serverAddr = addr;
        this.serverPort = port;
    }

    public void readData() throws IOException {
    	Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
        	socket = new Socket(serverAddr, serverPort);
       		out = new PrintWriter(socket.getOutputStream(), true);
            	in = new BufferedReader(new InputStreamReader(
            		socket.getInputStream()));
        } catch (UnknownHostException e) {
        	Log.e("readData", "Don't know about host");
        } catch (IOException e) {
        	Log.e("readData", "Couldn't get I/O for the connection.");
        }

		out.println("GPSDATA\0");
		String temp = in.readLine();
		Log.d("readData", "temp:"+temp);
		parseData(temp);
	
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
    	Log.d("parseData","lat:"+lat);
    	this.latitude = Long.valueOf(lat);
    	String longi = input.substring(sepIndex + 2);
    	Log.d("parseData","longi:"+longi);
    	this.longitude = Long.valueOf(longi);
    }

    public void sendMessage(String msg) throws IOException {
    	Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
        	socket = new Socket(serverAddr, serverPort);
       		out = new PrintWriter(socket.getOutputStream(), true);
            	in = new BufferedReader(new InputStreamReader(
            		socket.getInputStream()));
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
