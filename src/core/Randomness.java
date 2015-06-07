package core;

public class Randomness {
  public static int randomInt(int low, int high) {
    return low + (int) (Math.random() * (high - low + 1));
  }
}
