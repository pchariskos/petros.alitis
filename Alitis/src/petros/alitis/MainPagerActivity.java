package petros.alitis;


import java.util.Locale;

import petros.googleMaps.GoogleMapsFragment;
import petros.googleMaps.GooglePlayServicesController;
import petros.googleMaps.KartaStateManager;
import petros.units.UnitListFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;


public class MainPagerActivity extends ActionBarActivity {
	
	// Debugging tag for the application
    private static final String HOMEACTIVITY_TAG = "HomeActivity";

    /**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each 
	 * of the sections. We use a  {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it may be best to switch 
	 * to a  {@link android.support.v13.app.FragmentStatePagerAdapter} .
	 */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
	 * The {@link ViewPager} that will host the section contents. A {@link ViewPager} is a
	 * fragment container which requires a PagerAdapter.
	 */
    ViewPager mViewPager;
    
    // The indicator's tabs.
    private PagerSlidingTabStrip tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
//    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        Log.d(HOMEACTIVITY_TAG, getString(R.string.onCreate));
		logDeviceScreenMetris();
		

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        
        // Set up the tabs.
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        
        // Bind the tabs to the view pager.
        tabs.setViewPager(mViewPager);

        // Listen for changes in the page currently being displayed by ViewPager. ///////////////////DEN MPAINEI POTE SE AYTON TON KODIKA///////////////////
        tabs.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
        	
        	// Control whether the page animation is being actively dragged,
        	// settling to a steady state, or idling.
        	public void onPageScrollStateChanged(int state) {
        	}
        	
        	// Control exactly where your page is going to be.
        	public void onPageScrolled(int position, float posOffset, int posOffsetPixels) {
        	}
        	
        	// Control the selected page.
            @Override
            public void onPageSelected(int position) {
            }
            
        });
    }
    
    /*
     * Save data of app state when on pause or stop or on a rotation change.
     * ( Restore data during onCreate() or onRestoreInstanceState() )
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//outState.putInt("currentColor", currentColor);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		//currentColor = savedInstanceState.getInt("currentColor");
		//changeColor(currentColor);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(HOMEACTIVITY_TAG, getString(R.string.onStart));
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(HOMEACTIVITY_TAG, getString(R.string.onResume));

		try {
			// Check for Google Play services APK
			// if not, the class informs the user with an error dialog
			if (GooglePlayServicesController.get(this).servicesConnected()) {
				// Application exits at that point
			}
		} catch (Exception e) {
			Toast.makeText(this,
					"Please refer to your software provider refering this type of error: "
							+ e, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(HOMEACTIVITY_TAG, getString(R.string.onPause));
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(HOMEACTIVITY_TAG, getString(R.string.onStop));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//%%%%%%%%%%%% DETERMINE THE BEHAVIOUR OF THE APP HERE %%%%%%%%%%%%%%%%
		// %%%%% KANONIKA TA SHERED PREFS PREPEI NA ADEIAZOYN OTN KATASTREFETAI TO APP KAI OXI OTAN KATESTREFETAI TO HOMEACTIVITY %%%%%%
		//this.getSharedPreferences(NavigationDrawerFragment.DRAWER_PREFS_NAME, 0).edit().clear().commit();
		//this.getSharedPreferences(KartaStateManager.MAP_PREFS_NAME, 0).edit().clear().commit();
		Log.d(HOMEACTIVITY_TAG, getString(R.string.onDestroy));
	}
	
	/*
	 * Images that should change between landscape and portrait, must be re-assign each resource to each element here.
	 * 
	 * During a configuration change a Configuration object can be used to read fields in the new
	 * configuration and  make appropriate changes by updating the resources used in your interface.
	 * At the time this method is called, the activity's Resources object is updated to return
	 * resources based on the new configuration (e.g. reset any ImageViews with setImageResource()),
	 * we can easily reset elements of the UI without the system restarting the activity.
	 * 
	 * (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	Log.d(HOMEACTIVITY_TAG, "Orientation -> Landscape");
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    	Log.d(HOMEACTIVITY_TAG, "Orientation -> Portrait");
	    }
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	
		switch (item.getItemId()) {
		case R.id.action_search:
			Log.d(HOMEACTIVITY_TAG, "[onOptionsItemSelected]:  Search Pressed");
			return true; 
		case R.id.action_refresh:
			Log.d(HOMEACTIVITY_TAG, "[onOptionsItemSelected]:  Refresh Pressed");
			return true;
		case R.id.action_settings:
			return true;
			
		}
        
        return super.onOptionsItemSelected(item);
    }
    
    // Change the default search icon for the search action view in the action bar.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchViewMenuItem);
        int searchImgId = android.support.v7.appcompat.R.id.search_button; // The explicit layout ID of searchview's ImageView
        ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_action_search);
        return super.onPrepareOptionsMenu(menu);
    }


    
	public void logDeviceScreenMetris() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Log.d(HOMEACTIVITY_TAG, "density(dpi):" + Integer.toString(dm.densityDpi));
		Log.d(HOMEACTIVITY_TAG, "density:" + Float.toString(dm.density));
		Log.d(HOMEACTIVITY_TAG, "heightpixels:" + Integer.toString(dm.heightPixels));
		Log.d(HOMEACTIVITY_TAG, "widthpixels:" + Integer.toString(dm.widthPixels));
	}
	

    /**
     * A {@link FragmentPagerAdapter} is an agent that returns a fragment corresponding to
     * one of the sections/tabs/pages and adds them to the activity. It helps the Viewpager to
     * identify the fragments' views so that they can be placed correctly.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	
        	switch (position) {
            case 0:
            	return UnitListFragment.newInstance(position + 1);
            case 1:
            	return //TestMapFragment.newInstance(position + 1);
            			GoogleMapsFragment.newInstance(position + 1);
            case 2:
            	return PlaceholderFragment.newInstance(position + 1);
            }

        	return null;
        	
        	
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.tab_title_UnitList).toUpperCase(l);
                case 1:
                    return getString(R.string.tab_title_karta).toUpperCase(l);
                case 2:
                    return getString(R.string.tab_title_caseList).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
