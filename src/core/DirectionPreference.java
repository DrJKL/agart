package core;

import static core.Numbers.fix;

import java.util.Random;

import src.Organism;

public class DirectionPreference {
  private static final Random RANDOM = new Random();
  public final int northChance;
  public final int eastChance;
  public final int southChance;
  public final int westChance;

  public DirectionPreference(int northChance, int eastChance, int southChance, int westChance) {
    final boolean noPreference = //
    fix(northChance) + fix(eastChance) + fix(southChance) + fix(westChance) == 0;
    this.northChance = noPreference ? 1 : fix(northChance);
    this.eastChance = noPreference ? 1 : fix(eastChance);
    this.southChance = noPreference ? 1 : fix(southChance);
    this.westChance = noPreference ? 1 : fix(westChance);
  }

  public static DirectionPreference random() {
    return new DirectionPreference( //
        RANDOM.nextInt(4), RANDOM.nextInt(4), RANDOM.nextInt(4), RANDOM.nextInt(4));
  }

  public DirectionPreference mutate(Organism organism) {
    return new DirectionPreference(//
        organism.mutateTrait(northChance), //
        organism.mutateTrait(eastChance), //
        organism.mutateTrait(southChance), //
        organism.mutateTrait(westChance));
  }

  private int total() {
    return northChance + eastChance + southChance + westChance;
  }

  private int goNorth() {
    return northChance;
  }

  private int goEast() {
    return northChance + eastChance;
  }

  private int goSouth() {
    return northChance + eastChance + southChance;
  }

  public Direction getWeightedRandomDirection() {
    final int rand = RANDOM.nextInt(total());
    if (rand < goNorth()) {
      return Direction.NORTH;
    } else if (rand < goEast()) {
      return Direction.EAST;
    } else if (rand < goSouth()) {
      return Direction.SOUTH;
    } else {
      return Direction.WEST;
    }
  }
}
