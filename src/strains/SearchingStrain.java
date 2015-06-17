package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;

public class SearchingStrain implements Strain {

  @Override
  public String getStrainName() {
    return "Searching VIRUS";
  }

  @Override
  public void update(Organism org) {
    org.feast();
    org.replicate();
    IntStream.range(0, org.timesToMove()).forEach(i -> org.move(org.viewMaxAll()));
    org.check();
  }
}