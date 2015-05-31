package strains;

import src.Organism;
import src.Strain;

public class DrippyStrain implements Strain {

    String strainName = "FlossVIRUS ";
    int youngest = -1;

    public DrippyStrain(String str) {
        strainName = str;
    }

    public DrippyStrain() {
        strainName = "FlossVIRUS ";
    }

    @Override
    public String getStrainName() {
        return strainName;
    }

    @Override
    public void renameStrain(String str) {
        strainName = str;
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

        for (int i = 0; i < org.randomInt(0, org.getMovesEach()); i++) {
            if (org.getEnergy() < org.getMoveCost()) {
                break;
            }
            if (org.viewMaxAll() == 2) {
                org.move(org.viewMaxAll());
            } else {
                org.move();
            }
        }

        org.updates++;
        org.check();
    }
}