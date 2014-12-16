package petros.googleMaps.Extra;

import petros.alitis.R;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class TestMapFragment extends MapFragment {
	
	private static final String ARG_ID = "Id";
	
	public View mOriginalContentView;
	public TouchableWrapper mTouchView;

	private GoogleMap mGoogleMap;
	
	public static TestMapFragment newInstance(int index) {
		TestMapFragment fragment = new TestMapFragment();
		Bundle args = new Bundle();
		args.putInt("index", index);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//View v = inflater.inflate(R.layout.fragment_map, parent, false);
	    
		
		if (mGoogleMap == null && getActivity() != null ) {
            
            
            	mGoogleMap = getMap();
           
        }
		//mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		
		//mGoogleMap.setTrafficEnabled(true);
	       
		mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//mGoogleMap.setMyLocationEnabled(true);
		//mGoogleMap.setIndoorEnabled(true);
	    
	    //return v;
		
//		mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
//		mTouchView = new TouchableWrapper(getActivity());
//		mTouchView.addView(mOriginalContentView);
//				
//		mGoogleMap = getMap();
//		
//		mGoogleMap.setMyLocationEnabled(true);
//		
//		//return mTouchView;
		return mOriginalContentView;
	}
	
//	@Override
//	public View getView() {
//		return mOriginalContentView;
//	}
	

}
