package core;

import static core.Numbers.fix;
import static core.Numbers.randomInt;

import java.awt.Color;

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

  public FoodColor getWeightedRandomColor() {
    final int rand = randomInt(1, total());
    if (rand <= inRed()) {
      return FoodColor.RED;
    } else if (rand <= inGreen()) {
      return FoodColor.GREEN;
    } else {
      return FoodColor.BLUE;
    }
  }

  public int weightedValue(Color color) {
    return (redChance * color.getRed()) + (greenChance * color.getGreen())
        + (blueChance * color.getBlue());
  }
}
