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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
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
 * This Class requests regular updates about the device's location using the requestLocationUpdates()
 * method in the fused location provider.
 * 
 * @author petroschariskos
 *
 */
public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
	
	// Debugging tag for this fragment
	private static final String GOOGLE_MAPS_FRAGMENT_TAG = "GoogleMapFragment";

	/**
	 * A View which displays a map (with data obtained from the Google Maps service). When focused,
	 * it will capture keypresses and touch gestures to move the map.
	 */
	private MapView mMapView;
	
	// The Google API client. Common entry point to all the Google Play services.
	// Manages the network connection between the user's device and each Google service.
	private GoogleApiClient mGoogleApiClient;
	
	// The Location Request Object
	LocationRequest mLocationRequest;
	
	// Global variable to hold the current (last known location) location
	private Location mCurrentLocation;
	
	// The initial zoom of the google map
	private static final float CAMERA_ZOOM = 15;
	
	// The google map type
	private int mGoogleMapType;
	
	// The camera position
	private CameraPosition mCameraPosition;
	
	// Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    
    // Boolean to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    
    // Boolean to keep track of location updates
    private boolean mUpdatingLocation = false;
    
    // track whether location updates are currently turned on
    private boolean mRequestingLocationUpdates = true;			// FLAG TO BE USED IN THE FUTURE UI WITH BUTTON ETC.
    
    // Unique tag for storing the state of location updates
    private static final String KEY_REQUESTING_LOCATION_UPDATES = "request location updates";
    
    // Unique tag for storing the state of the current location
    private static final String KEY_CURRENT_LOCATION = "location";
    
    // Unique tag for storing the resolving error boolean
    private static final String KEY_RESOLVING_ERROR = "resolving_error";
    
    // flag that denotes if the state is stored in the shared preferences 
    private boolean isStateStored = false;
    
    //private GoogleMap mGoogleMap; use mMapView.getMap() instead
	
	/**
	 * Configure the fragment's instance in this method. A bundle used to save
	 * and retrieve the fragment's state.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		updateValuesFromBundle(savedInstanceState);
		
		// build the Google API Client
		buildGoogleApiClient();

		// create the location request
		createLocationRequest();
		
		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "OnCreate");
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
		
		// Use this class to initialize the Google Maps Android API. Must be called because CameraUpdateFactory need to be initialized.
		// DON'T REMOVE THIS CODE
		try {
	        MapsInitializer.initialize(getActivity().getApplicationContext());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
		// the map type imagebutton
		final ImageButton mapTypeImageButton = (ImageButton) v.findViewById(R.id.imageButton_MapType);
		
		// Set the background image of the image button
		mapTypeImageButton.setImageResource(getMapTypeImage());
		
		// Set up onClick functionality
		mapTypeImageButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				// if satellite map type
				if (mMapView.getMap().getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
					
					// set the background to be the normal image after the map type change
					mapTypeImageButton.setImageResource(R.drawable.ic_map_satellite);
					updateMapType();
				} 
				
				// if normal map type
				else {
					updateMapType();
					mapTypeImageButton.setImageResource(R.drawable.ic_map_normal);
				}
			}
		});
		
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (!mResolvingError) {

			try {
				// Connect to Google Play Location Services
				mGoogleApiClient.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		
		try {
			
			// if location services are not enabled
			if (!LocationServicesController.get(getActivity()).isLocationServicesEnabled(getActivity())) {
				
				// if fragment dialog is not on the fragment stack i.e. is not already shown to the user
				if (getActivity().getFragmentManager().findFragmentByTag("LocationAlertDialog") == null) {
					
					// show an alert dialog with the "settings" option 
					LocationServicesController.get(getActivity())
					.getLocationServicesAlertDialog()
					.show(((Activity) getActivity()).getFragmentManager(), "LocationAlertDialog");
				}
			} 
			
			// Location Services are enabled
			else {
				Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "Location Services Enabled");
				
				// If the client is connected start the location updates
				if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
					
			        startLocationUpdates();
			        restoreCurrentState();
			    }
			}
		} catch (Exception e) {
			Toast.makeText(
					this.getActivity(),
					"Please refer to your software provider refering this type of error: "
							+ e, Toast.LENGTH_LONG).show();
		}
		
		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
		
		if (mUpdatingLocation) {
			stopLocationUpdates();
		}
		
		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "onPause");
	}
	
	/**
	 * Disconnects the google client to avoid leaks.
	 */
	@Override
	public void onStop() {
		mGoogleApiClient.disconnect();

		try {
			saveCurrentState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onStop();
		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "onStop");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "onDestroy");
	}

	@Override
	public void onLowMemory() { 
		super.onLowMemory();
		mMapView.onLowMemory();
		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "onLowMemory");
	}
	
	/**
	 * MapStateManager Class uses SharedPreferences to store the current state
	 * of the Map.
	 */
	private void saveCurrentState() {
		KartaStateManager mgr = new KartaStateManager(this.getActivity());
		mgr.saveMapState(mMapView.getMap());
		isStateStored = true;
	}
	
	/**
	 * Restores the state of the Map using SharedPreferences
	 * by calling the Class KartaStateManager. The restored values
	 * are the camera position {@link #mCameraPosition} and the
	 * map type {@link #mGoogleMapType}.
	 */
	private void restoreCurrentState() {
		KartaStateManager mgr = new KartaStateManager(this.getActivity());

		mCameraPosition = mgr.getSavedCameraPosition();
		mGoogleMapType = mgr.getMapType();

		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "mGoogleMapType " + mGoogleMapType);
		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "State restored");
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putBoolean(KEY_RESOLVING_ERROR, mResolvingError);
	    outState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
	    //outState.putParcelable(KEY_CURRENT_LOCATION, mCurrentLocation);
	}
	
	/**
	 * Restore the saved values from the previous instance of the activity, if
	 * they're available.
	 * 
	 * NOTE: The current implementation of the app does not destroy the activity on rotation.
	 * So, this method is useless at the moment.
	 * 
	 * @param savedInstanceState
	 */
	private void updateValuesFromBundle(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			
			// Update the value of mResolvingError from the Bundle
			if (savedInstanceState.keySet().contains(KEY_RESOLVING_ERROR)) {
				mResolvingError =  savedInstanceState.getBoolean(KEY_RESOLVING_ERROR, false);
			}
			
			// Update the value of mRequestingLocationUpdates from the Bundle
			if (savedInstanceState.keySet().contains(
					KEY_REQUESTING_LOCATION_UPDATES)) {
				mRequestingLocationUpdates = savedInstanceState
						.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
			}

//			// Update the value of mCurrentLocation from the Bundle
//			if (savedInstanceState.keySet().contains(KEY_CURRENT_LOCATION)) {
//				// Since LOCATION_KEY was found in the Bundle, we can be sure
//				// that mCurrentLocation is not null.
//				mCurrentLocation = savedInstanceState
//						.getParcelable(KEY_CURRENT_LOCATION);
//			}
		}
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
	 * Callback method to get a handle to the GoogleMap object. The callback is
	 * triggered when the map is ready to be used. It provides a non-null
	 * instance of GoogleMap.
	 */
	@Override
	public void onMapReady(GoogleMap map) {
		
		// Set the map type
		map.setMapType(getMapType());
		
		// Enable the location button
		map.setMyLocationEnabled(true);

		// Enable indoor maps
		map.setIndoorEnabled(true);

		// Turn the traffic layer on
		map.setTrafficEnabled(true);
		
//		// Use this code snippet to override the MyLocationButton functionality
//		map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
//			
//            @Override
//            public boolean onMyLocationButtonClick() {
//            	// TODO
//                return true;
//            }
//        });
	}
	
	/**
	 * 
	 * @return the map type to be applied
	 */
	protected int getMapType() {
		
		// if the map type has not been assigned yet for some reason
		if ( (mGoogleMapType != GoogleMap.MAP_TYPE_NORMAL) && (mGoogleMapType != GoogleMap.MAP_TYPE_SATELLITE) ) {
			// assign the normal map type
			mGoogleMapType = GoogleMap.MAP_TYPE_NORMAL;}
		
		return mGoogleMapType;
	}
	
	/**
	 * Interchanges the google map type between 
	 * MAP_TYPE_NORMAL and MAP_TYPE_SATELLITE
	 */
	protected void updateMapType() {
		if (mMapView.getMap().getMapType() == GoogleMap.MAP_TYPE_NORMAL)
			mMapView.getMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		else
			mMapView.getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}
	
	/**
	 * 
	 * @return the imageResource to be applied to the map type Image Button when
	 *         the fragment is being creating.
	 */
	private int getMapTypeImage() {

		int mapImageResource = -999;

		// if no value has been assigned to mGoogleMapType from the
		// restoreCurrentState()
		if ((mGoogleMapType != GoogleMap.MAP_TYPE_NORMAL)
				&& (mGoogleMapType != GoogleMap.MAP_TYPE_SATELLITE)) {

			// check the current map type
			if (mMapView.getMap().getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
				
				// the image has to show the satellite
				mapImageResource = R.drawable.ic_map_satellite;
			} else
				
				// the image has to show the normal
				mapImageResource = R.drawable.ic_map_normal;
		} 
		
		// if the mGoogleMapType has taken the normal type map
		// from restoreCurrentState()
		else if (mGoogleMapType == GoogleMap.MAP_TYPE_NORMAL) {
			mapImageResource = R.drawable.ic_map_satellite;
		}
		
		else
			mapImageResource = R.drawable.ic_map_normal;

		return mapImageResource;
	}
	
