package petros.googleMaps;

import petros.alitis.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

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
public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener{
	
	// Debugging tag for this fragment
	private static final String GOOGLE_MAPS_FRAGMENT_TAG = "GoogleMapFragment";

	/**
	 * A View which displays a map (with data obtained from the Google Maps service). When focused,
	 * it will capture keypresses and touch gestures to move the map.
	 */
	private MapView mMapView;
	
	private GoogleMap mGoogleMap;
	
	// The Google API client. Common entry point to all the Google Play services.
	// Manages the network connection between the user's device and each Google service.
	private GoogleApiClient mGoogleApiClient;
	
	// Global variable to hold the current location
	private Location mCurrentLocation;
	
	// Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    
    // Boolean to track whether the app is already resolving an error
    private boolean mResolvingError = false;
	
	/**
	 * Configure the fragment's instance in this method. A bundle used to save
	 * and retrieve the fragment's state.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		buildGoogleApiClient();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Inflate and return the layout
		View v = inflater.inflate(R.layout.fragment_maps, container, false);
		mMapView = (MapView) v.findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);

		mMapView.onResume();// needed to get the map to display immediately

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
	public void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
        	
        	// Connect to Google Play Location Services
            mGoogleApiClient.connect();
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		
		
		try {
			// if location services are not enabled
			if (!LocationServicesController.get(getActivity()).isLocationServicesEnabled(getActivity())) {
				
				// if fragment dialog is not on the fragment stack i.e. is not shown to the user
				if (getActivity().getFragmentManager().findFragmentByTag("LocationAlertDialog") == null) {
					
					// show an alert dialog with the "settings" option 
					LocationServicesController.get(getActivity())
					.getLocationServicesAlertDialog()
					.show(((Activity) getActivity()).getFragmentManager(), "LocationAlertDialog");
				}
			} else {
				Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "Location Services Enabled");
			}
		} catch (Exception e) {
			Toast.makeText(
					this.getActivity(),
					"Please refer to your software provider refering this type of error: "
							+ e, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}
	
	/** 
     * Disconnects the google client to avoid leaks. 
     */ 
	@Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
	 * Instance that connects to the Google Play Location Services.
	 */
	protected synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
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
	
	private void showCurrentPosition(GoogleMap map) {

		if (map != null) {

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(getCurrentLocation()).zoom(calculateZoom(map))
					// .bearing(location.getBearing())
					// .tilt(30)
					.build();
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));

		}
	}
	
	private LatLng getCurrentLocation() {

		// Getting latitude of the current location
		double latitude = mCurrentLocation.getLatitude();

		// Getting longitude of the current location
		double longitude = mCurrentLocation.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		return latLng;
	}
	
	private float calculateZoom(GoogleMap map) {

		float mCurrentZoom = map.getCameraPosition().zoom;
		float mZoom;
		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "mCurrentZoom " + mCurrentZoom);

		if (mCurrentZoom > 13) {
			mZoom = mCurrentZoom;
		} else {
			mZoom = 16;
		}
		return mZoom;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
	}

	@Override
	public void onConnected(Bundle arg0) {
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
	}
	
	/* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment(this);
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getActivity().getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == Activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

	/* A fragment to display an error dialog */
	public static class ErrorDialogFragment extends DialogFragment {
		
		GoogleMapsFragment mGoogleMapsFragment;
		
		public ErrorDialogFragment(GoogleMapsFragment mGoogleMapsFragment) {
			this.mGoogleMapsFragment = mGoogleMapsFragment;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GooglePlayServicesUtil.getErrorDialog(errorCode,
					this.getActivity(), REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			super.onDismiss(dialog);
			mGoogleMapsFragment.onDialogDismissed();
		}
	}
}
