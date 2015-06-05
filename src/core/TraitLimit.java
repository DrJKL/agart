package core;

public enum TraitLimit {
  MUTATION_CHANCE(0, 33);

  public final int low;
  public final int high;

  private TraitLimit(int low, int high) {
    this.low = low;
    this.high = high;
  }
}
