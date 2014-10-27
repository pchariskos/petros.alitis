package petros.units;

import java.util.ArrayList;
import java.util.UUID;

import petros.alitis.R;

import android.content.Context;

/**
 * This is a singleton class and it works as a centralized data stash 
 * that stores Unit objetcs. Singleton allows only one instance of itself
 * to be created and it exists as long as the app stays in memory no matter
 * what happens with activities, fragments, and their lifecycles.
 * 
 * @author pchariskos
 *  
 */
public class UnitLab {

	// An ArrayList that supports an ordered list of objects.
	private ArrayList<Unit> mUnits;

	// The singleton instance to be returned. "s" prefix for a static variable.
	private static UnitLab sUnitLab;

	// The application context. An application-wide singleton must
	// always use the application context.
	private Context mAppContext;

	// The private constructor.
	private UnitLab(Context appContext) {

		mAppContext = appContext;

		constructUnits();

		// Use the below line to test the empty list View
		// mUnits.clear();
	}

	private void constructUnits() {

		mUnits = new ArrayList<Unit>();

		for (int i = 1; i < 8; i++) {
			Unit unit = new Unit();
			unit.setTitle("Ambulans #" + i);

			// Vehicle1
			if (i == 1) {
				unit.setStatus(R.string.unit_i_uppdrag);
			}

			// Vehicle2
			else if (i == 2) {
				unit.setStatus(R.string.unit_i_uppdrag);
			}

			// Vehicle3
			else if (i == 3) {
				unit.setStatus(R.string.unit_ledig);
			}

			// Vehicle4
			else if (i == 4) {
				unit.setStatus(R.string.unit_i_uppdrag);
			}

			// Vehicle5
			else if (i == 5) {
				unit.setStatus(R.string.unit_ledig);
			}

			// Vehicle6
			else if (i == 6) {
				unit.setStatus(R.string.unit_trasig);
			}

			// Vehicle7
			else if (i == 7) {
				unit.setStatus(R.string.unit_i_uppdrag);
			}

			mUnits.add(unit);
		}
	}

	/**
	 * @param c
	 *            Context To ensure that the singleton has a long-term context
	 *            to work with, getApplicationContext() is used.
	 * @return the singleton instance.
	 */
	public static UnitLab get(Context c) {
		if (sUnitLab == null) {
			sUnitLab = new UnitLab(c.getApplicationContext());
		}
		return sUnitLab;
	}

	/**
	 * @return the array list of vehicles.
	 */
	public ArrayList<Unit> getUnits() {
		return mUnits;
	}

	/**
	 * @param id
	 * @return the vehicle with the given id.
	 */
	public Unit getUnit(UUID id) {
		for (Unit unit : mUnits) {
			if (unit.getID().equals(id))
				return unit;
		}
		return null;
	}

}
