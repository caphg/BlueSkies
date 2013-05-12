package hr.hrvoje.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Geocoding class for getting geodata
 * @author hrvoje
 *
 */
public class GeoCoderLocation extends AsyncTask<String, Void, List<Address>>{
	
	private Context c;
	private TextView lat,longit;
	private AutoCompleteTextView auto;
	private Activity act;
	private String fetchedAddress;
	
	public GeoCoderLocation(Activity act, Context c,  TextView lat, TextView longit, AutoCompleteTextView a){
		this.c=c;
		this.lat=lat;
		this.longit=longit;
		this.auto = a;
		this.act = act;
	}

	/**
	 * gets location name from coordinates entered
	 * Uses google maps api as backup 
	 */
    @Override
    protected List<Address> doInBackground(String... locationName) {
        // Creating an instance of Geocoder class
        
        List<Address> addresses = null;

        try {
            // maximum 5 quick attempts
        	for(int attempt=0; attempt<5; attempt++){
        		try{
        		Geocoder geocoder = new Geocoder(HelperClazz.getMainActivity().getApplicationContext());
        		addresses = geocoder.getFromLocationName(locationName[0], 3);
        		} catch(IOException ex){
        			//option not available
        		}
        		if(addresses!= null && addresses.size()>0){
        			fetchedAddress = locationName[0];
        			break;
        		} 
        		
        	}
        } catch (Exception e) {
        	//error with geocoding
        }

        try {
        	//backup plan
        	if(addresses==null || addresses.size()<=0){
        		addresses = new ArrayList<Address>();
        		String urlToFetch = "http://maps.googleapis.com/maps/api/geocode/json?address=";
        		//removes spaces from URL
        		urlToFetch += locationName[0].replaceAll(" ", "");
        		urlToFetch += "&sensor=true";
        		String googleMapsLocation = HelperClazz.getResponseString(urlToFetch);
        		JSONReporter reporter = new JSONReporter();
        		HashMap<String, String> dataMap = reporter.getLocationFromGoogleMaps(googleMapsLocation);
        		Address address = new Address(null);
        		address.setLatitude(Double.parseDouble(dataMap.get("latitude")));
        		address.setLongitude(Double.parseDouble(dataMap.get("longitude")));
        		fetchedAddress = dataMap.get("formatted_address");
        		//Log.d("fetchedadd", dataMap.get("latitude"));
        		addresses.add(address);
        	}
        } catch (Exception e) {
            return null;
            //no data will be returned
        }
        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {
    	HelperClazz.hideProgressDialog();
    	//if nothing returned
        if(addresses==null || addresses.size()==0){
        	//check network availability
        	if(!HelperClazz.isNetworkAvailable(act)){
				  Toast.makeText(HelperClazz.getMainKontext().getApplicationContext(), 
		                    "No location found, check Internet connection and try again.", 
		                    Toast.LENGTH_LONG).show();
				  return;
			  }
        	//if its not network's fault then no such location exists
            Toast.makeText(c.getApplicationContext(), "No location found", Toast.LENGTH_LONG).show();
            return;
        }

        for(int i=0;i<addresses.size();){

            Address address = (Address) addresses.get(i);
            lat.setText(String.valueOf(address.getLatitude()));
            longit.setText(String.valueOf(address.getLongitude()));
            auto.setText(fetchedAddress);
            break;
        }


    }
}