//	protected OnCameraChangeListener getCameraChangeListener() {
//		return new OnCameraChangeListener() {
//
//			private int CAMERA_MOVE_REACT_THRESHOLD_MS = 500;
//			private long lastCallMs = Long.MIN_VALUE;
//
//			@Override
//			public void onCameraChange(CameraPosition cameraPosition) {
//
//				if (mGoogleMapsButtonLayout != null) {
//					if ((mGoogleMapsButtonLayout
//							.isEnableLocationUpdatesBtnMODE_ON())
//							&& (mTouchView.isScreenTouched())) {
//
//						final long snap = System.currentTimeMillis();
//						if (lastCallMs + CAMERA_MOVE_REACT_THRESHOLD_MS > snap) {
//							lastCallMs = snap;
//							return;
//						}
//
//						// here the actual call whatever you want to do on
//						// camera change
//						mGoogleMapsButtonLayout
//								.enableLocationUpdatesBtnMODE_OFF();
//						stopPeriodicUpdates();
//						Log.d(MAPFRAGMENT_TAG,
//								"[onCameraChange]:Camera has changed and the location button has been updated.");
//
//						lastCallMs = snap;
//					}
//				}
//			}
//		};
//	}
	
	public static void animateCamera(final MapView mapView, final GoogleMap map, final CameraPosition retrievedCameraPosition) {
		
		try {
		
			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(retrievedCameraPosition)); 
			
		} catch (Exception e) {
			MapsInitializer.initialize(mapView.getContext());

			if (map != null) {

				map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

					@Override
					public void onMapLoaded() {
						if (retrievedCameraPosition != null) {
							animateCamera(mapView, mapView.getMap(), retrievedCameraPosition);
						}
					}
				});
			}
		}
	}
	
	public static void animateCamera(final MapView mapView,
			final GoogleMap map, final LatLng latLng) {

		try {
			
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(latLng)
					//.zoom(calculateZoom(map)) // Sets the zoom
					.zoom(CAMERA_ZOOM)
					// .bearing(90) // Sets the orientation of the camera
					// .tilt(30) // Sets the tilt of the camera to 30 degrees
					.build();

			map.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition)); 

		} catch (Exception e) {

			MapsInitializer.initialize(mapView.getContext());

			if (map != null) {

				map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

					@Override
					public void onMapLoaded() {
						if (latLng != null) {
							animateCamera(mapView, mapView.getMap(), latLng);
						}
					}
				});
			}
		}
	}
	
	// returns the latitude and the longitude of a location
	private LatLng getLatlong(Location location) {
		
		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		
		return latLng;
	}
	
	/**
	 * Uses the fused location API to get the last known location of a user's device.
	 * Should be used after the onConnected().
	 * @return the last location
	 */
	private Location getLastLocation() {
		
		mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
		if (mCurrentLocation != null) {
			Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "Latitude " + String.valueOf(mCurrentLocation.getLatitude()));
			Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "Longitude " + String.valueOf(mCurrentLocation.getLongitude()));
        }
		return mCurrentLocation;
	}
	
