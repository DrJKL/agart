package strains;

import src.Organism;
import src.Strain;
import core.Direction;

public class FloatyStrain implements Strain {

  final String strainName;
  int youngest = -1;

  public FloatyStrain(String str) {
    strainName = str;
  }

  public FloatyStrain() {
    strainName = "Floaty VIRUS ";
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

    org.replicate();

    final int randomInt = Organism.randomInt(0, org.getMovesEach());
    for (int i = 0; i < randomInt; i++) {
      if (org.getEnergy() < org.getMoveCost()) {
        break;
      }
      if (org.viewMaxAll() == 0) {
        org.move(Direction.fromInt(org.viewMaxAll()));
      } else {
        org.move();
      }
    }

    org.updates++;
    org.check();
  }
}