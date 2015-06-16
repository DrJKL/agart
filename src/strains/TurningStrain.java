package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;
import core.Direction;

public class TurningStrain implements Strain {

  public static final String NAME = "Turning VIRUS";
  int youngest = -1;
  static Direction dir = Direction.random();

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

  public static void shiftDirectionMaybe() {
    if (Math.random() < 0.03) {
      dir = Math.random() > 0.5 ? dir.turnLeft() : dir.turnRight();
    }
  }

  @Override
  public void update(Organism org) {
    org.feast();
    org.replicate();
    IntStream.range(0, org.timesToMove()).forEach(i -> org.move(dir));
    org.check();
  }
}