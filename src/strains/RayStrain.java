package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;

public class RayStrain implements Strain {

  int youngest = -1;

  @Override
  public String getStrainName() {
    return "Ray VIRUS";
  }

  @Override
  public void resetYoungest() {
    youngest = -1;
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
    IntStream.range(0, org.timesToMove()).forEach(i -> org.movePreferentially());
    org.check();
  }
}