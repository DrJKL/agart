package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;

public class MultiTaskingRayStrain implements Strain {

  @Override
  public String getStrainName() {
    return "MT Ray";
  }

  @Override
  public void update(Organism org) {
    IntStream.range(0, org.timesToMove()).forEach(i -> {
      IntStream.range(0, 5).forEach(j -> org.acquireRand());
      if (Math.random() > .5) {
        org.movePreferentially();
      }
    });
    org.replicate();
    org.check();
  }
}