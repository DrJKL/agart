package src;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import core.ColorPreference;
import core.Direction;

public class Organism implements Comparable<Organism> {

  public String orgName;
  public String causeOfDeath = "";
  Strain strain;
  private final Environment envr;
  private final int generation;
  private int childrenSpawned, movesMade;

  public int updates;

  private int resourcesGathered, energy, red, blue, green;

  private final int mutation, mutationX, moveCost, reprCost, reprX, energyCap;

  private int row, col;

  public final ColorPreference colorPreference;
  public int northX, eastX, southX, westX;

  private static final String codDef = "Living";
  private static final String codStarved = "Starvation";
  private static final String codOldAge = "Old Age";
  private static final String codGericide = "Gericide";
  private static final String codHereBeDragons = "Dragons";

  private static final int mutLow = 0;
  private static final int mutHigh = 33;
  private static final int mutXLow = 2;
  private static final int mutXHigh = 8;
  private static final int moveLow = 20;
  private static final int moveHigh = 50;
  private static final int rprLow = 20;
  private static final int rprHigh = 50;
  private static final int rprXLow = 40;
  private static final int rprXHigh = 60;
  private static final int capLow = 200;
  private static final int capHigh = 500;

  private static final int maxKids = 3;
  private static final int breedCap = 80;

  private static final int movesEach = 20;
  private static final int lifeSpan = 40;
  private static final int popCap = 100;

  private static final int lowestRGBPer = 30;

  public static int randomInt(int low, int high) {
    return low + (int) (Math.random() * (high - low + 1));
  }

  // New organism with random attributes at random location
  public Organism(Environment env, Strain str) {
    this(env, str, randomInt(0, env.height), randomInt(0, env.width));
  }

  // New virus with random attributes at a set location
  public Organism(Environment env, Strain str, int r, int c) {
    row = r;
    col = c;

    envr = env;
    strain = str;
    orgName = str.getStrainName();
    causeOfDeath = codDef;
    generation = 0;
    strain.youngest(0);

    energyCap = randomInt(capLow, capHigh);
    energy = energyCap / 2;

    moveCost = randomInt(moveLow, moveHigh);
    reprCost = randomInt(rprLow, rprHigh);
    reprX = randomInt(rprXLow, rprXHigh);
    mutation = randomInt(mutLow, mutHigh);
    mutationX = randomInt(mutXLow, mutXHigh);

    colorPreference = new ColorPreference(randomInt(1, 100), randomInt(1, 100), randomInt(1, 100));

  }

  // New Virus with non-random attributes
  public Organism(Environment env, String str, Strain xStrain, int r, int c, int gen, int mut,
      int mutX, int met, int rpr, int rprX, int cap, ColorPreference colorPreference, int nX,
      int eX, int sX, int wX) {
    orgName = str;
    strain = xStrain;
    causeOfDeath = codDef;
    envr = env;
    row = r;
    col = c;
    generation = gen;

    energy = cap / 2;
    energyCap = cap;

    mutation = mut;
    mutationX = mutX;

    moveCost = met;

    reprCost = rpr;
    reprX = rprX;

    this.colorPreference = colorPreference;

    northX = nX;
    eastX = eX;
    southX = sX;
    westX = wX;
  }

  public void update() {
    strain.update(this);
  }

  public void check() {
    if (energy <= 0) {
      causeOfDeath = codStarved;
    }
    if (updates > lifeSpan) {
      causeOfDeath = codOldAge;
    }
    if (envr.getActiveStrainSize(strain) > popCap && generation + 1 < this.strain.getYoungest()) {
      causeOfDeath = codGericide;
    }
    if (onEdge()) {
      causeOfDeath = codHereBeDragons;
    }
    if (!causeOfDeath.equals("Living")) {
      passOn();
    }
  }

  private boolean onEdge() {
    return row == 0 || col == 0 || col == envr.width - 1 || row == envr.height - 1;
  }

  public void passOn() {
    envr.graveyard.add(this);
  }

  public void replicate() {
    if (energy < reprCost || !hasSpace() || !canReplicate()) {
      return;
    }
    Organism child;
    child = repr();
    energy /= 2;
    childrenSpawned++;
    envr.addKid(child);
    strain.youngest(generation + 1);
  }

  // Creates new Virus with mutated attributes
  public Organism repr() {
    int r = row;
    int c = col;
    while (r == row || c == col || !envr.inBounds(r, c)) {
      r = randomInt(row - 3, row + 3);
      c = randomInt(col - 3, col + 3);
    }
    final int gen = generation + 1;
    final String str = orgName + childrenSpawned;
    int mut, mutX, met, rpr, rprX, cap;

    int nX, eX, sX, wX;

    mut = mutateTrait(mutation);
    mutX = mutateTrait(mutationX);

    met = mutateTrait(moveCost);

    rpr = mutateTrait(reprCost);
    rprX = mutateTrait(reprX);

    cap = mutateTrait(energyCap);

    final ColorPreference newColorPreference = colorPreference.mutate(this);

    nX = mutateTrait(northX);
    eX = mutateTrait(eastX);
    sX = mutateTrait(southX);
    wX = mutateTrait(westX);

    final Organism spawn = new Organism(envr, str, strain, r, c, gen, mut, mutX, met, rpr, rprX,
        cap, newColorPreference, nX, eX, sX, wX);

    return spawn;
  }

  public int mutateTrait(int original) {
    return shouldMutate() ? Math.abs(randomInt(original - mutationX, original + mutationX))
        : original;
  }

  private boolean shouldMutate() {
    return Math.random() * 100 > mutation;
  }

