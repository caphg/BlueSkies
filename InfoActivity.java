package hr.hrvoje.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * Contains information about this app
 * @author hrvoje
 *
 */
public class InfoActivity extends Activity{
	private TextView text, about;
	private Button web;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_act);
		//forecastText= getIntent().getExtras().get(getResources().getString(R.string.forecast_data)).toString();
		about = (TextView) findViewById(R.id.aboutText);
		about.setText(getResources().getString(R.string.about_infos)+"\nVersion: "+getResources().getString(R.string.version));
		text = (TextView) findViewById(R.id.infoText);
		text.setText(getResources().getString(R.string.info_api)+"\n"+getResources().getString(R.string.info_text));
		web = (Button) findViewById(R.id.btnSubmitWeb);
		
		web.setOnClickListener(new OnClickListener() {
			 
			  @Override
			  public void onClick(View v) {
				  Intent in = new Intent(getApplicationContext(), WebPageRegisterActivity.class);
			        startActivity(in);

			  }
		});
	}

}
