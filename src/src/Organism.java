package src;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import core.CauseOfDeath;
import core.ColorPreference;
import core.Direction;
import core.DirectionPreference;
import core.TraitLimit;

public class Organism implements Comparable<Organism> {

  private String orgName;
  private CauseOfDeath causeOfDeath = CauseOfDeath.LIVING;
  Strain strain;
  private final Environment envr;
  private final int generation;
  private int childrenSpawned, movesMade;

  private int updates;

  private int resourcesGathered, energy, red, blue, green;

  private final int mutationChance, mutationDegree, moveCost, reproductionCost, reproductionChance,
      energyCap;

  private int row, col;

  private final ColorPreference colorPreference;
  private final DirectionPreference directionPreference;

  private static final int MAX_KIDS = 3;
  private static final int BREED_CAP = 80;

  private static final int MOVES_EACH = 20;
  private static final int LIFESPAN = 40;
  private static final int MAX_POPULATION = 100;

  private static final int MINIMUM_COLOR_VALUE = 30;

  public static int randomInt(int low, int high) {
    return low + (int) (Math.random() * (high - low + 1));
  }

  public static int randomInt(TraitLimit trait) {
    return randomInt(trait.low, trait.high);
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
    causeOfDeath = CauseOfDeath.LIVING;
    generation = 0;
    strain.youngest(0);

    energyCap = randomInt(TraitLimit.ENERGY_CAP);
    energy = energyCap / 2;

    moveCost = randomInt(TraitLimit.MOVE_COST);
    reproductionCost = randomInt(TraitLimit.REPRODUCTION_COST);
    reproductionChance = randomInt(TraitLimit.REPRODUCTION_CHANCE);
    mutationChance = randomInt(TraitLimit.MUTATION_CHANCE);
    mutationDegree = randomInt(TraitLimit.MUTATION_DEGREE);

    colorPreference = new ColorPreference(randomInt(1, 100), randomInt(1, 100), randomInt(1, 100));
    directionPreference = new DirectionPreference(randomInt(0, 3), randomInt(0, 3),
        randomInt(0, 3), randomInt(0, 3));

  }

  // New Virus with non-random attributes
  public Organism(Environment env, String str, Strain xStrain, int r, int c, int gen, int mut,
      int mutX, int met, int rpr, int rprX, int cap, ColorPreference colorPreference,
      DirectionPreference directionPreference) {
    orgName = str;
    strain = xStrain;
    causeOfDeath = CauseOfDeath.LIVING;
    envr = env;
    row = r;
    col = c;
    generation = gen;

    energy = cap / 2;
    energyCap = cap;

    mutationChance = mut;
    mutationDegree = mutX;

    moveCost = met;

    reproductionCost = rpr;
    reproductionChance = rprX;

    this.colorPreference = colorPreference;
    this.directionPreference = directionPreference;
  }

  public void update() {
    strain.update(this);
  }

  public void check() {
    if (energy <= 0) {
      causeOfDeath = CauseOfDeath.STARVED;
    }
    if (++updates > LIFESPAN) {
      causeOfDeath = CauseOfDeath.OLD_AGE;
    }
    if (envr.getActiveStrainSize(strain) > MAX_POPULATION
        && generation + 1 < this.strain.getYoungest()) {
      causeOfDeath = CauseOfDeath.GERICIDE;
    }
    if (onEdge()) {
      causeOfDeath = CauseOfDeath.DRAGONS;
    }
    if (!causeOfDeath.equals(CauseOfDeath.LIVING)) {
      passOn();
    }
  }

  private boolean onEdge() {
    return row == 0 || col == 0 || col == envr.width - 1 || row == envr.height - 1;
  }

  private void passOn() {
    envr.graveyard.add(this);
  }

  public void replicate() {
    if (energy < reproductionCost || !hasSpace() || !canReplicate()) {
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
  private Organism repr() {
    int r = row;
    int c = col;
    while (r == row || c == col || !envr.inBounds(r, c)) {
      r = randomInt(row - 3, row + 3);
      c = randomInt(col - 3, col + 3);
    }
    final int gen = generation + 1;
    final String str = orgName + childrenSpawned;
    int mut, mutX, met, rpr, rprX, cap;

    mut = mutateTrait(mutationChance);
    mutX = mutateTrait(mutationDegree);

    met = mutateTrait(moveCost);

    rpr = mutateTrait(reproductionCost);
    rprX = mutateTrait(reproductionChance);

    cap = mutateTrait(energyCap);

    final ColorPreference newColorPreference = colorPreference.mutate(this);
    final DirectionPreference newDirectionPreference = directionPreference.mutate(this);

    final Organism spawn = new Organism(envr, str, strain, r, c, gen, mut, mutX, met, rpr, rprX,
        cap, newColorPreference, newDirectionPreference);

    return spawn;
  }

  public int mutateTrait(int original) {
    return shouldMutate() ? Math
        .abs(randomInt(original - mutationDegree, original + mutationDegree)) : original;
  }

  private boolean shouldMutate() {
    return Math.random() * 100 > mutationChance;
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

  public void movePreferentially() {
    final int nX = directionPreference.northChance;
    final int eX = directionPreference.eastChance;
    final int sX = directionPreference.southChance;
    final int wX = directionPreference.westChance;
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

  private final int newThing = 0;

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

  public int timesToMove() {
    return randomInt(0, MOVES_EACH);
  }

  private void acquireRand() {
    final int totX = colorPreference.total();
    if (totX < 1) {
      return;
    }
    final int rand = randomInt(1, totX);
    if (rand <= colorPreference.inRed()) {
      acquireRed();
    } else if (rand <= colorPreference.inGreen()) {
      acquireGreen();
    } else {
      acquireBlue();
    }
  }

  private void acquireRed() {
    final int orig = envr.getColor(col, row).getRed();
    if (orig <= MINIMUM_COLOR_VALUE) {
      return;
    }
    final int take = randomInt(1, 20);
    red += take;
    envr.setRed(col, row, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  private void acquireGreen() {
    final int orig = envr.getColor(col, row).getGreen();
    if (orig <= MINIMUM_COLOR_VALUE) {
      return;
    }
    final int take = randomInt(1, 20);
    green += take;
    envr.setGreen(col, row, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  private void acquireBlue() {
    final int orig = envr.getColor(col, row).getBlue();
    if (orig <= MINIMUM_COLOR_VALUE) {
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

  private boolean canReplicate() {
    return energy >= energyCap - 20 //
        && generation < BREED_CAP //
        && childrenSpawned < MAX_KIDS //
        && Math.random() * 100 < reproductionChance;
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
    builder.append(", mutation=").append(mutationChance);
    builder.append(", moveCost=").append(moveCost);
    builder.append(", reprCost=").append(reproductionCost);
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

}
