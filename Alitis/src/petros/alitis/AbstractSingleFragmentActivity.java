package petros.alitis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


/************* An Abstract activity for hosting a fragment **************
 * This activity acts as a generic fragment-hosting layout.
 * Subclasses of {@link #AbstractSingleFragmentActivity()} will implement
 * the method {@link #createFragment()} to return an instance of the 
 * fragment that the activity is hosting.
 * 
 * @author petroschariskos
 *
 */
public abstract class AbstractSingleFragmentActivity extends FragmentActivity {
	
	/**
	 * This method should be overridden in order to instantiate
	 * a fragment.
	 */
	protected abstract Fragment createFragment();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_fragment);
		
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.singleFragmentContainer);
		
		if (fragment == null) {
			fragment = createFragment();
			fragmentManager.beginTransaction()
				.add(R.id.singleFragmentContainer, fragment)
				.commit();
		}
	}
}
