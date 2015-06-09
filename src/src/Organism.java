package src;

import static core.Randomness.randomInt;

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

public class Organism {

  final Strain strain;
  private final Environment environment;
  private final int generation;
  private int childrenSpawned;

  private int updates, energy;

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

  // New virus with random attributes at a set location
  public Organism(Environment environment, Strain strain, Point location) {
    this(environment, strain, location, 0, //
        TraitLimit.MUTATION_CHANCE.randomValue(), //
        TraitLimit.MUTATION_DEGREE.randomValue(), //
        TraitLimit.MOVE_COST.randomValue(), //
        TraitLimit.REPRODUCTION_COST.randomValue(), //
        TraitLimit.REPRODUCTION_CHANCE.randomValue(), //
        TraitLimit.ENERGY_CAP.randomValue(), //
        ColorPreference.random(), //
        DirectionPreference.random());
  }

  // New Virus with non-random attributes
  private Organism(Environment environment, Strain strain, Point location, int generation,
      int mutationChance, int mutationDegree, int moveCost, int reproductionCost,
      int reproductionChance, int energyCap, ColorPreference colorPreference,
      DirectionPreference directionPreference) {
    this.environment = environment;
    this.strain = strain;
    this.location = location;
    this.generation = generation;
    this.strain.updateYoungest(generation);

    this.energyCap = energyCap;
    this.energy = energyCap / 2;

    this.moveCost = moveCost;
    this.reproductionCost = reproductionCost;
    this.reproductionChance = reproductionChance;
    this.mutationChance = mutationChance;
    this.mutationDegree = mutationDegree;

    this.colorPreference = colorPreference;
    this.directionPreference = directionPreference;
  }

  public void update() {
    strain.update(this);
  }

  public void check() {
    updates++;
    if (CauseOfDeath.isItDead(this)) {
      environment.prepareForDeath(this);
    }
  }

  public boolean forcedOutByTheYouth() {
    return environment.overPopulated(strain) && generation + 1 < strain.getYoungest();
  }

  public boolean outOfBounds() {
    return !environment.inBounds(location);
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
    energy /= 2;
    childrenSpawned++;
    environment.addKid(repr());
  }

  // Creates new Virus with mutated attributes
  private Organism repr() {
    return new Organism(environment, strain, findChildPoint(), generation + 1,
        mutateTrait(mutationChance), mutateTrait(mutationDegree), mutateTrait(moveCost),
        mutateTrait(reproductionCost), mutateTrait(reproductionChance), mutateTrait(energyCap),
        colorPreference.mutate(this), directionPreference.mutate(this));
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
        return !environment.orgAt(i, j);
      });
    });
  }

  private Map<Direction, Color> setView() {
    final Map<Direction, Color> view = new HashMap<>();
    for (final Direction dir : Direction.values()) {
      view.put(dir, environment.getColor(dir.translatedCopy(location)));
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
    energy += environment.takeRed(location, -randomInt(1, 20));
  }

  private void acquireGreen() {
    energy += environment.takeGreen(location, -randomInt(1, 20));
  }

  private void acquireBlue() {
    energy += environment.takeBlue(location, -randomInt(1, 20));
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
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Organism [strain=").append(strain.getStrainName());
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
