package petros.googleMaps;

import petros.googleMaps.Extra.GoogleMapsButtonLayout;
import petros.googleMaps.Extra.TouchableWrapper;
import petros.alitis.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.*;

public class GoogleMapFragment extends MapFragment implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener
{

	// Debugging tag for the application
	private static final String MAPFRAGMENT_TAG = "MapFragment";

	//private GooglePlayServicesManager mGooglePlayServicesManager;

	/*
	 * A listener connected to the activity's interface "HomeActivityListener".
	 * Read the Interface for more details.
	 */
	// private HomeActivityListener mHomeActivitylistener;

	// A request to connect to Location Services. This object holds accuracy and
	// frequency parameters.
	private LocationRequest mLocationRequest;

	// Stores the current instantiation of the location client in this object.
	//private LocationClient mLocationClient;

	// Stores the location mode that is defined in the device's settings.
	private static int mLocationMode;

	// Global variable to hold the current location
	private Location mCurrentLocation;

	// Global variable to hold the Google Maps
	private GoogleMap mGoogleMap;

	private UiSettings mUiSettings;

	// Layout that stores the buttons placed on the top of google maps
	private GoogleMapsButtonLayout mGoogleMapsButtonLayout;

	// Global boolean variable. Is true only when the first time the app runs.
	private boolean mFirstRun;

	// Global boolean variable to keep track of the periodic updates.
	private boolean mPeriodicUpdatesOn;

	// The original Content View of this fragment
	private View mOriginalContentView;

	private TouchableWrapper mTouchView;

	/**
	 * Connect the listener to the HomeActivitiy's implemented methods. The
	 * listener is used to keep decoupled the HomeActivity from the
	 * GoogleMapFragment.
	 */
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(MAPFRAGMENT_TAG, getString(R.string.onAttach));

		// try {
		// mHomeActivitylistener = (HomeActivityListener) activity;
		// } catch (ClassCastException castException) {
		// // If the activity does not implement the listener. Log the
		// exception.
		// castException.printStackTrace();
		// }
	}

	/**
	 * Configure the fragment's instance in this method. A bundle used to save
	 * and retrieve the fragment's state.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(MAPFRAGMENT_TAG, getString(R.string.onCreate));

		// Tell the fragment that it should receive a call to
		// "onCreateOptionsMenu(..)"
		//setHasOptionsMenu(true);
		mFirstRun = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		Log.d(MAPFRAGMENT_TAG, getString(R.string.onCreateView));
		
		

		/*******************************************************************************************************
		 * VIEW OPTIONS - START
		 ********************************************************************************************************/

		/*
		 * Wrap the MapView (mOriginalContentView) in a customized FrameLayout
		 * (TouchableWrapper). The wrapper's view is placed on top of the map to
		 * detect the users touch screen's actions.
		 */
		mOriginalContentView = super.onCreateView(inflater, parent,
				savedInstanceState);
		mTouchView = new TouchableWrapper(getActivity());
		mTouchView.addView(mOriginalContentView);

		/*
		 * Wrap the GoogleMapsButtonLayout View (mGoogleMapsButtonLayout) in the
		 * same customized FrameLayout (TouchableWrapper). The wrapper's view is
		 * placed on top.
		 */
		mGoogleMapsButtonLayout = new GoogleMapsButtonLayout(getActivity());
		mTouchView.addView(mGoogleMapsButtonLayout);

		/*******************************************************************************************************
		 * VIEW OPTIONS - END
		 ********************************************************************************************************/

		
		
		
		/*******************************************************************************************************
		 * LOCATION PARAMETERS - START
		 ********************************************************************************************************/

		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval to 5 seconds. Sets the rate in milliseconds
		 * at which the app prefers to receive location updates. If no other
		 * apps are receiving updates from Location Services, the app will
		 * receive updates at this rate.
		 */
		mLocationRequest
				.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

		/*
		 * Set the interval ceiling to one minute. Sets an upper limit to the
		 * update rate (in milliseconds) at which the app can handle location
		 * updates to prevent problems with UI flicker or data overflow.
		 */
		mLocationRequest
				.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Location mode initialized to LOCATION_MODE_OFF
		mLocationMode = 0;

		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks. The first arg is the HomeActivity's context; the next two
		 * 'this' args indicate that this class will handle callbacks associated
		 * with connection and connection errors, respectively (see the
		 * onConnected, onDisconnected, and onConnectionError callbacks below).
		 * The location client cannot be used until the onConnected callback
		 * fires, indicating a valid connection. At that point access to
		 * location is possible services such as present position and location
		 * updates.
		 */
