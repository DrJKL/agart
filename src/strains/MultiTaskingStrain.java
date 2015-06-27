package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;

public class MultiTaskingStrain implements Strain {

  @Override
  public String getStrainName() {
    return "MT";
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