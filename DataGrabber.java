package hr.hrvoje.weather;

import java.math.BigDecimal;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
/**
 * gets forecast data from server
 * @author hrvoje
 *
 */
public class DataGrabber extends AsyncTask<String, String, String> {
	String forecastText;
	String longitude, latitude;
	long timeVar;
	String api;
	Context c;
	String currSummary, currTemp, currPrecipProb, currClouds;
	String hourSum, hourTemp, hourPrecip;
	String unit,addressName,icon;
	
	public DataGrabber(Context c, String longitude, String latitude, long vrijeme, String unit, String addressName) {
		this.c = c;
		//this.activity = activity;
		this.forecastText="";
		this.longitude = longitude;
		this.latitude = latitude;
		this.timeVar = vrijeme;
		this.api = HelperClazz.getApi();     
		this.unit = unit;
		this.addressName=addressName;
		
		//Log.v("lokacija2", "=" + longitude);
	}
	//runs in background
	@Override
    protected String doInBackground(String... uri) {
	//	Log.d("api", api);
        try {
        	//builds url depending on entered parameters
        	String reqUrl ="https://api.forecast.io/forecast/"+api+"/"+latitude+","+longitude;
        	//time shifted forecast adds time parameter
        	if(HelperClazz.getIsShifted()) reqUrl += ","+String.valueOf(timeVar);
        //	String reqUrl ="https://api.forecast.io/forecast/"+api+"/"+latitude+","+longitude+","+String.valueOf(vrijeme)+"/";
        	if(unit.equals("C")){
        		reqUrl += "/?units=si";
        	}
        	return HelperClazz.getResponseString(reqUrl);

        } catch (Exception e) {
        	return null;
        }
	
	}
	
	//after process has finished
	@Override
    protected void onPostExecute(String result) {
		//checks if result is returned
		if(result == null || result.length()==0) {
			HelperClazz.hideProgressDialog();
			Toast.makeText(c.getApplicationContext(), 
					HelperClazz.getMainActivity().getResources().getString(R.string.error_1),
					Toast.LENGTH_LONG).show();
			return;
		}
		JSONReporter parser = new JSONReporter(result);
		
		JSONObject obj = null;
		JSONObject obj2 = null;
		//parses JSON object
		try {
			obj = parser.getJsonObj("currently");
			obj2 = parser.getJsonObj("hourly");
		} catch (Exception e1) {
			HelperClazz.hideProgressDialog();
			Toast.makeText(c.getApplicationContext(), 
					HelperClazz.getMainActivity().getResources().getString(R.string.error_2),
					Toast.LENGTH_LONG).show();
			return;
		}
		try {
			currSummary = obj.getString("summary").toString();
		} catch (JSONException e) {
			
			currSummary = "N/A";
		}
		try {
			currTemp = obj.getString("temperature").toString();
		} catch(JSONException e2) {
			currTemp = "N/A";
		}
		try {
			currPrecipProb = getPrecipProb(obj.getString("precipProbability"))+"% "+obj.getString("precipType");
		} catch(Exception e3) {
			currPrecipProb = "0%";
		}
		try {
			currClouds = obj.getString("cloudCover").toString();
		} catch (JSONException e4){
			currClouds = "N/A";
		}
		try {
			icon = obj.getString("icon").toString();
		} catch (JSONException e4){
			icon = "";
		}
		try {
			hourSum = obj2.getString("summary").toString();
		} catch(JSONException e5) {
			currSummary = "N/A";
		}
		HelperClazz.hideProgressDialog();
    	showMessage(parser);

    }

	
	private void showMessage(JSONReporter pars) {

		HashMap<String, String> currentForecast = new HashMap<String, String>();
		currentForecast.put("description", currSummary);
		currentForecast.put("temperature", currTemp+" °" +unit);
		currentForecast.put("precip", currPrecipProb);
		currentForecast.put("clouds", currClouds);
		currentForecast.put("whole_day", hourSum);
		currentForecast.put("icon", icon);
		currentForecast.put("time", String.valueOf(timeVar));
		TransferClazz.inputMapToTransfer(currentForecast);
		//transfers data to next activity
        Intent in = new Intent(c.getApplicationContext(), ForecastScreen.class);
        in.putExtra(HelperClazz.getMainActivity().getResources().getString(R.string.forecast_title), addressName );
        TransferClazz.inputDataToTransfer(pars.getPeriodicReport("hourly",unit));
        if(!HelperClazz.getIsShifted()) TransferClazz.inputDailyDataToTransfer(pars.getPeriodicReport("daily", unit));
        HelperClazz.getMainActivity().startActivity(in);
	}
	//formats decimal number to two D places
	private String getPrecipProb(String p){
		BigDecimal bd = new BigDecimal(p);
		bd = bd.multiply(new BigDecimal(100));
		bd.setScale(2);
		return bd.toString();
		
	}
}
