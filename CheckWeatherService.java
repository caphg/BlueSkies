package hr.hrvoje.weather;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
/**
 * Checks for corrent API input and saves new API
 * Async and runs in background
 * @author hrvoje
 *
 */
public class CheckWeatherService extends AsyncTask<String, String, Boolean>{
	String newAPI;
	Activity act;
	Boolean silentUpdate, apiExists;
	public CheckWeatherService(Activity act, String newAPI, Boolean silentUpdate) {
		this.newAPI=newAPI;
		this.act=act;
		this.silentUpdate= silentUpdate;
		this.apiExists = false;
	}
	@Override
	protected Boolean doInBackground(String... arg0) {
		//testing location
		String respStr = HelperClazz.getResponseString("https://api.forecast.io/forecast/"+arg0[0]+"/"+"45.50"+","+"15.50");
		//forbidden when no forecast data eexists for that location
			if(respStr != null && !respStr.trim().equals("Forbidden")){
				return true;
			}
			return false;
		
	}
	@Override
    protected void onPostExecute(Boolean isActive) {
		if(isActive){
			//checks whether api already exists
			if(HelperClazz.getApi() != null && HelperClazz.getApi().equals(newAPI)){
				apiExists = true;
			}
			//saves new api
			HelperClazz.setApi(newAPI);
			SharedPreferences sharedPreferences = HelperClazz.getMainActivity().getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
		    editor.putString("apiPref", newAPI);
		    editor.commit();
		    
		    //shows dialog and returns to main menu
		    showDialog(act);
		} else {
			if(!silentUpdate){
				Toast.makeText(HelperClazz.getMainKontext(), 
						"API key you entered appears incorrect.\nNo changes were made.",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public  void showDialog(Activity c){
		String message = "Your new API key has been saved.\nYou now have 1000 forecasts per day. Enjoy!";
		//if api is already saved
		if(apiExists){
			message = "Your API key is already saved.";
		}
		 AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
		 alertDialog.setCancelable(false);
		    // Setting Dialog Title
		    alertDialog.setTitle("Success");

		    // Setting Dialog Message
		    alertDialog
		            .setMessage(message);

		    // On pressing Settings button
		    alertDialog.setPositiveButton("OK",
		            new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                	Intent in = new Intent(HelperClazz.getMainActivity().getApplicationContext(), MainActivity.class);
					        HelperClazz.getMainActivity().startActivity(in);
		                }
		            });


		    // Showing Alert Message
		    alertDialog.show();
	}
}