  public void move() {
    move(Direction.random());
  }

  /** dir >= 0 and dir <4 */
  public void move(Direction dir) {
    switch (dir) {
    case NORTH:
      if (row > 0) {
        row--;
        break;
      }
      return;
    case EAST:
      if (col < envr.width - 1) {
        col++;
        break;
      }
      return;
    case SOUTH:
      if (row < envr.height - 1) {
        row++;
        break;
      }
      return;
    case WEST:
      if (col > 0) {
        col--;
        break;
      }
      return;
    }

    energy -= moveCost;
    movesMade++;
  }

  public void move(int nX, int eX, int sX, int wX) {
    final int totX = nX + eX + sX + wX;
    if (totX < 1) {
      return;
    }
    final int rand = randomInt(1, totX);
    if (rand <= nX) {
      move(Direction.NORTH);
    } else if (rand <= (nX + eX)) {
      move(Direction.EAST);
    } else if (rand <= (nX + eX + sX)) {
      move(Direction.SOUTH);
    } else {
      move(Direction.WEST);
    }
  }

  private boolean hasSpace() {
    return IntStream.rangeClosed(row - 3, row + 3).anyMatch(i -> {
      return IntStream.rangeClosed(col - 3, col + 3).anyMatch(j -> {
        return !envr.orgAt(i, j);
      });
    });
  }

  private Map<Direction, Color> setView() {
    final int r = row;
    final int c = col;
    if (r < 0 || r >= envr.height || c < 0 || c >= envr.width) {
      throw new RuntimeException();
    }
    final int North = r - 1;
    final int East = c + 1;
    final int South = r + 1;
    final int West = c - 1;
    final Map<Direction, Color> view = new HashMap<>();
    view.put(Direction.NORTH, (North >= 0) ? envr.getColor(c, North) : Color.BLACK);
    view.put(Direction.EAST, (East < envr.width) ? envr.getColor(East, r) : Color.BLACK);
    view.put(Direction.SOUTH, (South < envr.height) ? envr.getColor(c, South) : Color.BLACK);
    view.put(Direction.WEST, (West >= 0) ? envr.getColor(West, r) : Color.BLACK);
    return view;
  }

  private static Integer totalNutrition(Color color) {
    return color.getBlue() + color.getRed() + color.getGreen();
  }

  public Direction viewMaxAll() {
    final Map<Direction, Color> view = setView();
    final Entry<Direction, Color> max = view.entrySet().stream()
        .max((a, b) -> totalNutrition(a.getValue()).compareTo(totalNutrition(b.getValue()))).get();
    return max.getKey();
  }

  public void feast() {
    IntStream.range(0, 15).forEach(j -> acquireRand());
  }

  public void acquireRand() {
    final int rX = colorPreference.redChance;
    final int gX = colorPreference.greenChance;
    final int bX = colorPreference.blueChance;
    final int totX = rX + gX + bX;
    if (envr.getRed(col, row) + envr.getBlue(col, row) + envr.getGreen(col, row) < lowestRGBPer) {
      return;
    }
    if (totX < 1) {
      return;
    }
    final int rand = randomInt(1, totX);
    if (rand <= rX) {
      acquireRed();
    } else if (rand <= (rX + gX)) {
      acquireGreen();
    } else if (rand <= totX) {
      acquireBlue();
    }
  }

  public void acquireRed() {
    final int orig = envr.getRed(col, row);
    if (orig <= lowestRGBPer) {
      return;
    }
    final int take = randomInt(1, 20);
    red += take;
    envr.setRed(col, row, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  public void acquireGreen() {
    final int orig = envr.getGreen(col, row);
    if (orig <= lowestRGBPer) {
      return;
    }
    final int take = randomInt(1, 20);
    green += take;
    envr.setGreen(col, row, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  public void acquireBlue() {
    final int orig = envr.getBlue(col, row);
    if (orig <= lowestRGBPer) {
      return;
    }
    final int take = randomInt(1, 20);
    blue += take;
    envr.setBlue(col, row, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  public boolean tooTired() {
    return energy < moveCost;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  public boolean canReplicate() {
    return energy >= energyCap - 20 //
        && generation < breedCap //
        && childrenSpawned < maxKids //
        && Math.random() * 100 < reprX;
  }

  @Override
  public int compareTo(Organism org) {
    if (orgName.length() == org.orgName.length()) {
      return orgName.compareTo(org.orgName);
    }
    return orgName.length() - org.orgName.length();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Organism [strain=").append(orgName);
    builder.append(", causeOfDeath=").append(causeOfDeath);
    builder.append(", generation=").append(generation);
    builder.append(", childrenSpawned=").append(childrenSpawned);
    builder.append(", movesMade=").append(movesMade);
    builder.append(", updates=").append(updates);
    builder.append(", resourcesGathered=").append(resourcesGathered);
    builder.append(", energy=").append(energy);
    builder.append(", red=").append(red);
    builder.append(", blue=").append(blue);
    builder.append(", green=").append(green);
    builder.append(", mutation=").append(mutation);
    builder.append(", moveCost=").append(moveCost);
    builder.append(", reprCost=").append(reprCost);
    builder.append(", energyCap=").append(energyCap);
    builder.append(", row=").append(row);
    builder.append(", col=").append(col);
    builder.append("]");
    return builder.toString();
  }

  public void setStrain(Strain strainSet) {
    strain = strainSet;
    final String newName = strainSet.getStrainName()
        + orgName.substring(strain.getStrainName().length() - 1);
    orgName = newName;
  }

  public int getMovesEach() {
    return movesEach;
  }

}