//	private static float calculateZoom(GoogleMap map) {
//
//		float mCurrentZoom = map.getCameraPosition().zoom;
//		float mZoom;
//		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "mCurrentZoom " + mCurrentZoom);
//
//		if (mCurrentZoom > 13) {
//			mZoom = mCurrentZoom;
//		} else {
//			mZoom = 14;
//		}
//		return mZoom;
//	}
	
	/**
	 * Instance that connects to Google Play services and the location services API.
	 * Is a prerequisite to get the last known location of a user's device.
	 */
	protected synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}
	
	// Create the Location Request Object
	protected void createLocationRequest() {
	    mLocationRequest = new LocationRequest();
	    mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
	    mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}
	
	protected void startLocationUpdates() {

		try {
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, mLocationRequest, this);
			mUpdatingLocation = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "Location Updates Started");
	}
	
	protected void stopLocationUpdates() {

		try {
			LocationServices.FusedLocationApi.removeLocationUpdates(
					mGoogleApiClient, this);
			mUpdatingLocation = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d(GOOGLE_MAPS_FRAGMENT_TAG, "Location Updates Stoped");
	}
	
	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		// TODO
	}

	// During onConnectionFailed() callback, we should call hasResolution() on the provided ConnectionResult object
	// and resolve the error by calling startResolutionForResult
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

	// When connected to Google Play Location Services
	@Override
	public void onConnected(Bundle arg0) {

		// if location services are enabled (Important check! otherwise throws
		// an error when is not enabled on the device)
		if (LocationServicesController.get(getActivity()).isLocationServicesEnabled(getActivity())) {

			try {

				/*
				 * if the state hasn't been stored yet, which translates to 
				 * "the main activity has been created and has not been stopped yet",
				 * so is the first "app opening" for the user
				 */
				if (!isStateStored) {
					// animate the camera to the current location
					animateCamera(mMapView, mMapView.getMap(), getLatlong(getLastLocation()));
				} 
				
				/*
				 * if the state has been saved, which translates to
				 * "the user has stopped and resumed the activity", but not killed it
				 * and has return to the app
				 */
				else
					//animate the camera to the retrieved camera position
					animateCamera(mMapView, mMapView.getMap(), mCameraPosition);
				
				// if location updates on start location updates
				if (mRequestingLocationUpdates) {
			        startLocationUpdates();
			    }
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
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
    /**
     * Once the user completes the resolution provided by startResolutionForResult() or
     * GooglePlayServicesUtil.getErrorDialog(), the activity receives the onActivityResult()
     * callback with the RESULT_OK result code. We can then call connect() again.
     */
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
		
		private GoogleMapsFragment mGoogleMapsFragment;
		
		public ErrorDialogFragment(GoogleMapsFragment mGoogleMapsFragment) {
			this.mGoogleMapsFragment = mGoogleMapsFragment;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			
			return GooglePlayServicesUtil.getErrorDialog(errorCode,
					getActivity(), REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			super.onDismiss(dialog);
			mGoogleMapsFragment.onDialogDismissed();
		}
	}
}