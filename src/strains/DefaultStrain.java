package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;

public class DefaultStrain implements Strain {

  @Override
  public String getStrainName() {
    return "Default";
  }

  @Override
  public void update(Organism org) {
    org.feast();
    org.replicate();
    IntStream.range(0, org.timesToMove()).forEach(i -> org.move());
    org.check();
  }
}