package edu.upenn.cis542.route;


import java.util.ArrayList;

public class Place {
	 
	 
	 public String id;
	 public String name;
	 public String reference;
	 public double rating;
	 public String vicinity;
	 public ArrayList<String> type;                   //can have multiple types
	 public double latitude;
     public double longtitude;
	
	 public Place()
	 {
		 type = new ArrayList<String>();
	 }
	 @Override
	 public String toString() {
	  return name + " - " + type.get(0)+ " - " +vicinity + " - "+latitude+" "+longtitude;
	 }
	public void setName(String string) {
		this.name = string;
	}
	public void setVicinity(String string) {
		this.vicinity = string;
		
	}
	public void setType(String string) {
		this.type.add(string);
		
	}
	public void setRating(String string) {
		this.rating = Double.valueOf(string);
		
	}
	public void setReference(String string) {
		this.reference = string;
		
	}
	
	public void setLatitude(String string){
		this.latitude = Double.valueOf(string);
	}
	
	public void setLontitude(String string){
		this.longtitude = Double.valueOf(string);
	}
	  
}