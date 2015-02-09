package it.unozerouno.givemetime.view.intro;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.model.places.PlaceModel;
import it.unozerouno.givemetime.view.editor.LocationEditorFragment;
import it.unozerouno.givemetime.view.editor.LocationEditorFragment.OnSelectedPlaceModelListener;

public class HomeLocationDialogActivity extends FragmentActivity implements OnSelectedPlaceModelListener{
	
	LocationEditorFragment lef;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_home_location);
		
		lef = (LocationEditorFragment) getSupportFragmentManager().findFragmentById(R.id.home_location_fragment_locations_container);
		getSupportFragmentManager().beginTransaction().show(lef).commit();
		
	}

	@Override
	public void onSelectedPlaceModel(PlaceModel place) {
		// TODO needs sleepTime set, precedence problem
		//DatabaseManager.addUserHomePlace(UserKeyRing.getUserEmail(this), place);
		finish();
	}

}
