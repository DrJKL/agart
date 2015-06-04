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
    org.feast();
    org.replicate();

    final int randomInt = Organism.randomInt(0, org.getMovesEach());
    for (int i = 0; i < randomInt; i++) {
      if (org.tooTired()) {
        break;
      }
      final Direction max = org.viewMaxAll();
      if (max == Direction.NORTH) {
        org.move(max);
      } else {
        org.move();
      }
    }

    org.updates++;
    org.check();
  }
}