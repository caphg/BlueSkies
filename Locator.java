package hr.hrvoje.weather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 *Listener class to get coordinates 
*/  
public class Locator  implements LocationListener {  
	private final Context mContext;

	// flag for GPS status
	public boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	private Location loc;
	private TextView la, lo;

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public Locator(Context context, TextView la, TextView lo) {
	    this.mContext = context;
	    this.la=la;
	    this.lo=lo;
	    //getLocation();
	}
	//getters, setters
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	public Location getMyLocation(){
		return this.loc;
	}

	/*private void getLoc(){
		LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		// set preferred provider based on the best accuracy possible
		Criteria fineAccuracyCriteria = new Criteria();
		fineAccuracyCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String preferredProvider = manager.getBestProvider(fineAccuracyCriteria, true);
		
		LocationListener listener = new LocationListener() {

			public void onLocationChanged(Location location) {
			   if (location != null) {

			       double lat = (location.getLatitude());
			       double lng = (location.getLongitude());
			       Log.d("lat", String.valueOf(lat));
			       la.setText(String.valueOf(lat));
			       lo.setText(String.valueOf(lng));
			   } else {
			       la.setText("Provider not available");
			       lo.setText("Provider not available");
			   }
			}
	        

	        public void onStatusChanged(String provider, int status, Bundle extras) {

	        }

	        public void onProviderEnabled(String provider) {}

	        public void onProviderDisabled(String provider) {}
	    };
	    manager.requestLocationUpdates(preferredProvider, 0, 0, listener);
	 // get a fast fix - cached version
	    updateLocation(manager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
	    
	}*/
	/*private void updateLocation(Location location) {
	    if (location == null)
	        return;

	    // save location details
	    latitude = (float) location.getLatitude();
	    longitude = (float) location.getLongitude();        
	}*/
	


	/**
	 * Function to get the user's current location
	 * @return
	 */
	public void getLocation() {
	    try {
	        locationManager = (LocationManager) mContext
	                .getSystemService(Context.LOCATION_SERVICE);

	        // getting GPS status
	        isGPSEnabled = locationManager
	                .isProviderEnabled(LocationManager.GPS_PROVIDER);
	        //Log.v("isGPSEnabled", "=" + isGPSEnabled);
	        // getting network status
	        isNetworkEnabled = locationManager
	                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	      //  Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

	        if (isGPSEnabled == false && isNetworkEnabled == false) {
	        	Toast.makeText(mContext, "Unable to get location, check Location settings", Toast.LENGTH_LONG).show();
	        } else {
	            this.canGetLocation = true;
	            if (isNetworkEnabled) {
	                locationManager.requestLocationUpdates(
	                        LocationManager.NETWORK_PROVIDER,
	                        0,
	                        0, this);
	           //     Log.d("Network", "Network");
	                if (locationManager != null) {
	                    location = locationManager
	                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                    if (location != null) {
	                        la.setText(String.valueOf(location.getLatitude()));
	                        lo.setText(String.valueOf(location.getLongitude()));
	                    }
	                }
	            }
	            // if GPS Enabled get lat/long using GPS Services
	            if (isGPSEnabled) {
	            //	Log.d("GPS Enabled", "GPS Enabledaa");
	                //if (location == null) {
	            	
	                    locationManager.requestLocationUpdates(
	                            LocationManager.GPS_PROVIDER,
	                            0,
	                            0, this);
	               //     Log.d("GPS Enabled", "GPS Enabled");
	                    if (locationManager != null) {
	                        location = locationManager
	                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                        if (location != null) {
	                        	la.setText(String.valueOf(location.getLatitude()));
		                        lo.setText(String.valueOf(location.getLongitude()));
	                        }
	                    }
	               // }
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
	    if (locationManager != null) {
	        locationManager.removeUpdates(Locator.this);
	    }
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
	    if (location != null) {
	        latitude = location.getLatitude();
	    }

	    // return latitude
	    return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
	    if (location != null) {
	        longitude = location.getLongitude();
	    }

	    // return longitude
	    return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
	    return this.canGetLocation;
	}

	/**
	 * 
	 * shows settings on tap
	 * */
	public void showSettingsAlert() {
	    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

	    // Setting Dialog Title
	    alertDialog.setTitle("GPS settings");

	    // Setting Dialog Message
	    alertDialog
	            .setMessage("GPS is not enabled. Do you want to go to settings menu?");

	    // On pressing Settings button
	    alertDialog.setPositiveButton("Settings",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    Intent intent = new Intent(
	                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                    mContext.startActivity(intent);
	                }
	            });

	    // on pressing cancel button
	    alertDialog.setNegativeButton("Cancel",
	            new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    dialog.cancel();
	                }
	            });

	    // Showing Alert Message
	    alertDialog.show();
	}

	@Override

	public void onLocationChanged(Location loc)

	{

		

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	 }