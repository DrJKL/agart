package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;

public class MultiTaskingStrain implements Strain {

  int youngest = -1;

  @Override
  public String getStrainName() {
    return "Multitasking VIRUS";
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
    IntStream.range(0, org.timesToMove()).forEach(i -> {
      IntStream.range(0, 5).forEach(j -> org.acquireRand());
      org.move();
      if (Math.random() > .5) {
        org.move();
      }
    });
    org.replicate();
    org.check();
  }
}