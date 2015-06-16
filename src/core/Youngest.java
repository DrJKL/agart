package core;

import java.util.HashMap;
import java.util.Map;

import src.Strain;

public class Youngest {
  private static final Map<Strain, Integer> youngestInStrain = new HashMap<>();

  public static void resetYoungest(Strain strain) {
    youngestInStrain.remove(strain);
  }

  public static void updateYoungest(Strain strain, int orgGen) {
    youngestInStrain.put(strain, Math.max(getYoungest(strain), orgGen));
  }

  public static int getYoungest(Strain strain) {
    return youngestInStrain.getOrDefault(strain, 0);
  }
}
