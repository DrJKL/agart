package src;

import static core.Numbers.randomInt;
import static java.util.Map.Entry.comparingByValue;

import java.awt.Point;
import java.util.Map;
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

  final Strain strain;
  private final Environment environment;
  private final int generation;
  private int childrenSpawned;

  private final Battery battery;

  private int updates;
  private final int reproductionChance;
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
    this(environment, strain, location, 0, //
        TraitLimit.MOVE_COST.randomValue(), //
        TraitLimit.REPRODUCTION_CHANCE.randomValue(), //
        Battery.random(), //
        Mutator.random(), //
        ColorPreference.random(), //
        DirectionPreference.random());
  }

  // New Virus with non-random attributes
  private Organism(Environment environment, Strain strain, Point location, int generation,
      int moveCost, int reproductionChance, Battery battery, Mutator mutator,
      ColorPreference colorPreference, DirectionPreference directionPreference) {
    this.environment = environment;
    this.strain = strain;
    this.location = location;
    this.generation = generation;
    this.strain.updateYoungest(generation);

    this.battery = battery;

    this.moveCost = moveCost;
    this.reproductionChance = reproductionChance;

    this.mutator = mutator;
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
    return battery.empty();
  }

  private boolean canReplicate() {
    return battery.overCap()//
        && generation < BREED_CAP //
        && childrenSpawned < MAX_KIDS //
        && Math.random() * 100 < reproductionChance;
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
        generation + 1, mutateTrait(moveCost), mutateTrait(reproductionChance),
        battery.mutate(this), mutator.mutate(), colorPreference.mutate(this),
        directionPreference.mutate(this));
  }

  public int mutateTrait(int original) {
    return mutator.mutateTrait(original);
  }

  public int timesToMove() {
    return randomInt(0, MOVES_EACH);
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
    return Direction
        .shuffled()
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
    battery.addEnergy(environment.takeColor(location, -randomInt(1, 20),
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
