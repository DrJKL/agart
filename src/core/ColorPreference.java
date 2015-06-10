package core;

import static core.Numbers.fix;
import static core.Numbers.randomInt;
import src.Organism;

public class ColorPreference {
  public final int redChance;
  public final int greenChance;
  public final int blueChance;

  public ColorPreference(int redChance, int greenChance, int blueChance) {
    final boolean noPreference = fix(redChance) + fix(greenChance) + fix(blueChance) == 0;
    this.redChance = noPreference ? 1 : fix(redChance);
    this.greenChance = noPreference ? 1 : fix(greenChance);
    this.blueChance = noPreference ? 1 : fix(blueChance);
  }

  public static ColorPreference random() {
    return new ColorPreference(randomInt(1, 100), randomInt(1, 100), randomInt(1, 100));
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
