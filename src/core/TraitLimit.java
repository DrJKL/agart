package core;

public enum TraitLimit {
  MUTATION_CHANCE(0, 33),
  MUTATION_DEGREE(10, 15),
  MOVE_COST(20, 50),
  ENERGY_CAP(200, 500),

  ;

  public final int low;
  public final int high;

  private TraitLimit(int low, int high) {
    this.low = low;
    this.high = high;
  }

  public int randomValue() {
    return Numbers.randomInt(low, high);
  }
}
