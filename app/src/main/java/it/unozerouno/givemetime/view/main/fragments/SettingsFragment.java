package it.unozerouno.givemetime.view.main.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.UserKeyRing;

/**
 * Fragment handling the menu settings
 */
public class SettingsFragment extends PreferenceFragment{
	
	//List of preferences to manipulate
	
	
	//debug category
	Preference debugWipeSettings;
	Preference debugUserEmail;
	Preference debugUserName;
	Preference debugUserSurname;
	//Preference debugToken;
	//SwitchPreference debugFirstTime;
	Preference debugCalendarId;
	Preference debugCalendarName;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        System.out.println("Settings opened");
        
        //Getting Debug Preferences
      debugUserEmail = (Preference) findPreference("debug_user_email");
      debugUserName = (Preference) findPreference("debug_user_name");
      debugUserSurname = (Preference) findPreference("debug_user_surname");
      debugWipeSettings = (Preference) findPreference("debug_wipe");
      //debugToken = (Preference) findPreference("debug_user_token"); 
      //debugFirstTime = (SwitchPreference) findPreference("debug_first_time");
      debugCalendarId = (Preference) findPreference("debug_user_selected_cal_id");  
      debugCalendarName = (Preference) findPreference("debug_user_selected_cal_name"); 
       
       //Setting values to show
       debugUserEmail.setSummary(UserKeyRing.getUserEmail(this.getActivity()));
       debugUserName.setSummary(UserKeyRing.getUserName(getActivity()));
       debugUserSurname.setSummary(UserKeyRing.getUserSurname(getActivity()));
       //debugToken.setSummary(UserKeyRing.getToken(this.getActivity()));
       //debugFirstTime.setChecked(UserKeyRing.isFirstTimeLogin(getActivity()));
       debugCalendarId.setSummary(UserKeyRing.getCalendarId(getActivity()));
       debugCalendarName.setSummary(UserKeyRing.getCalendarName(getActivity()));
       
       
       
       //Setting action for wipe debug preference
       debugWipeSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference arg0) {
				UserKeyRing.resetSharedPreferences(getActivity());
				Toast toast = Toast.makeText(getActivity(), "Settings Wiped", Toast.LENGTH_LONG);
				toast.show();
				return false;
			}
		});
       
       //Setting Action for DebugFirstTime
       /*debugFirstTime.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			boolean switched = ((SwitchPreference) preference).isChecked();
			UserKeyRing.setFirstLogin(getActivity(), !switched);
			Toast.makeText(getActivity(), "Setting Saved", Toast.LENGTH_SHORT).show();
			return true;
		}
	});*/
       
    }
    
}
