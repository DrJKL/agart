package core;

public class Numbers {
  public static int randomInt(int low, int high) {
    return low + (int) (Math.random() * (high - low + 1));
  }

  public static int fix(int value) {
    return Math.max(value, 0);
  }
}
