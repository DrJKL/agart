package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;
import core.Direction;

public class SearchingStrain implements Strain {

  @Override
  public String getStrainName() {
    return "Searching VIRUS";
  }

  @Override
  public void update(Organism org) {
    org.feast();
    org.replicate();
    IntStream.range(0, org.timesToMove()).forEach(i -> {
      final Direction max = org.viewMaxAll();
      org.move(max);
    });

    org.check();
  }
}