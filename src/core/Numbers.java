package core;

import java.util.Random;

public class Numbers {

  static final Random RANDOM = new Random();

  public static int randomInt(int low, int high) {
    return high == low ? high : low + RANDOM.nextInt(high - low);
  }

  public static int fix(int value) {
    return Math.max(value, 0);
  }
}
