package petros.googleMaps;

import petros.alitis.R;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

/**
 * Adds the Map to the Android App and handles all the Map options. This class implements 
 * the interface {@link #onMapReadyCallback(GoogleMap)} and uses the {@link #onMapReady(GoogleMap)}
 * callback method to handle a GoogleMap object. The GoogleMap object is the internal representation
 * of the map itself. To set the view options for a map, we modify its GoogleMap object. We call the
 * getMapAsync() on the fragment to register the callback.
 * 
 * The key class in this map object is the GoogleMap class, it models the map object within the app.
 * 
 * Within the UI, a map will be represented by a {@link #MapView} object
 * (the other option would be a MapFragment).
 * 
 * A {@link #MapView} is a subclass of the Android View class, allows us to place a map in an Android View.
 * It acts as a container for the map, exposing core map functionality through the GoogleMap object. Must
 * forward all the activity life cycle methods - onCreate(), onDestroy(), onResume(), and onPause() -
 * to the corresponding methods in the MapView class.
 * 
 * @author petroschariskos
 *
 */
public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback {

	/**
	 * A View which displays a map (with data obtained from the Google Maps service). When focused,
	 * it will capture keypresses and touch gestures to move the map.
	 */
	private MapView mMapView;
	
	
	private GoogleMap mGoogleMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Inflate and return the layout
		View v = inflater.inflate(R.layout.fragment_maps, container, false);
		mMapView = (MapView) v.findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);

		mMapView.onResume();// needed to get the map to display immediately

		try {
			MapsInitializer.initialize(getActivity().getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Register the callback to the GoogleMap object. Must be called from the main thread,
		 * and the callback will be executed in the main thread. If Google Play services is not
		 * installed on the user's device, the callback will not be triggered until the user
		 * installs Play services.
		 */
		mMapView.getMapAsync(this);

		// Perform any camera updates here
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onLowMemory() { 
		super.onLowMemory();
		mMapView.onLowMemory();
	}
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 * 
	 * @param sectionNumber
	 * @return
	 */
	public static GoogleMapsFragment newInstance(int index) {
		GoogleMapsFragment fragment = new GoogleMapsFragment();
		Bundle args = new Bundle();
		args.putInt("index", index);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Callback method to get a handle to the GoogleMap object. The callback is triggered
	 * when the map is ready to be used. It provides a non-null instance of GoogleMap.
	 */
	@Override
	public void onMapReady(GoogleMap map) {
	    
		// Enable the location button
		map.setMyLocationEnabled(true);

		// Enable indoor maps
		map.setIndoorEnabled(true);
		
		// Turn the traffic layer on
		map.setTrafficEnabled(true);
		
	}
}