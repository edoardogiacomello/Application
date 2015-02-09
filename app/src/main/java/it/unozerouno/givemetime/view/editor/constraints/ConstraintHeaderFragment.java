package it.unozerouno.givemetime.view.editor.constraints;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import it.unozerouno.givemetime.R;

public class ConstraintHeaderFragment extends Fragment{
	Button addBtn;
	OnClickListener addListener;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editor_constraint_header_fragment, container, false);
		addBtn = (Button) view.findViewById(R.id.editor_constraint_header_btn_add);
		addBtn.setOnClickListener(addListener);
		return view;
	}
	
	public void setOnAddButtonOnClick(OnClickListener listener){
		addListener = listener;
	}
}
