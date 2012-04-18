package edu.upenn.cis542.route;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

 
public class SearchPlaces {
	
	private static final String PLACES_SEARCH_URL =  "https://maps.googleapis.com/maps/api/place/search/xml?";
	  
	private static final String API_KEY= "AIzaSyDUzpiPg1XshL2lN8PqJE4S2gtUmwPxxDE";    //map api developer key
	
	
	private static boolean sensor;
	private static int radius;                         // search radius in meter
	private PlacesList m_placeslist;
	private static String type;
	private static double lat;
	private static double lon;

	public SearchPlaces(){
		
		sensor = false;
		radius = 50;
		m_placeslist = new PlacesList();
				
	}
	
	public PlacesList getNearByPlaces(double Lat, double Lon, String tp)
    {
		type = tp;
		lat = Lat;
		lon = Lon;
		System.out.println("Beginning Thread...");
		Thread sThread = new Thread(new SearchThread());            
		sThread.start();
		
		try {
			sThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Thread ending...");
		return m_placeslist;
		
    }
	 
    public class SearchThread implements Runnable {
    	public void run() {
                String url = getUrl(lat, lon, type);
                InputStream is = getConnection(url);
                m_placeslist = getPlaces(is);
        }
    }
	protected PlacesList getPlaces(InputStream is) {
		 XMLHandler handler = new XMLHandler();
         System.out.println("Create handler...");
         try {
                 SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                 parser.parse(is, handler);
                 
         } catch (ParserConfigurationException e) {
                 e.printStackTrace();
         } catch (SAXException e) {
                 e.printStackTrace();
         } catch (IOException e) {
                 e.printStackTrace();
         }
         System.out.println("parse ended... got "+handler.mPlacesList.results.size()+" places");
         return handler.mPlacesList;
	}


	protected InputStream getConnection(String url) {
		InputStream is = null;
        try {
                URLConnection conn = new URL(url).openConnection();
                is = conn.getInputStream();
        } catch (MalformedURLException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        System.out.println("Got connection...");
        return is;
	}


	protected String getUrl(double Lat, double  Lon, String type) {
												 
	  
     StringBuffer urlString = new StringBuffer();
     urlString.append(PLACES_SEARCH_URL);
     urlString.append("&location="); 
     urlString.append(Double.toString(Lat));
     urlString.append(",");
     urlString.append(Double.toString(Lon));
     urlString.append("&type=");
     urlString.append(type);
     urlString.append("&sensor=");
     urlString.append(Boolean.toString(sensor));
     urlString.append("&radius=");
     urlString.append(Integer.toString(radius));
     urlString.append("&key=");
     urlString.append(API_KEY);
     System.out.println("Query URL: " + urlString.toString());
     return urlString.toString();
	}
	
	/*Testing main method*/
	public static void main(String [] args)
	{
		SearchPlaces search = new SearchPlaces();
		PlacesList list = search.getNearByPlaces(39.952881, -75.209437, "bar");
		System.out.println("Getting nearby places ended. result size = "+list.results.size());
		for(int i = 0; i < list.results.size(); i++)
		{
			System.out.println("In main: "+list.results.get(i).toString());
		}
	}

}
class XMLHandler extends DefaultHandler{
	PlacesList mPlacesList;
	Place currentPlace;
	private StringBuilder builder;
	 
	
	 @Override
	public void characters(char[] ch, int start, int length)
	            throws SAXException {
	        super.characters(ch, start, length);
	        builder.append(ch, start, length);
	       
    }

	 @Override
	    public void startDocument() throws SAXException {
	        super.startDocument();
	        mPlacesList = new PlacesList();
	        builder = new StringBuilder();
	        System.out.println("Start Document...");
	    }

	 @Override
	    public void startElement(String uri, String localName, String name,
	            Attributes attributes) throws SAXException {
	        super.startElement(uri, localName, name, attributes);
	        //System.out.println("name: "+name);
	        if (name.equalsIgnoreCase("result")){
	            this.currentPlace = new Place();
	            System.out.println("Getting "+mPlacesList.results.size()+" th place...");
	        }
	    }
	 @Override
	 	
	    public void endElement(String uri, String localName, String name)
	            throws SAXException {
	        super.endElement(uri, localName, name);
	        if (this.currentPlace != null){
	            if (name.equalsIgnoreCase("name")){
	            	currentPlace.setName(builder.toString());
	            } else if (name.equalsIgnoreCase("vicinity")){
	            	currentPlace.setVicinity(builder.toString());
	            } else if (name.equalsIgnoreCase("type")){
	            	currentPlace.setType(builder.toString());
	            } else if (name.equalsIgnoreCase("rating")){
	            	currentPlace.setRating(builder.toString());
	            } else if(name.equalsIgnoreCase("reference")){
	            	currentPlace.setReference(builder.toString());
	            }else if(name.equalsIgnoreCase("lat")){
	            	currentPlace.setLatitude(builder.toString());
	            }else if(name.equalsIgnoreCase("lng")){
	            	currentPlace.setLontitude(builder.toString());
	            }else if (name.equalsIgnoreCase("result")){
	            	mPlacesList.add(currentPlace);
	            	System.out.println(currentPlace.name+" added");
	            }
	            builder.setLength(0);    
	        }
	    }
	
}
