package strains;

import src.Organism;
import src.Strain;
import core.Direction;

public class DrippyStrain implements Strain {

  final String strainName;
  int youngest = -1;

  public DrippyStrain(String str) {
    strainName = str;
  }

  public DrippyStrain() {
    strainName = "Drippy VIRUS ";
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
        && org.getChildrenSpawned() < org.getMaxkids() && Math.random() * 100 < org.getReprX()) {
      org.replicate();
    }

    final int randomInt = Organism.randomInt(0, org.getMovesEach());
    for (int i = 0; i < randomInt; i++) {
      if (org.getEnergy() < org.getMoveCost()) {
        break;
      }
      if (org.viewMaxAll() == 2) {
        org.move(Direction.fromInt(org.viewMaxAll()));
      } else {
        org.move();
      }
    }

    org.updates++;
    org.check();
  }
}