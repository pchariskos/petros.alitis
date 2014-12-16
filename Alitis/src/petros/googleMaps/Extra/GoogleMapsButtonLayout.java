package petros.googleMaps.Extra;

import petros.googleMaps.LocationUtils;
import petros.alitis.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class GoogleMapsButtonLayout extends RelativeLayout {
	
	// Debugging tag for the application
	private static final String MAPBUTTONSLAYOUT_TAG = "MapButtonLayout";

	// The buttons that will appear on top of the Google maps
	private GoogleMapButton mShowAllCasesBtn;
	private GoogleMapButton mShowActiveCasesBtn;
	private GoogleMapButton mEnableLocationUpdatesBtn;
	private GoogleMapButton mSatelitePlainViewBtn;

	private boolean mEnableLocationUpdatesBtnMODE_ON = false;

	public GoogleMapsButtonLayout(Context context) {
		super(context);

		// this.setBackgroundColor(Color.RED);

		mShowAllCasesBtn = new GoogleMapButton(context,
				R.drawable.ic_all_default, 1000);
		mShowActiveCasesBtn = new GoogleMapButton(context,
				R.drawable.ic_emergency_default, 1001);
		mEnableLocationUpdatesBtn = new GoogleMapButton(context,
				R.drawable.ic_location_default, 1002);
		mSatelitePlainViewBtn = new GoogleMapButton(context,
				R.drawable.ic_satelliteview_default, 1003);

		// Add rules for the "Enable Location Updates" ImageButton
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // make comment

		params.setMargins(LocationUtils.COMMON_LEFT_ZEROMARGIN,
				LocationUtils.VERY_TOP_MARGIN,
				LocationUtils.COMMON_RIGHT_MARGIN, LocationUtils.BOTTOM_MARGIN);
		this.addView(mEnableLocationUpdatesBtn, params);

		// Add rules for the "Show All Cases" ImageButton
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // make comment
		params.addRule(RelativeLayout.CENTER_IN_PARENT);

		params.setMargins(LocationUtils.COMMON_LEFT_ZEROMARGIN,
				LocationUtils.IN_BETWEEN_MARGIN,
				LocationUtils.COMMON_RIGHT_MARGIN, LocationUtils.BOTTOM_MARGIN);
		this.addView(mShowAllCasesBtn, params);

		// Add rules for the "Show Active Cases" ImageButton
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.ABOVE, mShowAllCasesBtn.getId());
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // make comment

		params.setMargins(LocationUtils.COMMON_LEFT_ZEROMARGIN,
				LocationUtils.TOP_ZEROMARGIN,
				LocationUtils.COMMON_RIGHT_MARGIN,
				LocationUtils.IN_BETWEEN_MARGIN);
		this.addView(mShowActiveCasesBtn, params);

		// Add rules for the "Satellite\Plain View" ImageButton
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // make comment
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		params.setMargins(LocationUtils.COMMON_LEFT_ZEROMARGIN,
				LocationUtils.TOP_ZEROMARGIN,
				LocationUtils.COMMON_RIGHT_MARGIN,
				LocationUtils.VERY_BOTTOM_MARGIN);
		this.addView(mSatelitePlainViewBtn, params);
	}
	
	public void sateliteViewBtnMODE_ON() {
		
		mSatelitePlainViewBtn.setImageResource(R.drawable.ic_satelliteview_pressed);
		mSatelitePlainViewBtn.setSelected(true);
	}
	
	public void sateliteViewBtnMODE_OFF() {
		
		mSatelitePlainViewBtn.setImageResource(R.drawable.ic_satelliteview_default);
		mSatelitePlainViewBtn.setSelected(false);
	}
	
	public void showActiveCasesBtnMODE_ON() {
		
		mShowActiveCasesBtn.setImageResource(R.drawable.ic_emergency_pressed);
		mShowActiveCasesBtn.setSelected(true);
	}
	
	public void showActiveCasesBtnMODE_OFF() {
		
		mShowActiveCasesBtn.setImageResource(R.drawable.ic_emergency_default);
		mShowActiveCasesBtn.setSelected(false);
	}
	
	public void showAllCasesBtnMODE_ON() {
		
		mShowAllCasesBtn.setImageResource(R.drawable.ic_all_pressed);
		mShowAllCasesBtn.setSelected(true);
	}
	
	public void showAllCasesBtnMODE_OFF() {
		
		mShowAllCasesBtn.setImageResource(R.drawable.ic_all_default);
		mShowAllCasesBtn.setSelected(false);
	}
	
	public void enableLocationUpdatesBtnMODE_ON() {
		
		mEnableLocationUpdatesBtn.setImageResource(R.drawable.ic_location_pressed);
		mEnableLocationUpdatesBtn.setSelected(true);
		mEnableLocationUpdatesBtnMODE_ON = true;
	}
	
	public void enableLocationUpdatesBtnMODE_OFF() {
		
		mEnableLocationUpdatesBtn.setImageResource(R.drawable.ic_location_default);
		mEnableLocationUpdatesBtn.setSelected(false);
		mEnableLocationUpdatesBtnMODE_ON = false;
	}
	
	public boolean isEnableLocationUpdatesBtnMODE_ON() {
		return mEnableLocationUpdatesBtnMODE_ON;
	}
	
	public GoogleMapButton getSateliteViewBtn() {
		return mSatelitePlainViewBtn;
	}

	public void setSateliteViewBtn(GoogleMapButton satelitePlainViewBtn) {
		mSatelitePlainViewBtn = satelitePlainViewBtn;
	}

	public GoogleMapButton getShowAllCasesBtn() {
		return mShowAllCasesBtn;
	}

	public GoogleMapButton getShowActiveCasesBtn() {
		return mShowActiveCasesBtn;
	}

	public GoogleMapButton getEnableLocationUpdatesBtn() {
		return mEnableLocationUpdatesBtn;
	}

	public void setShowAllCasesBtn(GoogleMapButton showAllCasesBtn) {
		mShowAllCasesBtn = showAllCasesBtn;
	}

	public void setShowActiveCasesBtn(GoogleMapButton showActiveCasesBtn) {
		mShowActiveCasesBtn = showActiveCasesBtn;
	}

	public void setEnableLocationUpdatesBtn(GoogleMapButton enableLocationUpdatesBtn) {
		mEnableLocationUpdatesBtn = enableLocationUpdatesBtn;
	}
	
	public void updateButton(GoogleMapButton mapButton) {
		if (mapButton == mShowAllCasesBtn) {
			if ( mShowAllCasesBtn.isSelected() ) {
				mShowAllCasesBtn.setImageResource(R.drawable.ic_all_pressed);
			} else {
				mShowAllCasesBtn.setImageResource(R.drawable.ic_all_default);
			}
		}
		else if (mapButton == mShowActiveCasesBtn) {
			if ( mShowActiveCasesBtn.isSelected() ) {
				mShowActiveCasesBtn.setImageResource(R.drawable.ic_emergency_pressed);
			} else {
				mShowActiveCasesBtn.setImageResource(R.drawable.ic_emergency_default);
			}
		} else if (mapButton == mEnableLocationUpdatesBtn) {
			if ( mEnableLocationUpdatesBtn.isSelected() ) {
				mEnableLocationUpdatesBtn.setImageResource(R.drawable.ic_location_pressed);
				mEnableLocationUpdatesBtnMODE_ON = true;
			} else {
				mEnableLocationUpdatesBtn.setImageResource(R.drawable.ic_location_default);
				mEnableLocationUpdatesBtnMODE_ON = false;
			}
		} else if (mapButton == mSatelitePlainViewBtn) {
			if ( mSatelitePlainViewBtn.isSelected() ) {
				mSatelitePlainViewBtn.setImageResource(R.drawable.ic_satelliteview_pressed);
			} else {
				mSatelitePlainViewBtn.setImageResource(R.drawable.ic_satelliteview_default);
			}
		}
	}
	
	public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
		
//		Log.d(MAPBUTTONSLAYOUT_TAG, "The view's name is:" + viewGroup.getClass().getName());
//		Log.d(MAPBUTTONSLAYOUT_TAG, "The parent's view's name is:" + viewGroup.getParent().getClass().getName());
//		View view = (View) viewGroup.getParent();
//		view.bringToFront();
		
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);
			Log.d(MAPBUTTONSLAYOUT_TAG, "The child " + i +" with id=" + view.getId() + " is a:" + view.getClass().getName());
			view.setEnabled(enabled);
			if (view instanceof ViewGroup) {
				enableDisableViewGroup((ViewGroup) view, enabled);
			}
		}
	}
}