package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;
import core.Direction;

public class OrthoStrain implements Strain {

  public static final String NAME = "Ortho VIRUS";
  int youngest = -1;
  Direction dir = Direction.random();

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
    youngest = Math.max(youngest, orgGen);
  }

  @Override
  public int getYoungest() {
    return youngest;
  }

  @Override
  public void update(Organism org) {
    org.feast();
    org.replicate();
    if (Math.random() < 0.01) {
      dir = Direction.random();
    }
    IntStream.range(0, org.timesToMove()).forEach(i -> org.move(dir));
    org.check();
  }
}