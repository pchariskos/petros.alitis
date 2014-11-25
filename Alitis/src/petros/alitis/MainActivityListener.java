package petros.alitis;

import android.content.Context;

/**
 * This Interface is used to keep decoupled the HomeActivity from the GoogleMapFragmet.
 * The HomeActivity overrides and implements the methods; An instance of the listener Interface is used 
 * in the GoogleMapFragment's onAttach(Activity) method to listen for changes in those methods.
 */
public interface MainActivityListener {
	
	public boolean servicesConnected();
	
	public Context myGetContext();

}
