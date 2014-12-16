package petros.googleMaps.Extra;

import petros.alitis.R;
import android.content.Context;
import android.widget.ImageButton;

public class GoogleMapButton extends ImageButton {
	
	int mId = 0;

	public GoogleMapButton(Context context, int imageResource, int id) {

		super(context);
		setImageResource(imageResource);
		setId(id);
		
		setPadding(0, 0, 0, 0);
		setBackgroundColor(this.getContext().getResources().getColor(R.color.white));
		getBackground().setAlpha(190);

		setFocusableInTouchMode(false);
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}
	
	public void setBackgroundImage(int imageResource) {
		this.setImageResource(imageResource);
	}	
}
