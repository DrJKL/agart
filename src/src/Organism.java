package src;

import java.awt.Color;
import java.awt.Point;
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

  private final String orgName;
  private CauseOfDeath causeOfDeath = CauseOfDeath.LIVING;
  Strain strain;
  private final Environment envr;
  private final int generation;
  private int childrenSpawned;

  private int updates;

  private int resourcesGathered, energy, red, blue, green;

  private final int mutationChance, reproductionChance;
  private final int mutationDegree, reproductionCost;
  private final int moveCost, energyCap;

  private final Point location;

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

  // New virus with random attributes at a set location
  public Organism(Environment env, Strain str, int r, int c) {
    location = new Point(c, r);

    envr = env;
    strain = str;
    orgName = str.getStrainName();
    causeOfDeath = CauseOfDeath.LIVING;
    generation = 0;
    strain.updateYoungest(0);

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
  private Organism(Environment env, String str, Strain xStrain, Point loc, int gen, int mut,
      int mutX, int met, int rpr, int rprX, int cap, ColorPreference colorPreference,
      DirectionPreference directionPreference) {
    orgName = str;
    strain = xStrain;
    causeOfDeath = CauseOfDeath.LIVING;
    envr = env;
    location = loc;
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
    if (!envr.inBounds(location)) {
      causeOfDeath = CauseOfDeath.DRAGONS;
    }
    if (!causeOfDeath.equals(CauseOfDeath.LIVING)) {
      envr.prepareForDeath(this);
    }
  }

  public void replicate() {
    if (!hasSpace() || !canReplicate()) {
      return;
    }
    Organism child;
    child = repr();
    energy /= 2;
    childrenSpawned++;
    envr.addKid(child);
    strain.updateYoungest(generation + 1);
  }

  // Creates new Virus with mutated attributes
  private Organism repr() {
    final Point childPoint = findChildPoint();
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

    final Organism spawn = new Organism(envr, str, strain, childPoint, gen, mut, mutX, met, rpr,
        rprX, cap, newColorPreference, newDirectionPreference);

    return spawn;
  }

  // I'm pretty sure this is bugged.
  private Point findChildPoint() {
    int r = location.y;
    int c = location.x;
    while (r == location.y && c == location.x) {
      r = randomInt(location.y - 3, location.y + 3);
      c = randomInt(location.x - 3, location.x + 3);
    }
    return new Point(c, r);
  }

  public int mutateTrait(int original) {
    return shouldMutate() ? Math
        .abs(randomInt(original - mutationDegree, original + mutationDegree)) : original;
  }

  private boolean shouldMutate() {
    return Math.random() * 100 < mutationChance;
  }

  public void move() {
    move(Direction.random());
  }

  /** dir >= 0 and dir <4 */
  public void move(Direction dir) {
    dir.tranlate(location);
    energy -= moveCost;
  }

  public void movePreferentially() {
    final int totX = directionPreference.total();
    if (totX < 1) {
      return;
    }
    final int rand = randomInt(1, totX);
    if (rand <= directionPreference.goNorth()) {
      move(Direction.NORTH);
    } else if (rand <= directionPreference.goEast()) {
      move(Direction.EAST);
    } else if (rand <= directionPreference.goSouth()) {
      move(Direction.SOUTH);
    } else {
      move(Direction.WEST);
    }
  }

  private boolean hasSpace() {
    return IntStream.rangeClosed(location.y - 3, location.y + 3).anyMatch(i -> {
      return IntStream.rangeClosed(location.x - 3, location.x + 3).anyMatch(j -> {
        return !envr.orgAt(i, j);
      });
    });
  }

  private Map<Direction, Color> setView() {
    final Map<Direction, Color> view = new HashMap<>();
    for (final Direction dir : Direction.values()) {
      view.put(dir, envr.getColor(dir.translatedCopy(location)));
    }
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
    final int orig = envr.getColor(location).getRed();
    if (orig <= MINIMUM_COLOR_VALUE) {
      return;
    }
    final int take = randomInt(1, 20);
    red += take;
    envr.setRed(location, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  private void acquireGreen() {
    final int orig = envr.getColor(location).getGreen();
    if (orig <= MINIMUM_COLOR_VALUE) {
      return;
    }
    final int take = randomInt(1, 20);
    green += take;
    envr.setGreen(location, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  private void acquireBlue() {
    final int orig = envr.getColor(location).getBlue();
    if (orig <= MINIMUM_COLOR_VALUE) {
      return;
    }
    final int take = randomInt(1, 20);
    blue += take;
    envr.setBlue(location, orig - take);
    resourcesGathered += take;
    energy += take;
  }

  public boolean tooTired() {
    return energy < moveCost;
  }

  public int getRow() {
    return location.y;
  }

  public int getCol() {
    return location.x;
  }

  private boolean canReplicate() {
    return energy >= energyCap - 20 //
        && energy >= reproductionCost //
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
    builder.append(", location=").append(location);
    builder.append("]");
    return builder.toString();
  }

}
