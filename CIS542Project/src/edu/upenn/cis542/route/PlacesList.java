package edu.upenn.cis542.route;


import java.util.ArrayList;

public class PlacesList {
  
 public String status;
 public ArrayList<Place> results = new ArrayList<Place>();
public void add(Place currentPlace) {
	this.results.add(currentPlace);
	}

}
