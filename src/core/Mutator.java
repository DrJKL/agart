package core;

import static core.Numbers.randomInt;

public class Mutator {
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
    return shouldMutate() ? Math
        .abs(randomInt(original - mutationDegree, original + mutationDegree)) : original;
  }

  private boolean shouldMutate() {
    return Math.random() * 100 < mutationChance;
  }

}
