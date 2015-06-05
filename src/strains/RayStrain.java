package strains;

import src.Organism;
import src.Strain;

public class RayStrain implements Strain {

  int youngest = -1;

  @Override
  public String getStrainName() {
    return "Ray VIRUS ";
  }

  @Override
  public void updateYoungest(int orgGen) {
    youngest = Math.max(youngest, orgGen);
  }

  @Override
  public int getYoungest() {
    return youngest;
  }

  @Override
  public void update(Organism org) {
    org.feast();
    org.replicate();

    final int timesToMove = org.timesToMove();
    for (int i = 0; i < timesToMove; i++) {
      if (org.tooTired()) {
        break;
      }
      org.movePreferentially();
    }

    org.check();
  }
}