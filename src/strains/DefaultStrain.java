package strains;

import src.Organism;
import src.Strain;

public class DefaultStrain implements Strain {

    final String strainName;
    int youngest = -1;

    public DefaultStrain(String str) {
        strainName = str;
    }

    public DefaultStrain() {
        strainName = "Default VIRUS ";
    }

    @Override
    public String getStrainName() {
        return strainName;
    }

    @Override
    public void youngest(int orgGen) {
        if (orgGen > youngest) {
            youngest = orgGen;
        }
    }

    @Override
    public int getYoungest() {
        return youngest;
    }

    @Override
    public void update(Organism org) {

        for (int j = 0; j < 15; j++) {
            org.acquireRand(org.getRedX(), org.getGreenX(), org.getBlueX());
        }

        if (org.getEnergy() >= org.getEnergyCap() - 20 && org.getGeneration() < org.getBreedcap()
                && org.getChildrenSpawned() < org.getMaxkids()
                && Math.random() * 100 < org.getReprX()) {
            org.replicate();
        }

        for (int i = 0; i < Organism.randomInt(0, org.getMovesEach()); i++) {
            if (org.getEnergy() < org.getMoveCost()) {
                break;
            }
            org.move();
        }
        org.updates++;
        org.check();
    }
}