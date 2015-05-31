package strains;

import src.Organism;
import src.Strain;

public class defaultStrain implements Strain {

	String strainName = "AllgemeineVIRUS ";
	int youngest = -1;

	public defaultStrain(String str) {
		strainName = str;
	}

	public defaultStrain() {
		strainName = "AllgemeineVIRUS ";
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
				org.move();
			}
		}
		org.updates++;
		org.check();
	}
}