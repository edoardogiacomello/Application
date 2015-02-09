package it.unozerouno.givemetime.view.editor.constraints;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import it.unozerouno.givemetime.R;

public class DoubleTimePickerDialog extends DialogFragment{
	TimePicker startPicker;
	TimePicker endPicker;
	Button okBtn;
	Button cancelBtn;
	OnConstraintSelectedListener listener;
	private Time defaultStart;
	private Time defaultEnd;
	
	public DoubleTimePickerDialog(OnConstraintSelectedListener callBack, Time defaultStart, Time defaultEnd) {
	super();
	listener=callBack;
	this.defaultStart = defaultStart;
	this.defaultEnd = defaultEnd;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_constraint_timepicker_double, container, false);
		startPicker = (TimePicker) view.findViewById(R.id.editor_constraint_timepicker_start);
		startPicker.setIs24HourView(true);
		endPicker = (TimePicker) view.findViewById(R.id.editor_constraint_timepicker_end);
		endPicker.setIs24HourView(true);
		okBtn = (Button) view.findViewById(R.id.editor_constraint_timepicker_btn_save);
		cancelBtn = (Button) view.findViewById(R.id.editor_constraint_timepicker_btn_cancel);
		setButtonListener();
		getDialog().setTitle(R.string.editor_constraints_timepicker_title);
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		//If default values are specified, then load them
		if (defaultStart != null){
			startPicker.setCurrentHour(defaultStart.hour);
			startPicker.setCurrentMinute(defaultStart.minute);
		}
		if(defaultEnd != null){
			endPicker.setCurrentHour(defaultEnd.hour);
			endPicker.setCurrentMinute(defaultEnd.minute);
		}
	}
	
	 private void setButtonListener() {
		 
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Getting start time
				int startHour = startPicker.getCurrentHour();
				int startMinute = startPicker.getCurrentMinute();
				//Getting ending time
				int endHour = endPicker.getCurrentHour();
				int endMinute = endPicker.getCurrentMinute();
				Time startTime = new Time();
				Time endTime = new Time();
				startTime.setJulianDay(Time.EPOCH_JULIAN_DAY);
				endTime.setJulianDay(Time.EPOCH_JULIAN_DAY);
				startTime.hour = startHour;
				startTime.minute = startMinute;
				endTime.hour = endHour;
				endTime.minute = endMinute;
				
				if(!startTime.after(endTime)){
					listener.onTimeSelected(startTime, endTime);
					DoubleTimePickerDialog.this.dismiss();
				} else {
					Toast.makeText(getActivity(), "Cannot select a negative interval", Toast.LENGTH_SHORT).show();
				}
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.timeNotSelected();
				DoubleTimePickerDialog.this.dismiss();
			}
		});
	}


	abstract static class OnConstraintSelectedListener{
		abstract void onTimeSelected(Time startTime, Time endTime);
		abstract void timeNotSelected();
	}
}
