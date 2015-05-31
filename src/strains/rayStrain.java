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
public class rayStrain implements Strain {

	String strainName = "StrahlVIRUS ";
	int youngest = -1;

	public rayStrain(String str) {
		strainName = str;
	}

	public rayStrain() {
		strainName = "StrahlVIRUS ";
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
				org.move(org.northX, org.eastX, org.southX, org.westX);
			}
		}

		org.updates++;
		org.check();
	}
}