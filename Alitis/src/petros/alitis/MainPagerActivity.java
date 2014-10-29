package petros.alitis;

import java.util.Locale;

import petros.units.UnitListFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class MainPagerActivity extends ActionBarActivity implements ActionBar.TabListener {
	
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(HOMEACTIVITY_TAG, getString(R.string.onCreate));
		logDeviceScreenMetris();
		
		setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Listen for changes in the page currently being displayed by ViewPager.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
        	
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
                actionBar.setSelectedNavigationItem(position);
            }
            
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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
            	return PlaceholderFragment.newInstance(position + 1);
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
