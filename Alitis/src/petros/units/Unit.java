package petros.units;

import java.util.UUID;

import com.google.android.gms.maps.model.LatLng;

// Represents the model of a single unit/vehicle.
public class Unit {

	// Unit Id
	private UUID mID;
	
	// Unit Title
	private String mTitle;
	
	// Unit Status
	private int mStatus;
	
	// Unit Status Icon
	private int mStatusIcon;
	
	// Unit Latitude and Longitude
	private LatLng mLatLng;


	public Unit() {
		super();
		mID = UUID.randomUUID();
	}

	public Unit(int icon, String title) {
		super();
		this.mStatusIcon = icon;
		this.mTitle = title;
	}
	
	public LatLng getmLatLng() {
		return mLatLng;
	}

	public void setmLatLng(LatLng mLatLng) {
		this.mLatLng = mLatLng;
	}

	@Override
	public String toString() {
		return mTitle;
	}

	public UUID getID() {
		return mID;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int status) {
		mStatus = status;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public int getStatusIcon() {
		return mStatusIcon;
	}

	public void setStatusIcon(int statusIcon) {
		mStatusIcon = statusIcon;
	}

}
