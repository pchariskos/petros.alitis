package petros.googleMaps;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class KartaStateManager {

	public static final String MAPSTATE_TAG = "KartaStateManager";

	private static final String LONGITUDE = "longitute";
	private static final String LATITUDE = "latitude";
	private static final String ZOOM = "zoom";
	private static final String BEARING = "bearing";
	private static final String TILT = "tilt";
	private static final String MAPTYPE = "MAPTYPE";

	// Used to identify an instance of SharedPreferences
	public static final String MAP_PREFS_NAME = "mapState";

	private SharedPreferences mapStatePrefs;

	public KartaStateManager(Context context) {
		mapStatePrefs = context.getSharedPreferences(MAP_PREFS_NAME,
				Context.MODE_PRIVATE);
	}

	public void saveMapState(GoogleMap map) {
		SharedPreferences.Editor editor = mapStatePrefs.edit();
		// editor.clear();
		CameraPosition position = map.getCameraPosition();

		editor.putFloat(LATITUDE, (float) position.target.latitude);
		editor.putFloat(LONGITUDE, (float) position.target.longitude);
		editor.putFloat(ZOOM, position.zoom);
		editor.putFloat(TILT, position.tilt);
		editor.putFloat(BEARING, position.bearing);
		editor.putInt(MAPTYPE, map.getMapType());

		Log.d(MAPSTATE_TAG, "Map state saved");
		editor.commit();
	}

	public CameraPosition getSavedCameraPosition() {
		double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
		// if there is no camera position
		if (latitude == 0) {
			return null;
		}
		double longitude = mapStatePrefs.getFloat(LONGITUDE, 0);
		LatLng target = new LatLng(latitude, longitude);

		float zoom = mapStatePrefs.getFloat(ZOOM, 0);
		float bearing = mapStatePrefs.getFloat(BEARING, 0);
		float tilt = mapStatePrefs.getFloat(TILT, 0);

		CameraPosition position = new CameraPosition(target, zoom, tilt,
				bearing);
		Log.d(MAPSTATE_TAG, "Map state retrieved");
		return position;
	}

	public int getMapType() {
		int mapType = mapStatePrefs.getInt(MAPTYPE, 0);
		if (mapType == 0) {
			return mapType = GoogleMap.MAP_TYPE_NORMAL;
		}
		return mapType;
	}
}
