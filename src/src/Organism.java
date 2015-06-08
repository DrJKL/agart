package src;

import static core.Randomness.randomInt;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import com.google.common.collect.ComparisonChain;

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

  private int energy;

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

  public static int randomIntForTrait(TraitLimit trait) {
    return randomInt(trait.low, trait.high);
  }

  // New virus with random attributes at a set location
  public Organism(Environment env, Strain str, Point point) {
    location = point;

    envr = env;
    strain = str;
    orgName = str.getStrainName();
    causeOfDeath = CauseOfDeath.LIVING;
    generation = 0;
    strain.updateYoungest(0);

    energyCap = randomIntForTrait(TraitLimit.ENERGY_CAP);
    energy = energyCap / 2;

    moveCost = randomIntForTrait(TraitLimit.MOVE_COST);
    reproductionCost = randomIntForTrait(TraitLimit.REPRODUCTION_COST);
    reproductionChance = randomIntForTrait(TraitLimit.REPRODUCTION_CHANCE);
    mutationChance = randomIntForTrait(TraitLimit.MUTATION_CHANCE);
    mutationDegree = randomIntForTrait(TraitLimit.MUTATION_DEGREE);

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
    updates++;
    if (CauseOfDeath.isItDead(this)) {
      envr.prepareForDeath(this);
    }
  }

  public boolean forcedOutByTheYouth() {
    return envr.overPopulated(strain) && generation + 1 < strain.getYoungest();
  }

  public boolean outOfBounds() {
    return !envr.inBounds(location);
  }

  public boolean tooOld() {
    return updates > LIFESPAN;
  }

  public boolean outOfEnergy() {
    return energy <= 0;
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
    if (tooTired()) {
      return;
    }
    dir.tranlate(location);
    energy -= moveCost;
  }

  public void movePreferentially() {
    move(directionPreference.getWeightedRandomDirection());
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
        .max((a, b) -> Integer.compare(totalNutrition(a.getValue()), totalNutrition(b.getValue())))
        .get();
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
    energy += envr.takeRed(location, -randomInt(1, 20));
  }

  private void acquireGreen() {
    energy += envr.takeGreen(location, -randomInt(1, 20));
  }

  private void acquireBlue() {
    energy += envr.takeBlue(location, -randomInt(1, 20));
  }

  public boolean tooTired() {
    return energy < moveCost;
  }

  public Point getLocation() {
    return location;
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
    return ComparisonChain.start().compare(orgName.length(), org.orgName.length())
        .compare(orgName, org.orgName).result();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Organism [strain=").append(orgName);
    builder.append(", causeOfDeath=").append(causeOfDeath);
    builder.append(", generation=").append(generation);
    builder.append(", childrenSpawned=").append(childrenSpawned);
    builder.append(", updates=").append(updates);
    builder.append(", energy=").append(energy);
    builder.append(", mutation=").append(mutationChance);
    builder.append(", moveCost=").append(moveCost);
    builder.append(", reprCost=").append(reproductionCost);
    builder.append(", energyCap=").append(energyCap);
    builder.append(", location=").append(location);
    builder.append("]");
    return builder.toString();
  }

}
