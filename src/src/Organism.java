package src;

import static java.util.Map.Entry.comparingByValue;

import java.awt.Point;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import core.Battery;
import core.CauseOfDeath;
import core.ColorPreference;
import core.Direction;
import core.DirectionPreference;
import core.Mutator;
import core.TraitLimit;

public class Organism {

  private static final Random RANDOM = new Random();
  final Strain strain;
  private final Environment environment;
  private final int generation;
  private int childrenSpawned;

  private final Battery battery;

  private int updates;
  private final int moveCost;

  private final Point location;

  private final Mutator mutator;
  private final ColorPreference colorPreference;
  private final DirectionPreference directionPreference;

  private static final int MAX_KIDS = 3;
  private static final int BREED_CAP = 80;

  private static final int MOVES_EACH = 20;
  private static final int LIFESPAN = 40;

  // New virus with random attributes at a set location
  public Organism(Environment environment, Strain strain, Point location) {
    this(environment, strain, location, Battery.random(), //
        Mutator.random(), //
        ColorPreference.random(), //
        DirectionPreference.random(), //
        TraitLimit.MOVE_COST.randomValue(), //
        0); // Generation
  }

  // New Virus with non-random attributes
  private Organism(Environment environment, Strain strain, Point location, Battery battery,
      Mutator mutator, ColorPreference colorPreference, DirectionPreference directionPreference,
      int moveCost, int generation) {
    this.environment = environment;
    this.strain = strain;
    this.location = location;

    this.battery = battery;

    this.mutator = mutator;
    this.colorPreference = colorPreference;
    this.directionPreference = directionPreference;

    this.moveCost = moveCost;
    this.generation = generation;
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

  public boolean sacrificedForYourChildren() {
    return environment.overPopulated(strain) && //
        childrenSpawned > 0;
  }

  public boolean outOfBounds() {
    return !environment.inBounds(location);
  }

  public boolean tooOld() {
    return updates > LIFESPAN;
  }

  public boolean outOfEnergy() {
    return battery.empty();
  }

  private boolean canReplicate() {
    return battery.overCap()//
        && generation < BREED_CAP //
        && childrenSpawned < MAX_KIDS //
        && Math.random() < 0.5;
  }

  public void replicate() {
    if (!canReplicate()) {
      return;
    }
    battery.halve();
    childrenSpawned++;
    environment.addKid(generateOffspring());
  }

  // Creates new Virus with mutated attributes
  private Organism generateOffspring() {
    return new Organism(environment, strain, Direction.random().translatedCopy(location),
        battery.mutate(this), mutator.mutate(), colorPreference.mutate(this),
        directionPreference.mutate(this), mutateTrait(moveCost), generation + 1);
  }

  public int mutateTrait(int original) {
    return mutator.mutateTrait(original);
  }

  public int timesToMove() {
    return RANDOM.nextInt(MOVES_EACH);
  }

  public void move() {
    move(Direction.random());
  }

  public void move(Direction dir) {
    if (!battery.hasAtLeast(moveCost)) {
      return;
    }
    dir.tranlate(location);
    battery.addEnergy(-moveCost);
  }

  public void movePreferentially() {
    move(directionPreference.getWeightedRandomDirection());
  }

  private Map<Direction, Integer> setView() {
    return Arrays
        .asList(Direction.values())
        .stream()
        .collect(
            Collectors.toMap(Function.identity(), d -> colorPreference.weightedValue(environment
                .getColor(d.translatedCopy(location)))));
  }

  public Direction viewMaxAll() {
    return setView().entrySet().stream().unordered().max(comparingByValue(Integer::compareTo))
        .get().getKey();
  }

  public void feast() {
    IntStream.range(0, 15).forEach(j -> acquireRand());
  }

  public void acquireRand() {
    if (battery.fullToBursting()) {
      return;
    }
    battery.addEnergy(environment.takeColor(location, -RANDOM.nextInt(20),
        colorPreference.getWeightedRandomColor()));
  }

  public Point getLocation() {
    return location;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Organism [strain=").append(strain.getStrainName());
    builder.append(", generation=").append(generation);
    builder.append(", childrenSpawned=").append(childrenSpawned);
    builder.append(", updates=").append(updates);
    builder.append(", energy=").append(battery);
    builder.append(", mutation=").append(mutator);
    builder.append(", moveCost=").append(moveCost);
    builder.append(", location=").append(location);
    builder.append("]");
    return builder.toString();
  }
}
