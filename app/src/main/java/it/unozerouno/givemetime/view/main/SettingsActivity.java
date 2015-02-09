package it.unozerouno.givemetime.view.main;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import it.unozerouno.givemetime.view.main.fragments.SettingsFragment;

/**
 * Activity showing the settings menu
 */
public class SettingsActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display the Settings Fragment as the main content
        getFragmentManager().beginTransaction()
                // preference fragment doesn't have it's own context
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


}
