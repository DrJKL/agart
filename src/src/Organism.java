package src;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import core.ColorPreference;
import core.Direction;
import core.DirectionPreference;

public class Organism implements Comparable<Organism> {

  public String orgName;
  public String causeOfDeath = "";
  Strain strain;
  private final Environment envr;
  private final int generation;
  private int childrenSpawned, movesMade;

  public int updates;

  private int resourcesGathered, energy, red, blue, green;

  private final int mutationChance, mutationDegree, moveCost, reproductionCost, reproductionChance,
      energyCap;

  private int y, x;

  public final ColorPreference colorPreference;
  public final DirectionPreference directionPreference;

  private static final String codDef = "Living";
  private static final String codStarved = "Starvation";
  private static final String codOldAge = "Old Age";
  private static final String codGericide = "Gericide";
  private static final String codHereBeDragons = "Dragons";

  private static final int MUTATION_CHANCE_LOW = 0;
  private static final int MUTATION_CHANCE_HIGH = 33;
  private static final int MUTATION_DEGREE_LOW = 2;
  private static final int MUTATION_DEGREE_HIGH = 8;
  private static final int MOVE_COST_LOW = 20;
  private static final int MOVE_COST_HIGH = 50;
  private static final int REPRODUCTION_COST_LOW = 20;
  private static final int REPRODUCTION_COST_HIGH = 50;
  private static final int REPRODUCTION_CHANCE_LOW = 40;
  private static final int REPRODUCTION_CHANCE_HIGH = 60;
  private static final int ENERGY_CAP_LOW = 200;
  private static final int ENERGY_CAP_HIGH = 500;

  private static final int maxKids = 3;
  private static final int breedCap = 80;

  public static final int movesEach = 20;
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
  public Organism(Environment env, Strain str, int y, int x) {
    this.y = y;
    this.x = x;

    envr = env;
    strain = str;
    orgName = str.getStrainName();
    causeOfDeath = codDef;
    generation = 0;
    strain.youngest(0);

    energyCap = randomInt(ENERGY_CAP_LOW, ENERGY_CAP_HIGH);
    energy = energyCap / 2;

    moveCost = randomInt(MOVE_COST_LOW, MOVE_COST_HIGH);
    reproductionCost = randomInt(REPRODUCTION_COST_LOW, REPRODUCTION_COST_HIGH);
    reproductionChance = randomInt(REPRODUCTION_CHANCE_LOW, REPRODUCTION_CHANCE_HIGH);
    mutationChance = randomInt(MUTATION_CHANCE_LOW, MUTATION_CHANCE_HIGH);
    mutationDegree = randomInt(MUTATION_DEGREE_LOW, MUTATION_DEGREE_HIGH);

    colorPreference = new ColorPreference(randomInt(1, 100), randomInt(1, 100), randomInt(1, 100));
    directionPreference = new DirectionPreference(randomInt(0, 3), randomInt(0, 3),
        randomInt(0, 3), randomInt(0, 3));

  }

  // New Virus with non-random attributes
  public Organism(Environment env, String str, Strain xStrain, int y, int x, int gen, int mut,
      int mutX, int met, int rpr, int rprX, int cap, ColorPreference colorPreference,
      DirectionPreference directionPreference) {
    orgName = str;
    strain = xStrain;
    causeOfDeath = codDef;
    envr = env;
    this.y = y;
    this.x = x;
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
    return y == 0 || x == 0 || x == envr.width - 1 || y == envr.height - 1;
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
    int newY = y;
    int newX = x;
    while (newY == y || newX == x || !envr.inBounds(newY, newX)) {
      newY = randomInt(y - 3, y + 3);
      newX = randomInt(x - 3, x + 3);
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

    final Organism spawn = new Organism(envr, str, strain, newY, newX, gen, mut, mutX, met, rpr,
        rprX, cap, newColorPreference, newDirectionPreference);

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
      if (y > 0) {
        y--;
        break;
      }
      return;
    case EAST:
      if (x < envr.width - 1) {
        x++;
        break;
      }
      return;
    case SOUTH:
      if (y < envr.height - 1) {
        y++;
        break;
      }
      return;
    case WEST:
      if (x > 0) {
        x--;
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
    return IntStream.rangeClosed(y - 3, y + 3).anyMatch(i -> {
      return IntStream.rangeClosed(x - 3, x + 3).anyMatch(j -> {
        return !envr.orgAt(i, j);
      });
    });
  }

  private Map<Direction, Color> setView() {
    final int newY = y;
    final int newX = x;
    if (newY < 0 || newY >= envr.height || newX < 0 || newX >= envr.width) {
      throw new RuntimeException();
    }
    final int North = newY - 1;
    final int East = newX + 1;
    final int South = newY + 1;
    final int West = newX - 1;
    final Map<Direction, Color> view = new HashMap<>();
    view.put(Direction.NORTH, (North >= 0) ? envr.getColor(newX, North) : Color.BLACK);
    view.put(Direction.EAST, (East < envr.width) ? envr.getColor(East, newY) : Color.BLACK);
    view.put(Direction.SOUTH, (South < envr.height) ? envr.getColor(newX, South) : Color.BLACK);
    view.put(Direction.WEST, (West >= 0) ? envr.getColor(West, newY) : Color.BLACK);
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

  private void acquireRand() {
    final int totX = colorPreference.total();
    if (totalNutrition(envr.getColor(x, y)) < lowestRGBPer) {
      return;
    }
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
    final int orig = envr.getRed(x, y);
    if (orig <= lowestRGBPer) {
      return;
    }
    final int take = randomInt(1, 20);
    red += take;
    envr.setRed(x, y, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  private void acquireGreen() {
    final int orig = envr.getGreen(x, y);
    if (orig <= lowestRGBPer) {
      return;
    }
    final int take = randomInt(1, 20);
    green += take;
    envr.setGreen(x, y, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  private void acquireBlue() {
    final int orig = envr.getBlue(x, y);
    if (orig <= lowestRGBPer) {
      return;
    }
    final int take = randomInt(1, 20);
    blue += take;
    envr.setBlue(x, y, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  public boolean tooTired() {
    return energy < moveCost;
  }

  public int getRow() {
    return y;
  }

  public int getCol() {
    return x;
  }

  private boolean canReplicate() {
    return energy >= energyCap - 20 //
        && generation < breedCap //
        && childrenSpawned < maxKids //
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
    builder.append(", y=").append(y);
    builder.append(", x=").append(x);
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
