package petros.units;

import java.util.UUID;

import android.support.v4.app.Fragment;
import petros.alitis.AbstractSingleFragmentActivity;


/**
 * Calls UnitFragment.newInstance(UUID) to create a new instance of a UnitFragment. Passes in the UUID
 * it retrieved from its Extra.
 * 
 * @author petroschariskos
 *
 */
public class UnitActivity extends AbstractSingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		
		UUID vehicleId = (UUID) getIntent().getSerializableExtra(UnitFragment.EXTRA_UNIT_ID);
		
		return UnitFragment.newInstance(vehicleId);
	}
}
