package hr.hrvoje.weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
/**
 * class for displaying forecast info and handling screen actions
 * @author hrvoje
 *
 */
public class ForecastScreen extends Activity{
	
	//private String forecastText;
	private String forecastTitle;
	private ArrayList<HashMap> forecastArray, forecastDailyArray;
	private HashMap<String, String> currForecast;
	private TextView text;
	private EditText forecastHourly;
	private TableLayout mLayout, mDailyLayout;
	private RelativeLayout relLayout;
	private Button hourly, daily;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forecast);
		//gets address name
		forecastTitle= getIntent().getExtras().get(getResources().getString(R.string.forecast_title)).toString();
		relLayout = (RelativeLayout) findViewById(R.id.forecast_act);
		//gets forecast data from another activity
		try {
			forecastArray = TransferClazz.outputDataToTransfer();
		} catch (Exception e2) {
			forecastArray = null;
		}
		try {
			forecastDailyArray = TransferClazz.outputDailyDataToTransfer();
		} catch (Exception e1) {
			forecastDailyArray=null;
		}
		try {
			currForecast = TransferClazz.outputMapToTransfer();
		} catch (Exception e) {
			currForecast=null;
		}
		hourly = (Button) findViewById(R.id.btnhourly);
		daily = (Button) findViewById(R.id.btndaily);
		setTitle("Forecast");
		
		text = (TextView) findViewById(R.id.forecast_text);
		//Typeface font = Typeface.createFromAsset(getAssets(), "corbert_regular.otf");  
		text.setTypeface(null, Typeface.BOLD);
		//text.setTypeface(font);  
		//text.setText(forecastText);
		
		forecastHourly = (EditText) findViewById(R.id.forecastHourly);
		forecastHourly.setVisibility(View.GONE);
		mLayout = (TableLayout) findViewById(R.id.tableLayout);
		mDailyLayout = (TableLayout) findViewById(R.id.tableDailyLayout);
		mLayout.setVisibility(View.GONE);
		mDailyLayout.setVisibility(View.GONE);
		
		//event listeners
		hourly.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				 if(mLayout.getVisibility()==View.GONE){
					 mLayout.setVisibility(View.VISIBLE);
					 hourly.setText(R.string.hideHourly);
				 } else {
					 mLayout.setVisibility(View.GONE);
					 hourly.setText(R.string.showHourly);
				 }
				  
			  }
		});
		//there is no daily report for time shifted forecast
		if(HelperClazz.getIsShifted()) daily.setVisibility(View.GONE);
		daily.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  if(mDailyLayout.getVisibility()==View.GONE){
						 mDailyLayout.setVisibility(View.VISIBLE);
						 daily.setText(R.string.hideDaily);
					 } else {
						 mDailyLayout.setVisibility(View.GONE);
						 daily.setText(R.string.showDaily);
					 } 
			  }
		});
		
		populateCurrentForecast();
		populateHourlyForecast(forecastArray);
		if(!HelperClazz.getIsShifted()) populateDailyForecast(forecastDailyArray);
	}
	/**
	 * displays text for current forecast
	 */
	private void populateCurrentForecast() {
		//in case of error shows nothing
		if(currForecast == null) return;
		String desc =  currForecast.get("description");
		String temp = currForecast.get("temperature");
		String precip = currForecast.get("precip");
		String clouds = currForecast.get("clouds");
		String day = currForecast.get("whole_day");
		String icon = currForecast.get("icon");
		String time = currForecast.get("time");
		
		String forecastText="";
		if(forecastTitle!=null && forecastTitle.length()>0){
			forecastText += "Forecast for " + forecastTitle+ "\n";
		}
		if(time !=null){
			forecastText += String.valueOf(HelperClazz.unix2Date(Long.parseLong(time)))+"\n";
		}
		if(desc != null) {
			forecastText += desc+"\n";
		}
		if(temp != null) {
			forecastText += temp+"\n";
		}
		if(precip!=null) {
			forecastText += "Precipitation: "+precip+"\n";
		}
		if(clouds != null) {
			forecastText += "Clouds [0-1]: "+clouds+"\n\n";
		}
		if( day != null) {
			forecastText += getResources().getString(R.string.whole_day)+"\n"+day+"\n\n";
		}
		forecastText += "Powered by Forecast.io";
	//	Log.d("Icon", icon);
		text.setText(forecastText);
		text.setBackgroundResource((R.drawable.rect_glass));
		int imgRes = getImageRes(icon);
		if(imgRes == -1) return;
		relLayout.setBackgroundResource(getImageRes(icon));
	}

	private void populateDailyForecast(ArrayList<HashMap> map){
		if (map == null) return;
		Iterator<HashMap> it = map.iterator();
	//	setContentView(mLayout);
		TableRow header = new TableRow(this);
		//header.setBackgroundResource((R.drawable.table_row_bcg));
		header.addView(createNewTextView(getResources().getString(R.string.time_value)));
		header.addView(createNewTextView(getResources().getString(R.string.temp_min_value)));
		header.addView(createNewTextView(getResources().getString(R.string.temp_max_value)));
		header.addView(createNewTextView(" "));
		mDailyLayout.addView(header); //, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT

		while(it.hasNext()){
			HashMap<String, String> tempMap = new HashMap<String, String>();
			tempMap = it.next();
			String timeVal = tempMap.get("time");
			String tempMinVal = tempMap.get("tempMin");
			String tempMaxVal = tempMap.get("tempMax");
			String iconVal = tempMap.get("icon");
			
			
			TableRow tr = new TableRow(this);
			tr.setGravity(Gravity.CENTER_VERTICAL);
			tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
		            TableRow.LayoutParams.WRAP_CONTENT));
			tr.setPadding(0, 15, 0, 15);
			tr.setBackgroundResource((R.drawable.table_row_bcg));
			tr.addView(createNewTextView(timeVal));
			tr.addView(createNewTextView(tempMinVal));
			tr.addView(createNewTextView(tempMaxVal));
			
			
			try {
				ImageView im = getWeatherImage(iconVal);
				if(im!=null) tr.addView(im);			
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		
			mDailyLayout.addView(tr);
		}
	}
	private void populateHourlyForecast(ArrayList<HashMap> map){
		if(map == null) return;
		Iterator<HashMap> it = map.iterator();
	//	setContentView(mLayout);
		TableRow header = new TableRow(this);
		//header.setBackgroundResource((R.drawable.table_row_bcg));
		header.addView(createNewTextView(getResources().getString(R.string.time_value)));
		header.addView(createNewTextView(getResources().getString(R.string.temp_value)));
		header.addView(createNewTextView(getResources().getString(R.string.precipitation_value)));
		header.addView(createNewTextView(" "));
		mLayout.addView(header); //, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT

		while(it.hasNext()){
			HashMap<String, String> tempMap = new HashMap<String, String>();
			tempMap = it.next();
			String timeVal = tempMap.get("time");
			String tempVal = tempMap.get("temp");
			String precipVal = tempMap.get("precip");
			String iconVal = tempMap.get("icon");
			
			
			TableRow tr = new TableRow(this);
			tr.setGravity(Gravity.CENTER_VERTICAL);
			tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
		            TableRow.LayoutParams.WRAP_CONTENT));
			tr.setPadding(0, 15, 0, 15);
			tr.setBackgroundResource((R.drawable.table_row_bcg));
			tr.addView(createNewTextView(timeVal));
			tr.addView(createNewTextView(tempVal));
			tr.addView(createNewTextView(precipVal));
			
			
			try {
				ImageView im = getWeatherImage(iconVal);
				if(im!=null) tr.addView(im);			
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			mLayout.addView(tr);
		}
	}
	
	
	
	private TextView createNewTextView(String text) {
	    //final LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    final TextView textView = new TextView(this);
	    int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 1, getResources().getDisplayMetrics());
	   // textView.setLayoutParams(lparams);
	    textView.setPadding(0, 0, 20*dip, 30*dip);
	 //   Typeface font = Typeface.createFromAsset(getAssets(), "corbert_regular.otf");  
	//	textView.setTypeface(font); 
	    if(text==null) text = "";
	    textView.setText(text);
	    return textView;
	}
	private ImageView getWeatherImage(String text){
		ImageView iv = null;
	//	iv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		if(text.equals(getResources().getString(R.string.clear_day))){
			iv = new ImageView(this);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.clearday));
			return iv;
		} else if(text.equals(getResources().getString(R.string.clear_night))){
			iv = new ImageView(this);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.clearnight));
			return iv;
		} else if(text.equals(getResources().getString(R.string.cloudy))){
			iv = new ImageView(this);			
			iv.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
			return iv;
		} else if(text.equals(getResources().getString(R.string.fog))){
			iv = new ImageView(this);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.fog));
			return iv;
		} else if(text.equals(getResources().getString(R.string.partly_cloudy_day))){
			iv = new ImageView(this);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.partcloudly));
			return iv;
		} else if(text.equals(getResources().getString(R.string.partly_cloudy_night))){
			iv = new ImageView(this);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.partcloudlynight));
			return iv;
		} else if(text.equals(getResources().getString(R.string.rain))){
			iv = new ImageView(this);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.rain));
			return iv;
		} else if(text.equals(getResources().getString(R.string.sleet))){
			iv = new ImageView(this);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.sleet));
			return iv;
		} else if(text.equals(getResources().getString(R.string.snow))){
			iv = new ImageView(this);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.snow));
			return iv;
		} else if(text.equals(getResources().getString(R.string.wind))){
			iv = new ImageView(this);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.wind));
			return iv;
		} else {
			return null;
		}
	}
	
	private int getImageRes(String textIc){
			int res;
			
		//	iv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			if(textIc.equals(getResources().getString(R.string.clear_day))){
				res = R.drawable.sunny_bcg;
				return res;
			} else if(textIc.equals(getResources().getString(R.string.clear_night))){
				res = R.drawable.clear_night_bcg;
				return res;
			} else if(textIc.equals(getResources().getString(R.string.cloudy))){
				res = R.drawable.cloudy_bcg;
				return res;
			} else if(textIc.equals(getResources().getString(R.string.fog))){
				res = R.drawable.fog_bcg;
				return res;
			} else if(textIc.equals(getResources().getString(R.string.partly_cloudy_day))){
				res = (R.drawable.cloudy_bcg);
				return res;
			} else if(textIc.equals(getResources().getString(R.string.partly_cloudy_night))){
				res = R.drawable.cloudy_bcg;
				return res;
			} else if(textIc.equals(getResources().getString(R.string.rain))){
				res = (R.drawable.rainy_day_background_page);
				return res;
			} else if(textIc.equals(getResources().getString(R.string.sleet))){
				res = R.drawable.snow_bcg;
				return res;
			} else if(textIc.equals(getResources().getString(R.string.snow))){
				res = R.drawable.snow_bcg;
				return res;
			} else if(textIc.equals(getResources().getString(R.string.wind))){
				res = R.drawable.cloudy_bcg;
				return res;
			} else {
				res = R.drawable.background_main;
				return res;
			}
		}
	
	
	
}
