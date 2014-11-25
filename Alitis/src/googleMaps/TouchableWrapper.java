package googleMaps;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;


/**
 * The TouchableWrapper class declares an "UpdateMapAfterUserInterection" interface, which is implemented by the Map Fragment.
 * That way we have control over the map events, though is a hacky way of doing things. A frame layout is placed on top of
 * the Map Fragment's View intercepting the user interaction, and then it passed over to the map fragment.
 * @author petroschariskos
 *
 */
public class TouchableWrapper extends FrameLayout {
	
	// Debugging tag for the application
	public static final String MAPFRAGMENT_TAG = "MapFragment";

	private long lastTouched = 0;
	private static final long SCROLL_TIME = 200L; // 200 Milliseconds. Can be adjusted according to specifications
	private UpdateMapAfterUserInterection updateMapAfterUserInterection;
	
	private boolean isScreenTouched = false;
	
	public boolean isScreenTouched() {
		return isScreenTouched;
	}

	public void setScreenTouched(boolean isScreenTouched) {
		this.isScreenTouched = isScreenTouched;
	}

	public TouchableWrapper(Context context, AttributeSet attrs) {
		super(context, attrs);
		}

		public TouchableWrapper(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		}

		public TouchableWrapper(Context context) {
		super( context);
		}

	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isScreenTouched = true;
			Log.d(MAPFRAGMENT_TAG, "ACTION_DOWN");
			lastTouched = SystemClock.uptimeMillis();
			break;
		case MotionEvent.ACTION_UP:
			Log.d(MAPFRAGMENT_TAG, "ACTION_UP");
			final long now = SystemClock.uptimeMillis();
			if (now - lastTouched > SCROLL_TIME) {
				Log.d(MAPFRAGMENT_TAG, "now - lastTouched > SCROLL_TIME");
				// Update the map
				if (updateMapAfterUserInterection != null)
					updateMapAfterUserInterection
							.onUpdateMapAfterUserInterection();
				Log.d(MAPFRAGMENT_TAG, "onUpdateMapAfterUserInterection");
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	

	// Map Fragment must implement this interface
    public interface UpdateMapAfterUserInterection {
        public void onUpdateMapAfterUserInterection();
    }
    
    public void setUpdateMapAfterUserInterection(UpdateMapAfterUserInterection mUpdateMapAfterUserInterection){
    	this.updateMapAfterUserInterection = mUpdateMapAfterUserInterection;
    	}
}