package strains;

import src.Organism;
import src.Strain;

public class DefaultStrain implements Strain {

  int youngest = -1;

  @Override
  public String getStrainName() {
    return "Default VIRUS";
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
      org.move();
    }
    org.check();
  }
}