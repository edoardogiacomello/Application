package it.unozerouno.givemetime.view.main.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.controller.fetcher.DatabaseManager;
import it.unozerouno.givemetime.controller.service.GiveMeTimeService;
import it.unozerouno.givemetime.utils.GiveMeLogger;
import it.unozerouno.givemetime.view.main.MainActivity;

public class DebugFragment extends Fragment{
	
	private static TextView debugTextView;
	private static Activity activity;
	public static final String ITEM_NAME = "item_name";
	
	public DebugFragment() {
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		MainActivity.toolbar.setTitle("Debug");
		View view = inflater.inflate(R.layout.fragment_debug_layout, container, false);
		debugTextView = (TextView) view.findViewById(R.id.debug_textview);
        
		// get the db and set the categories
		DatabaseManager.getInstance(getActivity());
        DatabaseManager.addDefaultCategories();
		setBtnOnclick(view);
		activity=getActivity();
		return view;
	}
	
	private void setBtnOnclick(View v){
		Button locationBtn = (Button) v.findViewById(R.id.btn_locations);
		Button freetimeBtn = (Button) v.findViewById(R.id.btn_freetime);
		Button editCalendarBtn = (Button) v.findViewById(R.id.btn_calendar_view);
		
		
		freetimeBtn.setText("Service Debug");
		freetimeBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					GiveMeLogger.log("Starting Service");
					Intent serviceIntent = new Intent(getActivity(),GiveMeTimeService.class);
					getActivity().startService(serviceIntent);
		}});
		editCalendarBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//						Fragment calendarFragment = new EventListFragment();
//						openFragment(calendarFragment);
		}});
		
		
	}
	
	public void openFragment(Fragment fragment){
		if (fragment != null){
	        // insert the fragment into the view, replacing the existing one
	        // obtain the fragment manager
	        FragmentManager fragmentManager = getFragmentManager();
	        // start transaction, replace the fragment, commit
	        fragmentManager.beginTransaction()
	                        .replace(R.id.content_frame, fragment)
	                        .commit();
	        
        }
	}
	
	public static void log(final String msg){
		if(activity!=null)
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (debugTextView == null) return;
				debugTextView.append("\n" + msg);
				return;
			}
		});
		
	}
	
	
}
