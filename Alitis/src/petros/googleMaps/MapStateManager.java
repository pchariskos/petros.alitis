package petros.googleMaps;

import petros.googleMaps.Extra.GoogleMapButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MapStateManager {
	
	public static final String MAPSTATE_TAG = "MapStateManager";

private static final String LONGITUDE = "longitute";
private static final String LATITUDE = "latitude";
private static final String ZOOM = "zoom";
private static final String BEARING = "bearing";
private static final String TILT = "tilt";
private static final String MAPTYPE = "MAPTYPE";
private static final String FIRST_RUN = "firstRun";
private static final String SHOW_ALL_CASES_BUTTON = "ShowAllCasesBtn";
private static final String SHOW_ACTIVE_CASES_BUTTON = "ShowActiveCasesBtn";
private static final String ENABLE_LOCATION_UPDATES_BUTTON = "EnableLocationUpdatesBtn";
private static final String SATELITE_VIEW_BUTTON = "SateliteViewBtn";

//Used to identify an instance of SharedPreferences
public static final String MAP_PREFS_NAME = "mapState";

private SharedPreferences mapStatePrefs;

public MapStateManager(Context context) {
	mapStatePrefs = context.getSharedPreferences(MAP_PREFS_NAME, Context.MODE_PRIVATE);
}

public void saveGMapState (GoogleMap map) {
	SharedPreferences.Editor editor = mapStatePrefs.edit();
	//editor.clear();
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

public void saveFirstRun(boolean b) {
	SharedPreferences.Editor editor = mapStatePrefs.edit();
	editor.putBoolean(FIRST_RUN, b);
	
	Log.d(MAPSTATE_TAG, b + " saved");
	editor.commit();
}

public void saveShowAllCasesBtnSelectionState(GoogleMapButton b) {
	SharedPreferences.Editor editor = mapStatePrefs.edit();

	editor.putBoolean(SHOW_ALL_CASES_BUTTON, b.isSelected());
	
	Log.d(MAPSTATE_TAG, "SHOW_ALL_CASES_BUTTON State saved");
	editor.commit();
}

public void saveShowActiveCasesBtnSelectionState(GoogleMapButton b) {
	SharedPreferences.Editor editor = mapStatePrefs.edit();
	
	editor.putBoolean(SHOW_ACTIVE_CASES_BUTTON, b.isSelected());
	
	Log.d(MAPSTATE_TAG, "SHOW_ACTIVE_CASES_BUTTON State saved");
	editor.commit();
}

public void saveEnableLocationUpdatesBtnSelectionState(GoogleMapButton b) {
	SharedPreferences.Editor editor = mapStatePrefs.edit();
	
	editor.putBoolean(ENABLE_LOCATION_UPDATES_BUTTON, b.isSelected());
	
	Log.d(MAPSTATE_TAG, "ENABLE_LOCATION_UPDATES_BUTTON State saved");
	editor.commit();
}

public void saveSateliteViewBtnSelectionState(GoogleMapButton b) {
	SharedPreferences.Editor editor = mapStatePrefs.edit();
	
	editor.putBoolean(SATELITE_VIEW_BUTTON, b.isSelected());
	
	Log.d(MAPSTATE_TAG, "SATELITE_VIEW_BUTTON State saved");
	editor.commit();
}

public boolean getShowAllCasesBtnSelectionState() {
	boolean isShowAllCasesBtnSelected = mapStatePrefs.getBoolean(SHOW_ALL_CASES_BUTTON, false);//if ShowAllCasesBtn does not exist return false
	
	return isShowAllCasesBtnSelected;
}

public boolean getShowActiveCasesBtnSelectionState() {
	boolean isShowActiveCasesBtnSelected = mapStatePrefs.getBoolean(SHOW_ACTIVE_CASES_BUTTON, false);//if ActiveCasesBtn does not exist return false
	
	return isShowActiveCasesBtnSelected;
}

public boolean getEnableLocationUpdatesBtnSelectionState() {
	boolean isEnableLocationUpdatesBtnSelected = mapStatePrefs.getBoolean(ENABLE_LOCATION_UPDATES_BUTTON, false);//if EnableLocationUpdatesBtn does not exist return false
	
	return isEnableLocationUpdatesBtnSelected;
}

public boolean getSateliteViewBtnSelectionState() {
	boolean isSateliteViewBtnSelected = mapStatePrefs.getBoolean(SATELITE_VIEW_BUTTON, false);//if SateliteViewBtn does not exist return false
	
	return isSateliteViewBtnSelected;
}

public CameraPosition getSavedCameraPosition() {
	double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
	//if there is no camera position
	if (latitude == 0) {
		return null;
	}
	double longitude = mapStatePrefs.getFloat(LONGITUDE, 0);
	LatLng target = new LatLng(latitude, longitude);
	
	float zoom = mapStatePrefs.getFloat(ZOOM, 0);
	float bearing = mapStatePrefs.getFloat(BEARING, 0);
	float tilt = mapStatePrefs.getFloat(TILT, 0);
	
	CameraPosition position = new CameraPosition(target, zoom, tilt, bearing);
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

public boolean getFirstRun() {
	boolean firstRun = mapStatePrefs.getBoolean(FIRST_RUN, true);//if firstTimeRun does not exist return true
	
	return firstRun;
}
}
