package it.unozerouno.givemetime.view.utilities;

import android.content.Context;
import android.preference.Preference;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unozerouno.givemetime.R;

public class ToolbarPreference extends Preference{

	public ToolbarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected View onCreateView(ViewGroup parent) {
		
		parent.setPadding(0, 0, 0, 0);
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.toolbar, parent, false);
		
		Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar_settings);
		toolbar.setTitle("Settings");
		
		return layout;
	}

}
