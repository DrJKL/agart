package strains;

import src.Organism;
import src.Strain;

public class RayStrain implements Strain {

  final String strainName;
  int youngest = -1;

  public RayStrain(String str) {
    strainName = str;
  }

  public RayStrain() {
    strainName = "Ray VIRUS ";
  }

  @Override
  public String getStrainName() {
    return strainName;
  }

  @Override
  public void youngest(int orgGen) {
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

    final int timesToMove = Organism.randomInt(0, org.getMovesEach());
    for (int i = 0; i < timesToMove; i++) {
      if (org.tooTired()) {
        break;
      }
      org.movePreferentially();
    }

    org.updates++;
    org.check();
  }
}