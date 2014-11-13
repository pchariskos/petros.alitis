package petros.units;

import java.util.ArrayList;

import petros.alitis.R;

import SpinnerNavigation.SpinnerAdapter;
import SpinnerNavigation.SpinnerNavItem;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Is a controller Class that interacts between the model layer (the array list of units) 
 * and the ListView. It accesses the data set of the array list and presents those
 * in a ListFragment. The adapter applied on the ListFragment is depended on the spinner
 * selected item.
 * 
 * @author pchariskos
 *
 */
public class UnitListFragment extends ListFragment implements ActionBar.OnNavigationListener {
	
	// Debugging tag for this fragment
	private static final String LIST_FRAGMENT_TAG = "UnitListFragment";
	
	/**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    
    // Title navigation Spinner data
    private ArrayList<SpinnerNavItem> navSpinner;
     
    // Navigation adapter
    private SpinnerAdapter mSpinnerAdapter;
	
	// The action bar
	private ActionBar mActionBar;
	
    // The preferences name for the spinner navigation selection.
 	public static final String SPINNER_PREFS_NAME = "spinnerState";
 	
 	private static final String SPINNER_SELECTION = "spinnerSelection";
 	
    private SharedPreferences spinnerStatePrefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get the activity's action bar
		mActionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		
		// Hide the action bar title
		mActionBar.setDisplayShowTitleEnabled(false);
 
        // Enabling Spinner dropdown navigation
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
         
        // Spinner's titles navigation data
        addSpinnerItems();
         
        // title drop down adapter
        mSpinnerAdapter = new SpinnerAdapter(getActivity().getApplicationContext(), navSpinner);
 
        // Set the callback for the drop-down list
        mActionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
		
		setHasOptionsMenu(true);
		
		spinnerStatePrefs = getActivity().getSharedPreferences(SPINNER_PREFS_NAME, Context.MODE_PRIVATE);
		
		// Set the item position that the navigation spinner should first invoke
		// Retrieved from sharedpreferences 
		mActionBar.setSelectedNavigationItem(spinnerStatePrefs.getInt(SPINNER_SELECTION, 0));
		
		initAdapter();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_unit_list, parent, false);
		
		return v;
	}
	
	//Reloading the VehicleList after the user navigates back to it 
	@Override
	public void onResume() {
		super.onResume();
		((UnitAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onStop() {
		super.onStop();

		// Save the current selected item of the navigation spinner.
		spinnerStatePrefs = getActivity().getSharedPreferences(SPINNER_PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spinnerStatePrefs.edit();
		editor.putInt(SPINNER_SELECTION, mActionBar.getSelectedNavigationIndex());
		
		editor.commit();
	}
	
	/**
	 * Form the arraylist to show, and set the arraylist to the list adapter.
	 */
	private void initAdapter() {
		
		ArrayList<Unit> listToShow = getUnitListToShow();
	    
	    setListAdapter(new UnitAdapter (listToShow));
	  }
	
	/**
	 * Calculate the arraylist of units to show according to the current spinner selection.
	 * 
	 * @return the arraylist of units
	 */
	private ArrayList<Unit> getUnitListToShow() {

		switch (mActionBar.getSelectedNavigationIndex()) {

		// "All"
		case 0:
			ArrayList<Unit> mAllUnits = UnitLab.get(getActivity()).getUnits();
			Log.d(LIST_FRAGMENT_TAG,"All:" + String.valueOf(mAllUnits));
			return mAllUnits;

		// "I uppdrag"
		case 1:
			ArrayList<Unit> mUnitsLediga = UnitLab.get(getActivity()).getUnits(R.string.unit_ledig);
			Log.d(LIST_FRAGMENT_TAG,"Ledig:" + String.valueOf(mUnitsLediga));
			return mUnitsLediga;

		// "Lediga"
		case 2:
			ArrayList<Unit> mUnitsUppdrag = UnitLab.get(getActivity()).getUnits(R.string.unit_i_uppdrag);
			Log.d(LIST_FRAGMENT_TAG,"I uppdrag:" + String.valueOf(mUnitsUppdrag));
			return mUnitsUppdrag;

		// "Trasiga"
		case 3:
			ArrayList<Unit> mUnitsTrasiga = UnitLab.get(getActivity()).getUnits(R.string.unit_trasig);
			Log.d(LIST_FRAGMENT_TAG,"Trasiga:" + String.valueOf(mUnitsTrasiga));
			return mUnitsTrasiga;

		default:
			return getUnitListToShow();
		}
	}
	
