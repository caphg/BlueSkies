package hr.hrvoje.weather;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdView;
/**
 * selection screen for current forecast
 * @author hrvoje
 *
 */
public class CurrentForecast extends Activity {

	private AutoCompleteTextView autocmpl;
	private Button btnSubmit, btnSubmit2, btnSubmit3;
	private long time;
	private TextView lat,longit;
	private Activity act;
	private String unit="C";
	private AdView ad;
	private LinearLayout dummyLayout;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.curr_main);
		//init
		this.act = this;
		autocmpl = (AutoCompleteTextView) findViewById(R.id.autocmpl);
		autocmpl.setHint("Enter location here...");
		dummyLayout = (LinearLayout) findViewById(R.id.dummyLayout);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		lat = (TextView) findViewById(R.id.editText);
		lat.setHint("Latitude...");
		longit = (TextView) findViewById(R.id.editText2);
		longit.setHint("Longitude...");
		btnSubmit2 = (Button) findViewById(R.id.btnSubmit2);
		btnSubmit3 = (Button) findViewById(R.id.btnSubmit3);
		ad = (AdView) findViewById(R.id.adView);

		//events
		//hide show forecast button when entering information
		autocmpl.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(hasFocus){
			    	btnSubmit.setVisibility(View.GONE);
			    	ad.setVisibility(View.GONE);
			    }else {
			    	btnSubmit.setVisibility(View.VISIBLE);
			    	ad.setVisibility(View.VISIBLE);
			    }
			   }
			});
		//gets current forecast
		btnSubmit.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  String addressName;
				//  setCoord(String.valueOf(autocmpl.getText()));
				 // setVrijeme();
				  //checks for user's own API and controls the API usage
				  if(!HelperClazz.isApiDefined()){
					  Toast.makeText(getApplicationContext(), getResources().getString(R.string.api_err_miss),
							  Toast.LENGTH_LONG).show();
					  return;
					  
				  }
				  //check for correct coordinates input
				  if(lat.getText().toString().length()<=0 || longit.getText().toString().length()<=0){
					  Toast.makeText(getApplicationContext(), 
			                    "Search for location or use Find me option.", 
			                    Toast.LENGTH_LONG).show();
					  return;
				  }
				  if(!HelperClazz.checkCorrdinatesInput(lat.getText().toString())
						  || !HelperClazz.checkCorrdinatesInput(longit.getText().toString())){
					  Toast.makeText(getApplicationContext(), 
			                    "Enter location correctly!", 
			                    Toast.LENGTH_LONG).show();
					  return;
				  }
				  //converts time to ms
				  time = System.currentTimeMillis() / 1000L;
				  addressName = HelperClazz.getAddress(getApplicationContext(), 
						  Double.parseDouble(lat.getText().toString()),
						  Double.parseDouble(longit.getText().toString())
				  );
				  //addressName=getAddress(Double.parseDouble(lat.getText().toString()), Double.parseDouble(longit.getText().toString()));
				  //checks for network availability
				  if(!HelperClazz.isNetworkAvailable(act)){
					  Toast.makeText(getApplicationContext(), 
			                    "No networks available!", 
			                    Toast.LENGTH_LONG).show();
					  return;
				  }
				  //shows progress
				  HelperClazz.showProgressDialog(v.getContext(), getResources().getString(R.string.progress_dialog));
				  //class that fetches data, runs in background as a separate process
				  DataGrabber grabber = new DataGrabber(getApplicationContext(), 
						  longit.getText().toString(), 
						  lat.getText().toString(),
						  time,
						  unit,
						  addressName
				  );
				  grabber.execute();  
			  }	 
			});
		//gets users current location
		btnSubmit2.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  dummyLayout.requestFocus();
				  Locator locator = new Locator(getBaseContext(),lat,longit); 
				  locator.getLocation();
			  }
		 
		});		
		//gets coordinates from entered adress
		//if coordinates are not available from android API 
		//google maps API will be used instead
		btnSubmit3.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  dummyLayout.requestFocus();
				  //checks for empty string
				  if(autocmpl.getText().toString().length()<=0){
					  Toast.makeText(getApplicationContext(), 
			                    "Enter location!", 
			                    Toast.LENGTH_LONG).show();
					  return;
				  }
				  //saves inout for later use
				  TransferClazz.saveLastInput(autocmpl.getText().toString(), Mode.CURRENT);
				  HelperClazz.showProgressDialog(v.getContext(), getResources().getString(R.string.progress_location));
				  HelperClazz.getLocationCoordinates(getApplicationContext(),
						  String.valueOf(autocmpl.getText()),
						  lat, 
						  longit,
						  autocmpl
			  );
				  //saves the name of a location in an autocomplete form
				  TransferClazz.saveLocation(HelperClazz.getMainActivity(), autocmpl, String.valueOf(autocmpl.getText()));
				//  setCoord(String.valueOf(autocmpl.getText()));
  
			  }
		 	
		});
		
		//startup initialization
		//loads saved autocomplete entries
		TransferClazz.loadSavedLocations(HelperClazz.getMainActivity(), autocmpl);
		//loads saved api if not already loaded
		if(!HelperClazz.isApiDefined()){
			HelperClazz.loadApi(this);
		}
		//loads saved unit preference
		unit = TransferClazz.getTempUnit(HelperClazz.getMainActivity());
		//sets last entered address in a field
		autocmpl.setText(TransferClazz.loadLastInput(Mode.CURRENT));
	}
		

	//options menu
//	@Override
/*	public boolean onCreateOptionsMenu(Menu menu) {
		//loads activity
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		return true;
	}*/
	//action on selected item
/*	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        //enter settings menu
	        case R.id.menu_settings:
	            Intent i = new Intent(this, UserSettings.class);
	            startActivityForResult(i, RESULT_SETTINGS);
	            break;
	 
	        }
	 
	        return true;
	   }*/
	 //show settigns menu
	/* @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	 
	        switch (requestCode) {
	        case RESULT_SETTINGS:
	            applySettings();
	            break;
	 
	        }
	 
	    }*/
	 
	/* private void applySettings() {
		 //saves entered values, api and unit
	        SharedPreferences sharedPrefs = PreferenceManager
	                .getDefaultSharedPreferences(this);
	        SharedPreferences sharedPreferences = HelperClazz.getMainActivity().getPreferences(Context.MODE_PRIVATE);
	       
	        this.api = String.valueOf(sharedPrefs.getString("api_setting", null));
	        //if entered API is not the current API, empty string or null
	        if(!api.equals(HelperClazz.getApi()) && !api.equals("") && api != null){
	        	HelperClazz.setApi(api);
	        //	SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
			    SharedPreferences.Editor editor = sharedPreferences.edit();
			    editor.putString("apiPref", api);
			    editor.commit();
	        }
	        this.unit = sharedPrefs.getString("temp_setting", "C");
	        if(!unit.equals(TransferClazz.getTempUnit(HelperClazz.getMainActivity()))){
	        	TransferClazz.saveTempUnit(HelperClazz.getMainActivity(), unit);
	        }
	        
	        
	 }*/
	 
		 
}
	
