package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;
import core.Direction;

public class DrippyStrain implements Strain {

  public static final String NAME = "Drippy VIRUS";
  int youngest = -1;

  @Override
  public String getStrainName() {
    return NAME;
  }

  @Override
  public void update(Organism org) {
    org.feast();
    org.replicate();
    IntStream.range(0, org.timesToMove()).forEach(i -> {
      final Direction max = org.viewMaxAll();
      if (max == Direction.SOUTH) {
        org.move(max);
      } else {
        org.move();
      }
    });

    org.check();
  }
}