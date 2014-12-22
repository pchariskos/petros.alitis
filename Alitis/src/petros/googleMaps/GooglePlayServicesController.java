package petros.googleMaps;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import petros.alitis.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * A manager class that encapsulates the check for Google Play services APK on the Android device.
 * The check must be done before each connection attempt to Location Services and on the onResume()
 * method of the main activity.
 * 
 * @author petroschariskos
 *
 */
public class GooglePlayServicesController {
	
	// Debugging tag for this fragment
	private static final String GOOGLE_PLAY_SERVICES_CONTROLLER_TAG = "GooglePlayServicesManager";
	
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// The singleton instance to be returned. "s" prefix for a static variable.
	private static GooglePlayServicesController sGooglePlayServicesManager;

	// The application context. An application-wide singleton must
	// always use the application context.
	private Context mAppContext;

	private GooglePlayServicesController(Context appContext) {
		mAppContext = appContext;
	}
	
	/**
	 * Context To ensure that the singleton has a long-term context
	 * to work with, getApplicationContext() is used.
	 * 
	 * @param c
	 * @return the singleton instance.
	 */
	public static GooglePlayServicesController get(Context c) {
		if (sGooglePlayServicesManager == null) {
			sGooglePlayServicesManager = new GooglePlayServicesController(c.getApplicationContext());
		}
		return sGooglePlayServicesManager;
	}
	
	/**
     * @return true if Google Play services is available, otherwise false
     * 
     */
	public boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(mAppContext);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {

			// In debug mode, log the status
			Log.d(GOOGLE_PLAY_SERVICES_CONTROLLER_TAG, mAppContext.getString(R.string.play_services_available));

			return true;

			// Google Play services was not available for some reason
		} else {

			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					resultCode, (Activity) mAppContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {

				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();

				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);

				// Show the error dialog in the DialogFragment
				errorFragment.show(((Activity) mAppContext).getFragmentManager(),
						GOOGLE_PLAY_SERVICES_CONTROLLER_TAG);
			}
			return false;
		}
	}
	
	// Handle results returned to the hosting Activity. 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// Decide what to do based on the original request code
		switch (requestCode) {
		
		case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:

			/*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
			switch (resultCode) {
			
			// If Google Play services resolved the problem
            case Activity.RESULT_OK:

                // Log the result
                Log.d(GOOGLE_PLAY_SERVICES_CONTROLLER_TAG, mAppContext.getString(R.string.resolved));

                // Display the result
                Toast.makeText( (Activity) mAppContext, R.string.resolved, Toast.LENGTH_SHORT).show();
                Toast.makeText( (Activity) mAppContext, R.string.connected, Toast.LENGTH_SHORT).show();
            break;

            // If any other result was returned by Google Play services
            default:
            	
                // Log the result
                Log.d(GOOGLE_PLAY_SERVICES_CONTROLLER_TAG, mAppContext.getString(R.string.no_resolution));

                // Display the result
                Toast.makeText( (Activity) mAppContext, R.string.no_resolution, Toast.LENGTH_SHORT).show();
                Toast.makeText( (Activity) mAppContext, R.string.disconnected, Toast.LENGTH_SHORT).show();

            break;
        }

    // If any other request code was received
    default:
    	
       // Report that this Activity received an unknown requestCode
       Log.d(GOOGLE_PLAY_SERVICES_CONTROLLER_TAG,
    		   mAppContext.getString(R.string.unknown_activity_request_code, requestCode));

       break;
       
			}
		}
	
	/**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {
    	
        // Global field to contain the error dialog
        private Dialog mDialog;
        
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        
        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
