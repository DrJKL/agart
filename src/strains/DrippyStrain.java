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
    org.feast();
    org.replicate();

    final int randomInt = Organism.randomInt(0, org.getMovesEach());
    for (int i = 0; i < randomInt; i++) {
      if (org.getEnergy() < org.getMoveCost()) {
        break;
      }
      final Direction max = org.viewMaxAll();
      if (max == Direction.SOUTH) {
        org.move(max);
      } else {
        org.move();
      }
    }

    org.updates++;
    org.check();
  }
}