	/*
	 * Fills the spinner navigation with data.
	 */
	private void addSpinnerItems() {
		navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem(R.string.s_unit_all));
        navSpinner.add(new SpinnerNavItem(R.string.s_unit_i_uppdrag));
        navSpinner.add(new SpinnerNavItem(R.string.s_unit_lediga));
        navSpinner.add(new SpinnerNavItem(R.string.s_unit_trasiga));
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		//inflater.inflate(R.menu.fragment_unit_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.action_search:
			Log.d(LIST_FRAGMENT_TAG, "[onOptionsItemSelected]:  Search Pressed");
			return true; 
		case R.id.action_refresh:
			Log.d(LIST_FRAGMENT_TAG, "[onOptionsItemSelected]:  Refresh Pressed");
			return true;
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	// The spinner navigation item selection.
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		
		switch (itemPosition) {
		
		// "All"
		case 0:
			
			initAdapter();
			return true; 
		
		// "I uppdrag"
		case 1:
			
			initAdapter();
			return true; 
			
		// "Lediga"
		case 2:
			
			initAdapter();
			return true;
			
		// "Trasiga"
		case 3:
			
			initAdapter();
			return true;

		default:
            return onNavigationItemSelected(itemPosition, itemId);
		}
	}
	
	@Override
	public void onListItemClick (ListView l, View v, int position, long id) {
		
		Unit unit = ( (UnitAdapter) getListAdapter()).getItem(position);
		
		// Start VehicleActivity
		Intent intent = new Intent(getActivity(), UnitActivity.class);
		
		// Add an Intent extra to tell the VehicleFragment which vehicle to display
		intent.putExtra(UnitFragment.EXTRA_UNIT_ID, unit.getID());
		startActivity(intent);
	}

	/**
     * Returns a new instance of this fragment for the given section
     * number.
     * 
     * @param sectionNumber
	 * @return
     */
    public static UnitListFragment newInstance(int sectionNumber) {
    	UnitListFragment fragment = new UnitListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
	
	
	/**
	 * This is an adapter class that knows how to work with Vehicle
	 * Objects.
	 * 
	 * @author pchariskos
	 *
	 */
	private class UnitAdapter extends ArrayAdapter<Unit> {
		
		public UnitAdapter(ArrayList<Unit> unit) {
			super (getActivity(), 0, unit);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//If we weren't given a view, inflate one
			if (convertView == null ) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_list_item_unit_row,  null);
			}
			
			/*
			 * Configure the view for this vehicle
			 */
			Unit unit = getItem(position);
			ImageButton imageOverflow = (ImageButton) convertView.findViewById(R.id.imageButton_OVerflow);
			imageOverflow.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Toast.makeText(getActivity(), "ImageButton Clicked", Toast.LENGTH_SHORT).show();
				}
				});
			
			ImageButton imageLocate = (ImageButton) convertView.findViewById(R.id.imageButton_findLocation);
			imageLocate.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Toast.makeText(getActivity(), "Need implementation.", Toast.LENGTH_SHORT).show();
				}
				});
			
			TextView statusTextView = (TextView) convertView.findViewById(R.id.vehicle_list_item_statusTextView);
			statusTextView.setText(unit.getStatus());
			
			TextView titleTextView = (TextView) convertView.findViewById(R.id.vehicle_list_item_titlteTextView);
			titleTextView.setText(unit.getTitle());
			
			//Set the image resource for the status icon
			ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView_statusIndicator);
			if (unit.getStatus() == R.string.unit_i_uppdrag) {
				imageView.setImageResource(R.drawable.ic_status_uppdrag);
			} else if (unit.getStatus() == R.string.unit_ledig) {
				imageView.setImageResource(R.drawable.ic_status_ledig);
			} else if (unit.getStatus() == R.string.unit_trasig) {
				imageView.setImageResource(R.drawable.ic_status_trasig);
			} else {
				Toast.makeText(getActivity(), "Cannot set up image resource for the status icon", Toast.LENGTH_SHORT).show();
			}
			
			
			return convertView;
		}
	}

}