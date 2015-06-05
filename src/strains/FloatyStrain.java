package strains;

import src.Organism;
import src.Strain;
import core.Direction;

public class FloatyStrain implements Strain {

  int youngest = -1;

  @Override
  public String getStrainName() {
    return "Floaty VIRUS ";
  }

  @Override
  public void updateYoungest(int orgGen) {
    youngest = Math.max(orgGen, youngest);
  }

  @Override
  public int getYoungest() {
    return youngest;
  }

  @Override
  public void update(Organism org) {
    org.feast();
    org.replicate();

    final int randomInt = org.timesToMove();
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

    org.check();
  }
}