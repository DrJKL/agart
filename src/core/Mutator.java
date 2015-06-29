package core;

import java.util.Random;

public class Mutator {
  private static final Random RANDOM = new Random();
  private final int mutationChance, mutationDegree;

  private Mutator(int mutationChance, int mutationDegree) {
    this.mutationChance = mutationChance;
    this.mutationDegree = mutationDegree;
  }

  public static Mutator random() {
    return new Mutator(TraitLimit.MUTATION_CHANCE.randomValue(),
        TraitLimit.MUTATION_DEGREE.randomValue());
  }

  public Mutator mutate() {
    return new Mutator(mutateTrait(mutationChance), mutateTrait(mutationDegree));
  }

  public int mutateTrait(int original) {
    return Math.abs(original + changeDegree());
  }

  public int changeDegree() {
    return shouldMutate() ? mutationDegree - RANDOM.nextInt(1 + (2 * mutationDegree)) : 0;
  }

  private boolean shouldMutate() {
    return mutationDegree > 0 && Math.random() * 100 < mutationChance;
  }

}
