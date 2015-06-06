package core;

import src.Organism;

public class DirectionPreference {
  public final int northChance;
  public final int eastChance;
  public final int southChance;
  public final int westChance;

  public DirectionPreference(int northChance, int eastChance, int southChance, int westChance) {
    this.northChance = fix(northChance);
    this.eastChance = fix(eastChance);
    this.southChance = fix(southChance);
    this.westChance = fix(westChance);
  }

  public DirectionPreference mutate(Organism organism) {
    return new DirectionPreference(organism.mutateTrait(northChance),
        organism.mutateTrait(eastChance), organism.mutateTrait(southChance),
        organism.mutateTrait(westChance));
  }

  public int total() {
    return northChance + eastChance + southChance + westChance;
  }

  public int goNorth() {
    return northChance;
  }

  public int goEast() {
    return northChance + eastChance;
  }

  public int goSouth() {
    return northChance + eastChance + southChance;
  }

  private static int fix(int value) {
    return Math.max(value, 0);
  }

}
