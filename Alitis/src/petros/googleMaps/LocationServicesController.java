package petros.googleMaps;

import petros.alitis.R;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;

/**
 * Singleton static class. Checks if location services are enabled on the
 * device. If not, an alert dialog appears to the user with the opportunity to
 * select "Settings" and modify the "Location Mode".
 * 
 * NOTE! The code takes also the cases "MODE_SENSORS_ONLY" and
 * "LOCATION_MODE_BATTERY_SAVING" into account, yet, not elaborated. Configure
 * at will the if statement to give the desired outcome.
 * 
 * @author petroschariskos
 * 
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public final class LocationServicesController {

	// Debugging tag for this fragment
	private static final String LOCATION_SERVICES_CONTROLLER_TAG = "LocationServicesController";

	// The singleton instance to be returned. "s" prefix for a static variable.
	private static LocationServicesController sLocationServicesController;

	// Stores the location mode that is defined on the device's settings.
	private int mLocationMode;

	// Variables that store the messages that will appear to the user when the
	// dialog will be formed.
	int TITLE_ID = 0;
	int MESSAGE_ID = 0;

	// The application context.
	private Context mAppContext;

	private LocationServicesController(Context appContext) {
		mAppContext = appContext;
	}

	/**
	 * Context To ensure that the singleton has a long-term context to work
	 * with, getApplicationContext() is used.
	 * 
	 * @param c
	 * @return the singleton instance.
	 */
	public static LocationServicesController get(Context c) {
		if (sLocationServicesController == null) {
			sLocationServicesController = new LocationServicesController(
					c.getApplicationContext());
		}
		return sLocationServicesController;
	}

	@SuppressWarnings("deprecation")
	public boolean isLocationServicesEnabled(Context context) {

		String locationProviders;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			try {
				mLocationMode = Settings.Secure.getInt(
						context.getContentResolver(),
						Settings.Secure.LOCATION_MODE);

				Log.d(LOCATION_SERVICES_CONTROLLER_TAG,
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

			Log.d(LOCATION_SERVICES_CONTROLLER_TAG,
					"SDK < KITKAT. location Providers retrieved = "
							+ locationProviders); // TO BE TESTED

			return !TextUtils.isEmpty(locationProviders);
		}
	}

	/**
	 * Determines the degree of the location mode and forms the title and
	 * message of the dialog accordingly.
	 */
	private void formDialogMessages(int locationMode) {

		switch (locationMode) {

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
	}

	// returns the alert dialog
	public LocationServicesDialogFragment getLocationServicesAlertDialog() {

		// Calculate TITLE_ID and MESSAGE_ID according to location mode
		formDialogMessages(mLocationMode);

		// Create the dialog object
		LocationServicesDialogFragment d = new LocationServicesDialogFragment(
				TITLE_ID, MESSAGE_ID);

		return d;
	}

	// The alert dialog class.
	protected static class LocationServicesDialogFragment extends
			DialogFragment {

		// Buttons text
		final static int NEGATIVE_BUTTON_ID = R.string.LocationServices_NegativeButton;
		final static int POSITIVE_BUTTON_ID = R.string.LocationServices_PositiveButton;

		// Text used to form the dialog message and title
		int MESSAGE = 0;
		int TITLE = 0;

		public LocationServicesDialogFragment(int title, int message) {
			super();
			this.MESSAGE = message;
			this.TITLE = title;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setMessage(MESSAGE)
					.setTitle(TITLE)
					.setPositiveButton(POSITIVE_BUTTON_ID,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									getActivity()
											.startActivity(
													new Intent(
															Settings.ACTION_LOCATION_SOURCE_SETTINGS));
								}
							})
					.setNegativeButton(NEGATIVE_BUTTON_ID,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

			// Create the AlertDialog object and return it
			return builder.create();
		}
	}
}
