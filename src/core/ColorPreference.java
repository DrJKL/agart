package core;

import static core.Numbers.fix;

import java.awt.Color;
import java.util.Random;

import src.Organism;

public class ColorPreference {

  private static final Random RANDOM = new Random();

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
    return new ColorPreference(RANDOM.nextInt(100), RANDOM.nextInt(100), RANDOM.nextInt(100));
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
    final int rand = RANDOM.nextInt(total());
    if (rand < inRed()) {
      return FoodColor.RED;
    } else if (rand < inGreen()) {
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
