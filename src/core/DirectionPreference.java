package core;

import src.Organism;

public class DirectionPreference {
  public final int northChance;
  public final int eastChance;
  public final int southChance;
  public final int westChance;

  public DirectionPreference(int northChance, int eastChance, int southChance, int westChance) {
    this.northChance = northChance;
    this.eastChance = eastChance;
    this.southChance = southChance;
    this.westChance = westChance;
  }

  public DirectionPreference mutate(Organism organism) {
    return new DirectionPreference(organism.mutateTrait(northChance),
        organism.mutateTrait(eastChance), organism.mutateTrait(southChance),
        organism.mutateTrait(westChance));
  }
}
