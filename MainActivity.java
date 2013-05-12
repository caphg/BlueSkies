package hr.hrvoje.weather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * Main menu activity
 * @author hrvoje
 *
 */
public class MainActivity extends Activity{
	
	private Button current, shifted, settings, info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_act);
		HelperClazz.setMainActivity(this);
		HelperClazz.setMainContext(getApplicationContext());
		this.setTitle("Welcome");
		current = (Button) findViewById(R.id.btnSubmitCurrent);
		shifted = (Button) findViewById(R.id.btnSubmitShifted);
		settings = (Button) findViewById(R.id.btnSubmitSettings);
		info = (Button) findViewById(R.id.btnSubmitInfo);
		
		
		current.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  HelperClazz.setIsShifted(false);
				  Intent in = new Intent(getApplicationContext(), CurrentForecast.class);

			        startActivity(in);
			  }
		});
		
		shifted.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  HelperClazz.setIsShifted(true);
				  Intent in = new Intent(getApplicationContext(), ShiftedForecast.class);

			        startActivity(in);
			  }
		});
		settings.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  doSettings();

			  }		  
		});
		
		
		
		info.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  Intent in = new Intent(getApplicationContext(), InfoActivity.class);

			        startActivity(in);
			  }
		});
		
		//if api is not defined show dialog
		if(!HelperClazz.isApiDefined()){
			if(!HelperClazz.loadApi(this)){
				showintroMessage();
			}
		}
		
	}
	private void doSettings(){
		Intent in = new Intent(getApplicationContext(), UserSettings.class);

		  startActivityForResult(in, 1);
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case 1:
            saveSettings();
            break;
 
        }
 
    }
	//this is shown when no API is defined
	private void showintroMessage(){
		new AlertDialog.Builder(this)
	    .setTitle("Register your account")
	    .setMessage(getResources().getString(R.string.intro_mess))
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            dialog.dismiss();
	        }
	     })
	     .setNeutralButton("More Info", new DialogInterface.OnClickListener() {
	    	 public void onClick(DialogInterface dialog, int which) { 
	    		 	Intent in = new Intent(getApplicationContext(), InfoActivity.class);
		        	startActivity(in);
		        }
			
		})
	    .setNegativeButton("Register", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	Intent in = new Intent(getApplicationContext(), WebPageRegisterActivity.class);
	        	startActivity(in);
	        }
	     })
	     .show();
	}
	/**
	 * when done editing options save them
	 */
	private void saveSettings(){
		  SharedPreferences sharedPrefs = PreferenceManager
        .getDefaultSharedPreferences(this);
		  
		  SharedPreferences sharedPreferences = HelperClazz.getMainActivity().getPreferences(Context.MODE_PRIVATE);
		String unit = sharedPrefs.getString("temp_setting", "C");
		//Log.d("C", unit);
        if(!unit.equals(TransferClazz.getTempUnit(HelperClazz.getMainActivity()))){
        	TransferClazz.saveTempUnit(HelperClazz.getMainActivity(), unit);
        }
        String newApi = String.valueOf(sharedPrefs.getString("api_setting", null));
       // Log.d("new api", newApi);
        if(!newApi.equals(HelperClazz.getApi()) && !newApi.equals("") && newApi != null){
        	//checks for correct input & saves
        	CheckWeatherService checkInput = new CheckWeatherService(this, newApi, true);
        	checkInput.execute(newApi);
        }
	}
}
	