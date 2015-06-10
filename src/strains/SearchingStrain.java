package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;
import core.Direction;

public class SearchingStrain implements Strain {

  public static final String NAME = "Searching VIRUS";

  int youngest = -1;

  @Override
  public String getStrainName() {
    return NAME;
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
    IntStream.range(0, org.timesToMove()).forEach(i -> {
      final Direction max = org.viewMaxAll();
      org.move(max);
    });

    org.check();
  }
}