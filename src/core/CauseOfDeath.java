package core;

import java.util.Arrays;

import src.Organism;

public enum CauseOfDeath {
  LIVING("Living") {
    @Override
    public boolean rollTheDice(Organism organism) {
      return false;
    }
  },
  STARVED("Starvation") {
    @Override
    public boolean rollTheDice(Organism organism) {
      return organism.outOfEnergy();
    }
  },
  OLD_AGE("Old Age") {
    @Override
    public boolean rollTheDice(Organism organism) {
      return organism.tooOld();
    }
  },
  GERICIDE("Gericide") {
    @Override
    public boolean rollTheDice(Organism organism) {
      return organism.forcedOutByTheYouth();
    }
  },
  DRAGONS("Dragons") {
    @Override
    public boolean rollTheDice(Organism organism) {
      return organism.outOfBounds();
    }
  };

  public final String description;

  private CauseOfDeath(String description) {
    this.description = description;
  }

  public abstract boolean rollTheDice(Organism organism);

  public static boolean isItDead(Organism organism) {
    return Arrays.stream(values()).anyMatch(cod -> cod.rollTheDice(organism));
  }
}
