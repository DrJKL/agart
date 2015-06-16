package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;
import core.Direction;

public class FloatyStrain implements Strain {

  @Override
  public String getStrainName() {
    return "Floaty VIRUS";
  }

  @Override
  public void update(Organism org) {
    org.feast();
    org.replicate();
    IntStream.range(0, org.timesToMove()).forEach(i -> {
      final Direction max = org.viewMaxAll();
      if (max == Direction.NORTH) {
        org.move(max);
      } else {
        org.move();
      }
    });
    org.check();
  }
}