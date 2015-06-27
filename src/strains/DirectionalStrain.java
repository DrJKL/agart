package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;
import core.Direction;

public class DirectionalStrain implements Strain {

  private final Direction preferredDirection;
  private final String name;

  public DirectionalStrain(Direction preferredDirection, String name) {
    this.preferredDirection = preferredDirection;
    this.name = name;
  }

  @Override
  public String getStrainName() {
    return name;
  }

  @Override
  public void update(Organism org) {
    org.feast();
    org.replicate();
    IntStream.range(0, org.timesToMove()).forEach(i -> {
      if (org.viewMaxAll() == preferredDirection) {
        org.move(preferredDirection);
      } else {
        org.move();
      }
    });
    org.check();
  }

  public static Strain drippy() {
    return new DirectionalStrain(Direction.SOUTH, "Drippy");
  }

  public static Strain floaty() {
    return new DirectionalStrain(Direction.NORTH, "Floaty");
  }

}