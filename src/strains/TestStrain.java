package strains;

import src.Organism;
import src.Strain;

public class TestStrain implements Strain {

  final String strainName;
  int youngest = -1;

  public TestStrain(String str) {
    strainName = str;
  }

  public TestStrain() {
    strainName = "Test ";
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
    // int updateX = randomInt(0, 2);

    // if (updateX == 0) {
    for (int j = 0; j < 15; j++) {
      org.acquireRand(org.getRedX(), org.getGreenX(), org.getBlueX());
      // acquireRed();
      // acquireBlue();
      // acquireGreen();
    }
    // }

    // if (updateX == 1) {
    if (org.getEnergy() >= org.getEnergyCap() - 20 && org.getGeneration() < org.getBreedcap()
        && org.getChildrenSpawned() < org.getMaxkids()) {
      org.replicate();
    }
    // }

    // if (updateX == 2) {
    final int randomInt = Organism.randomInt(0, org.getMovesEach());
    for (int i = 0; i < randomInt; i++) {
      if (org.getEnergy() < org.getMoveCost()) {
        break;
      }
      // if (org.viewMaxAll() == 2)
      // org.move(org.viewMaxAll());
      // else
      org.move();
      // move(northX, eastX, southX, westX);
    }
    // }
    // setView();
    org.updates++;
    // if (check)
    org.check();
  }
}