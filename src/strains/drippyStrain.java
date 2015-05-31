/**
 * 
 */
package strains;

import src.Organism;
import src.Strain;

/**
 * @author DrJKL
 * 
 */
public class drippyStrain implements Strain {

	String strainName = "FlossVIRUS ";
	int youngest = -1;

	public drippyStrain(String str) {
		strainName = str;
	}

	public drippyStrain() {
		strainName = "FlossVIRUS ";
	}

	public String getStrainName() {
		return strainName;
	}

	public void renameStrain(String str) {
		strainName = str;
	}

	public void youngest(int orgGen) {
		if (orgGen > youngest) {
			youngest = orgGen;
		}
	}

	public int getYoungest() {
		return youngest;
	}

	public void update(Organism org) {

		for (int j = 0; j < 15; j++) {
			org.acquireRand(org.getRedX(), org.getGreenX(), org.getBlueX());
		}

		if (org.getEnergy() >= org.getEnergyCap() - 20
				&& org.getGeneration() < org.getBreedcap()
				&& org.getChildrenSpawned() < org.getMaxkids()
				&& Math.random() * 100 < org.getReprX()) {
			org.replicate();
		}

		for (int i = 0; i < org.randomInt(0, org.getMovesEach()); i++) {
			if (org.getEnergy() < org.getMoveCost())
				break;
			else {
				if (org.viewMaxAll() == 2)
					org.move(org.viewMaxAll());
				else
					org.move();
			}
		}

		// setView();
		org.updates++;
		// if (check)
		org.check();
	}
}