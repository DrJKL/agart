package core;

import src.Organism;

public class ColorPreference {
  public final int redChance;
  public final int greenChance;
  public final int blueChance;

  public ColorPreference(int redChance, int greenChance, int blueChance) {
    this.redChance = redChance;
    this.greenChance = greenChance;
    this.blueChance = blueChance;
  }

  public ColorPreference mutate(Organism organism) {
    return new ColorPreference(organism.mutateTrait(redChance), organism.mutateTrait(greenChance),
        organism.mutateTrait(blueChance));
  }

  public int total() {
    return redChance + greenChance + blueChance;
  }

  public int inRed() {
    return redChance;
  }

  public int inGreen() {
    return redChance + greenChance;
  }
}
