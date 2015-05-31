package strains;

import src.Organism;
import src.Strain;

public class RayStrain implements Strain {

    String strainName = "StrahlVIRUS ";
    int youngest = -1;

    public RayStrain(String str) {
        strainName = str;
    }

    public RayStrain() {
        strainName = "StrahlVIRUS ";
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
            org.move(org.northX, org.eastX, org.southX, org.westX);
        }

        org.updates++;
        org.check();
    }
}