//		mLocationClient = new LocationClient(getActivity()
//				.getApplicationContext(), this, this);
		Log.d(MAPFRAGMENT_TAG, getString(R.string.onCreateView)
				+ "Location client instantiated.");

		/*******************************************************************************************************
		 * LOCATION PARAMETERS - END
		 ********************************************************************************************************/
		
		

		initializeGoogleMap();
		initializeMapImgButtons();
		mPeriodicUpdatesOn = false;

		// Keep screen on while this map location tracking activity is running
		this.getActivity().getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		//return mTouchView;
		return mOriginalContentView;
	}

	@Override
	public View getView() {
		return mOriginalContentView;
	}



	/**
	 * Returns a new instance of this fragment for the given section number.
	 * 
	 * @param sectionNumber
	 * @return
	 */
	public static GoogleMapFragment newInstance(int index) {
		GoogleMapFragment fragment = new GoogleMapFragment();
		Bundle args = new Bundle();
		args.putInt("index", index);
		fragment.setArguments(args);
		return fragment;
	}

	// /**
	// * Static factory method that takes an int parameter, initializes the
	// * fragment's arguments, and returns the new fragment to the client.
	// */
	// public static GoogleMapFragment newInstance(int index) {
	// GoogleMapFragment f = new GoogleMapFragment();
	// Bundle args = new Bundle();
	// args.putInt("index", index);
	// f.setArguments(args);
	// return f;
	// }

	/*
	 * All fragments come with their own set of options menu callbacks. The
	 * above two methods have to be implemented on each fragment.
	 */
	// @Override
	// public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// super.onCreateOptionsMenu(menu, inflater);
	// inflater.inflate(R.menu.fragment_map, menu);
	// }

	/*
	 * Handle action bar item clicks here. The action bar will automatically
	 * handle clicks on the Home/Up button, so long as a parent activity in
	 * AndroidManifest.xml is specified.
	 */
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		switch (item.getItemId()) {
//		case R.id.action_search:
//			Log.d(MAPFRAGMENT_TAG, "[onOptionsItemSelected]:  Search Pressed");
//			return true;
//		case R.id.action_refresh:
//			Log.d(MAPFRAGMENT_TAG, "[onOptionsItemSelected]:  Refresh Pressed");
//			return true;
//
//		}
//		return super.onOptionsItemSelected(item);
//	}

	/***************************************************************************************************************************
	 * Checks if location services are enabled in the device. If not, an
	 * alertdialog appears for the user with the opportunity to select
	 * "Settings" and modify the "Location Mode".
	 * 
	 * NOTE that the method is implemented to take care only the case which the
	 * location services are disabled. Observe that in the method
	 * locationServicesPromtForAction(), the code takes also the cases
	 * "MODE_SENSORS_ONLY" and "LOCATION_MODE_BATTERY_SAVING" into account, but
	 * deliberately are never accessed. Configure at will the if statement.
	 ****************************************************************************************************************************/
	protected void checkLocationServicesAndPromtForAction() {

		try {

			if (!isLocationServicesEnabled(getActivity()
					.getApplicationContext())) {
				locationServicesPromtForAction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	protected static boolean isLocationServicesEnabled(Context context) {

		String locationProviders;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			try {
				mLocationMode = Settings.Secure.getInt(
						context.getContentResolver(),
						Settings.Secure.LOCATION_MODE);

				Log.d(MAPFRAGMENT_TAG,
						"SDK >= KITKAT. Location Mode retrieved = "
								+ Integer.toString(mLocationMode));

			} catch (SettingNotFoundException e) {
				e.printStackTrace();
			}

			return mLocationMode != Settings.Secure.LOCATION_MODE_OFF;

		} else {
			locationProviders = Settings.Secure.getString(
					context.getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

			Log.d(MAPFRAGMENT_TAG,
					"SDK < KITKAT. location Providers retrieved = "
							+ locationProviders); // TO BE TESTED

			return !TextUtils.isEmpty(locationProviders);
		}
	}

	/**
	 * Checks the degree of the location mode and shows an alertdialog for the
	 * user to take action.
	 */
	protected void locationServicesPromtForAction() {

		// Variables that store the messages that will appear to the user when
		// the dialog will be formed.
		int NEGATIVE_BUTTON_ID = R.string.LocationServices_NegativeButton;
		int POSITIVE_BUTTON_ID = R.string.LocationServices_PositiveButton;
		int TITLE_ID = 0;
		int MESSAGE_ID = 0;

		switch (mLocationMode) {

		/*
		 * Location Services are disabled.
		 */
		case Settings.Secure.LOCATION_MODE_OFF:

			TITLE_ID = R.string.LocationServices_Title;
			MESSAGE_ID = R.string.LocationServices_Message;

			break;

		/*
		 * Case where GPS is enabled and network location is disabled. Note, GPS
		 * can be slow to obtain location data but is accurate.
		 */
		case Settings.Secure.LOCATION_MODE_SENSORS_ONLY:

			TITLE_ID = R.string.LocationServices_Title_Network;
			MESSAGE_ID = R.string.LocationServices_Message_Network;

			break;

		/*
		 * Case where GPS is disabled and network location is enabled.
		 */
		case Settings.Secure.LOCATION_MODE_BATTERY_SAVING:

			TITLE_ID = R.string.LocationServices_Title_GPS;
			MESSAGE_ID = R.string.LocationServices_Message_GPS;

			break;
		}

		// Build a new alert dialog and inform the user for the current
		// Location Service's state
		new AlertDialog.Builder(getActivity())

				.setTitle(TITLE_ID)
				.setMessage(MESSAGE_ID)

				// add the 'positive button' to the dialog and give it a
				// click listener
				.setPositiveButton(POSITIVE_BUTTON_ID,
						new DialogInterface.OnClickListener() {

							// setup what to do when clicked
							public void onClick(DialogInterface dialog, int id) {
								// start the settings menu on the correct
								// screen for the user
								startActivity(new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				// add the 'negative button' to the dialog and
				// give it a click listener
				.setNegativeButton(NEGATIVE_BUTTON_ID,
						new DialogInterface.OnClickListener() {

							// setup what to do when clicked
							public void onClick(DialogInterface dialog, int id) {
								// remove the dialog
								dialog.cancel();
							}
							// finish creating the dialog and show to the
							// user
						}).create().show();

		Log.d(MAPFRAGMENT_TAG, "Dialog Created and shown to the user");
	}

	/**
	 * Since the client must be connected for the app to receive updates, the
	 * client is connected in onStart(). This ensures a valid, connected client
	 * while the app is visible. Called also when the fragment is restarted,
	 * even before it becomes visible.
	 */
	@Override
	public void onStart() {
		super.onStart();
		Log.d(MAPFRAGMENT_TAG, getString(R.string.onStart));

		try {
			// Connect the client.
			if (GooglePlayServicesController.get(getActivity()).servicesConnected()) {

//				mLocationClient.connect();
				Log.d(MAPFRAGMENT_TAG, getString(R.string.onStart)
						+ getString(R.string.location_services_connected));

			}
		} catch (Exception e) {
			Log.d(MAPFRAGMENT_TAG,
					getString(R.string.onStart)
							+ "Error on the LocationClient Connection:"
							+ e.getMessage());
		}
	}

	/**
	 * Called when the system detects that this Activity is now visible.
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.d(MAPFRAGMENT_TAG, getString(R.string.onResume));

		checkLocationServicesAndPromtForAction();
		restoreCurrentState();
		updateMapButtons();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(MAPFRAGMENT_TAG, getString(R.string.onPause));
		if (this.getActivity().isFinishing()) {
			Log.d(MAPFRAGMENT_TAG, "Activity is finishing");
		}
	}

	/**
	 * Called when the Activity is no longer visible at all. Stop updates and
	 * disconnect.
	 * 
	 * Disconnects the location client if connected. IMPORTANT!: NEVER return
	 * null here for the location client.
	 */
	@Override
	public void onStop() {
		super.onStop();
		Log.d(MAPFRAGMENT_TAG, getString(R.string.onStop));

//		if (mLocationClient.isConnected()) 
		{
			stopPeriodicUpdates();
//			mLocationClient.disconnect(); // The client is considered dead
			Log.d(MAPFRAGMENT_TAG, getString(R.string.onStop)
					+ getString(R.string.location_services_disconnected));
		}
//		saveCurrentState();
	}

	/**
	 * Release the API in onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(MAPFRAGMENT_TAG, getString(R.string.onDestroy));

//		if (mLocationClient != null && mLocationClient.isConnected()) {
//			mLocationClient.disconnect();
//			mLocationClient = null;
//			Log.d(MAPFRAGMENT_TAG, getString(R.string.onDestroy)
//					+ getString(R.string.location_services_disconnected));
//		}
		super.onDestroy();
	}

	/**
	 * MapStateManager Class uses SharedPreferences to restore the state of the
	 * Map.
	 */
	private void restoreCurrentState() {
		MapStateManager mgr = new MapStateManager(this.getActivity());

		mFirstRun = mgr.getFirstRun();

		mGoogleMapsButtonLayout.getShowAllCasesBtn().setSelected(
				mgr.getShowAllCasesBtnSelectionState());
		mGoogleMapsButtonLayout.getShowActiveCasesBtn().setSelected(
				mgr.getShowActiveCasesBtnSelectionState());
		mGoogleMapsButtonLayout.getEnableLocationUpdatesBtn().setSelected(
				mgr.getEnableLocationUpdatesBtnSelectionState());
		mGoogleMapsButtonLayout.getSateliteViewBtn().setSelected(
				mgr.getSateliteViewBtnSelectionState());

		CameraPosition position = mgr.getSavedCameraPosition();

		if (position != null) {
			CameraUpdate update = CameraUpdateFactory
					.newCameraPosition(position);
			mGoogleMap.moveCamera(update);
		}
		mGoogleMap.setMapType(mgr.getMapType());
	}

	/**
	 * MapStateManager Class uses SharedPreferences to store the current state
	 * of the Map.
	 */
	private void saveCurrentState() {
		MapStateManager mgr = new MapStateManager(this.getActivity());
		mgr.saveGMapState(mGoogleMap);
		mgr.saveFirstRun(mFirstRun);
		mgr.saveShowAllCasesBtnSelectionState(mGoogleMapsButtonLayout
				.getShowAllCasesBtn());
		mgr.saveShowActiveCasesBtnSelectionState(mGoogleMapsButtonLayout
				.getShowActiveCasesBtn());
		mgr.saveEnableLocationUpdatesBtnSelectionState(mGoogleMapsButtonLayout
				.getEnableLocationUpdatesBtn());
		mgr.saveSateliteViewBtnSelectionState(mGoogleMapsButtonLayout
				.getSateliteViewBtn());
	}

	/**
	 * Sets the Img Resource of the button according to the selection state if
	 * the button.
	 */
	private void updateMapButtons() {
		mGoogleMapsButtonLayout.updateButton(mGoogleMapsButtonLayout
				.getShowAllCasesBtn());
		mGoogleMapsButtonLayout.updateButton(mGoogleMapsButtonLayout
				.getShowActiveCasesBtn());
		mGoogleMapsButtonLayout.updateButton(mGoogleMapsButtonLayout
				.getEnableLocationUpdatesBtn());
		mGoogleMapsButtonLayout.updateButton(mGoogleMapsButtonLayout
				.getSateliteViewBtn());
	}

	/**
	 * Send a request to Location Services to start periodic updates.
	 */
	private void startPeriodicUpdates() {

		if (GooglePlayServicesController.get(getActivity()).servicesConnected()
				&& isLocationServicesEnabled(getActivity()
						.getApplicationContext())) {
//			mLocationClient.requestLocationUpdates(mLocationRequest, this);
			mPeriodicUpdatesOn = true;
			Toast.makeText(this.getActivity(), "Periodic Updates started",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Send a request to Location Services to stop periodic updates.
	 */
	private void stopPeriodicUpdates() {

		if (GooglePlayServicesController.get(getActivity()).servicesConnected()
				&& isLocationServicesEnabled(getActivity()
						.getApplicationContext()) && mPeriodicUpdatesOn) {
//			mLocationClient.removeLocationUpdates(this);
			Toast.makeText(this.getActivity(), "Periodic Updates stopped",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Show a dialog returned by Google Play services for the connection error
	 * code
	 * 
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
				this.getActivity(),
				LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			// errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(getFragmentManager(), MAPFRAGMENT_TAG);
		}
	}

	/********************************************************************************************************
	 * Before sending a request for location updates, implement first the
	 * interfaces that Location Services uses to communicate the connection
	 * status with the app. i.e. onConnected(Bundle), onDisconnected(),
	 * onConnectionFailed(ConnectionResult)
	 ********************************************************************************************************/

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {

//		mCurrentLocation = mLocationClient.getLastLocation();

		if (mFirstRun) {

			try {
				showCurrentPositionAndStartPeriodicUpdates();
				mFirstRun = false;
				Log.d(MAPFRAGMENT_TAG, "[onConnected]: mFirstTimeRun is "
						+ mFirstRun);
			} catch (Exception e) {
				e.printStackTrace(); // BETTER IMPLEMENTATION HERE
			}
		}
	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this.getActivity(), R.string.disconnected,
				Toast.LENGTH_SHORT).show();
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this.getActivity(),
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	/*
	 * Define the callback method that receives location updates. Is being
	 * invoked by Location Services. It passes in a Location object.
	 */
	@Override
	public void onLocationChanged(Location location) {

		// // Getting latitude of the current location
		// double latitude = location.getLatitude();
		//
		// // Getting longitude of the current location
		// double longitude = location.getLongitude();
		//
		// // Creating a LatLng object for the current location
		// LatLng latLng = new LatLng(latitude, longitude);
		//
		//
		// CameraPosition cameraPosition = new CameraPosition.Builder()
		// .target(latLng)
		// .zoom(18)
		// // .bearing(location.getBearing())
		// // //.tilt(30)
		// .build();
		// mGoogleMap.animateCamera(CameraUpdateFactory.
		// newCameraPosition(cameraPosition));

		// CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
		// 17);
		// mGoogleMap.animateCamera(cameraUpdate);

		// // Showing the current location in Google Map
		// mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		//
		//
		// CameraPosition camPos = new CameraPosition.Builder()
		// .target(new LatLng(latitude, longitude))
		// .zoom(18)
		// .bearing(location.getBearing())
		// .tilt(70)
		// .build();
		//
		// CameraUpdate camUpd3 =
		// CameraUpdateFactory.newCameraPosition(camPos);
		//
		// mGoogleMap.animateCamera(camUpd3);

		// mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,
		// 15));

		// Report to the UI that the location was updated
		// String msg = "Updated Location: " +
		// Double.toString(location.getLatitude()) + "," +
		// Double.toString(location.getLongitude());
		// Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	protected OnCameraChangeListener getCameraChangeListener() {
		return new OnCameraChangeListener() {

			private int CAMERA_MOVE_REACT_THRESHOLD_MS = 500;
			private long lastCallMs = Long.MIN_VALUE;

			@Override
			public void onCameraChange(CameraPosition cameraPosition) {

				if (mGoogleMapsButtonLayout != null) {
					if ((mGoogleMapsButtonLayout
							.isEnableLocationUpdatesBtnMODE_ON())
							&& (mTouchView.isScreenTouched())) {

						final long snap = System.currentTimeMillis();
						if (lastCallMs + CAMERA_MOVE_REACT_THRESHOLD_MS > snap) {
							lastCallMs = snap;
							return;
						}

						// here the actual call whatever you want to do on
						// camera change
						mGoogleMapsButtonLayout
								.enableLocationUpdatesBtnMODE_OFF();
						stopPeriodicUpdates();
						Log.d(MAPFRAGMENT_TAG,
								"[onCameraChange]:Camera has changed and the location button has been updated.");

						lastCallMs = snap;
					}
				}
			}
		};
	}

	protected void initializeGoogleMap() {

		if (mGoogleMap == null) {

			try {
				// Stash a reference to the Google Map
				mGoogleMap = getMap();
			} catch (Exception e) {
				Toast.makeText(
						this.getActivity(),
						"Please refer to your software provider refering this type of error: "
								+ e, Toast.LENGTH_LONG).show();
			}
		}

		if (mGoogleMap != null) {

			mGoogleMap.setOnCameraChangeListener(getCameraChangeListener());
			mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			mGoogleMap.setTrafficEnabled(true);

			// Show the user's location
			mGoogleMap.setMyLocationEnabled(true);

			// enable indoor maps
			mGoogleMap.setIndoorEnabled(true);

			mUiSettings = mGoogleMap.getUiSettings();

			mUiSettings.setZoomControlsEnabled(false);
			mUiSettings.setMyLocationButtonEnabled(false);

			mUiSettings.setCompassEnabled(true);
			mUiSettings.setScrollGesturesEnabled(true);
			mUiSettings.setZoomGesturesEnabled(true);
			mUiSettings.setTiltGesturesEnabled(true);
			mUiSettings.setRotateGesturesEnabled(true);
		}
	}

	private float calculateZoom() {

		float mCurrentZoom = mGoogleMap.getCameraPosition().zoom;
		float mZoom;
		Log.d(MAPFRAGMENT_TAG, "mCurrentZoom " + mCurrentZoom);

		if (mCurrentZoom > 13) {
			mZoom = mCurrentZoom;
		} else {
			mZoom = 16;
		}
		return mZoom;
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

	private void showCurrentPositionAndStartPeriodicUpdates() {

		if ((mGoogleMap != null) & (mGoogleMapsButtonLayout != null)) {

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(getCurrentLocation()).zoom(calculateZoom())
					// .bearing(location.getBearing())
					// .tilt(30)
					.build();
			mGoogleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));

			if (!mGoogleMapsButtonLayout.getEnableLocationUpdatesBtn()
					.isSelected()) {

				mGoogleMapsButtonLayout.enableLocationUpdatesBtnMODE_ON();

			}
			// startPeriodicUpdates();
		}
	}

	/**
	 * Create a new RelativeLayout with ImgButtons on top of the MapView to add
	 * extra functionality.
	 */
	protected void initializeMapImgButtons() {

		/**
		 * Functionality for "SateliteViewBtn". Switches from Satellite to plain
		 * View and vice versa.
		 */
		mGoogleMapsButtonLayout.getSateliteViewBtn().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						if ((mGoogleMap != null)
								&& (mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
								&& !mGoogleMapsButtonLayout
										.getSateliteViewBtn().isSelected()) {

							mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
							mGoogleMapsButtonLayout.sateliteViewBtnMODE_ON();

						} else if ((mGoogleMap != null)
								&& (mGoogleMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE)
								&& mGoogleMapsButtonLayout.getSateliteViewBtn()
										.isSelected()) {

							mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
							mGoogleMapsButtonLayout.sateliteViewBtnMODE_OFF();
						}
					}
				});

		/**
		 * Functionality for "EnableLocationUpdatesBtn".
		 */
		mGoogleMapsButtonLayout.getEnableLocationUpdatesBtn()
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						// When the user taps the button "EnableLocationUpdates"
						// we set manually that the screen hasn't been touched
						// because the action was on the button and not on the
						// map. Thus, we avoid to enter the "onCameraChange"
						// method which changes the EnableLocationUpdates icon
						// to default.
						mTouchView.setScreenTouched(false);

						if ((mGoogleMap != null)
								&& !mGoogleMapsButtonLayout
										.getEnableLocationUpdatesBtn()
										.isSelected()) {
							if ((mCurrentLocation != null)
//									&& (mLocationClient.isConnected())
									) 
							{

								try {
									showCurrentPositionAndStartPeriodicUpdates();
								} catch (Exception e) {
									e.printStackTrace(); // BETTER
															// IMPLEMENTATION
															// NEEDED
								}
							} else {
								Toast.makeText(
										getActivity().getApplicationContext(),
										"Unable to get the current location",
										Toast.LENGTH_SHORT).show();
							}
						} else if ((mGoogleMap != null)
								&& mGoogleMapsButtonLayout
										.getEnableLocationUpdatesBtn()
										.isSelected()) {
							// WAITING FOR ACTION
						}
					}
				});

		/**
		 * Functionality for "ShowActiveCasesBtn".
		 */
		mGoogleMapsButtonLayout.getShowActiveCasesBtn().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						if ((mGoogleMap != null)
								&& !mGoogleMapsButtonLayout
										.getShowActiveCasesBtn().isSelected()) {

							mGoogleMapsButtonLayout.showActiveCasesBtnMODE_ON();
							mGoogleMapsButtonLayout.showAllCasesBtnMODE_OFF();

						} else if ((mGoogleMap != null)
								&& mGoogleMapsButtonLayout
										.getShowActiveCasesBtn().isSelected()) {
							mGoogleMapsButtonLayout
									.showActiveCasesBtnMODE_OFF();
						}
					}
				});

		/**
		 * Functionality for "ShowAllCasesBtn".
		 */
		mGoogleMapsButtonLayout.getShowAllCasesBtn().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						if ((mGoogleMap != null)
								&& !mGoogleMapsButtonLayout
										.getShowAllCasesBtn().isSelected()) {
							mGoogleMapsButtonLayout.showAllCasesBtnMODE_ON();
						} else if ((mGoogleMap != null)
								&& mGoogleMapsButtonLayout.getShowAllCasesBtn()
										.isSelected()) {
							mGoogleMapsButtonLayout.showAllCasesBtnMODE_OFF();
						}
					}
				});
	}
}
