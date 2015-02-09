package it.unozerouno.givemetime.view.intro.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.UserKeyRing;
import it.unozerouno.givemetime.view.intro.HomeLocationDialogActivity;
import it.unozerouno.givemetime.view.intro.HomeSleepDialogActivity;
import it.unozerouno.givemetime.view.main.MainActivity;

public class LastTutorialPage extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.last_tutorial_page, container, false);
        
        Button continueButton = (Button) rootView.findViewById(R.id.continueButton);
        Button btnHomeLocation = (Button) rootView.findViewById(R.id.btnHomeLocation);
        Button btnSleep = (Button) rootView.findViewById(R.id.btnSleepTime);
        
        btnHomeLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), HomeLocationDialogActivity.class);
				startActivity(intent);
				
			}
		});
        
        btnSleep.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), HomeSleepDialogActivity.class);
				startActivity(intent);
			}
		});
        
        
        
        continueButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UserKeyRing.setFirstLogin(getActivity(), false);
				Intent i = new Intent(getActivity(), MainActivity.class);
				startActivity(i);
				// close the calling activity in order to avoid back button to work
				getActivity().finish();
			}
		});

        //((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Tells us something about your habits!");
        
        return rootView;
    }
}
