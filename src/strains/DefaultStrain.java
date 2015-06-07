package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;

public class DefaultStrain implements Strain {

  int youngest = -1;

  @Override
  public String getStrainName() {
    return "Default VIRUS";
  }

  @Override
  public void resetYoungest() {
    youngest = -1;
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
    IntStream.range(0, org.timesToMove()).forEach(i -> org.move());
    org.check();
  }
}