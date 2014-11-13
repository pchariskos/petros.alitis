package petros.units;

import java.util.UUID;

import petros.alitis.R;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UnitFragment extends Fragment {
	public static final String EXTRA_UNIT_ID = "petros.units.vehicle_id";
	private Unit mUnit;
	private TextView mTextView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//UnitFragment access its arguments
		UUID vehicleId = (UUID) getArguments().getSerializable(EXTRA_UNIT_ID);
		
		//Use the retrieved unitId to fetch the Unit
		mUnit = UnitLab.get(getActivity()).getUnit(vehicleId);
		
		/*
		 * As part of the respond to the enabled app icon as an Up button, we
		 * tell the fragment to handle options menu callbacks on behalf of the activity.
		 */
		setHasOptionsMenu(true);	
	}
	
	//Respond to the enabled app icon as an Up button.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			
			// First check to see if there is a parent activity named in the meta-data
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				//Navigate to the parent activity
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_unit, parent, false);
		
		//Enable ancestral navigation
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//No parent activity, no caret shown
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		
		mTextView = (TextView) view.findViewById(R.id.testTitle);
		mTextView.setText(mUnit.getTitle());
		
		return view;
	}
	
	/**
	 * Attach the arguments bundle to a fragment (after fragment creation and before it is added to the activity).
	 * Adding a static method named newInstance() to the Fragment Class in order to create the fragment instance,
	 * and attach the arguments to the fragment.
	 * 
	 * @param vehicleId
	 * @return
	 */
	public static UnitFragment newInstance(UUID vehicleId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_UNIT_ID, vehicleId);
		
		UnitFragment fragment = new UnitFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
}