package hr.hrvoje.weather;

import android.os.Bundle;
import android.preference.PreferenceActivity;
 
public class UserSettings extends PreferenceActivity {
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.settings);
 
    }
}