package core;

public enum CauseOfDeath {
  LIVING("Living"), STARVED("Starvation"), OLD_AGE("Old Age"), GERICIDE("Gericide"), DRAGONS(
      "Dragons");

  public final String description;

  private CauseOfDeath(String description) {
    this.description = description;
  }

  public static final String codDef = "Living";
  public static final String codStarved = "Starvation";
  public static final String codOldAge = "Old Age";
  public static final String codGericide = "Gericide";
  public static final String codHereBeDragons = "Dragons";
}
