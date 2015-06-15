package core;

import src.Organism;

public class Battery {
  private int energy;
  private final int energyCap;

  private Battery(int energyCap) {
    this.energyCap = energyCap;
    this.energy = energyCap / 2; // Start half full.
  }

  public static Battery random() {
    return new Battery(TraitLimit.ENERGY_CAP.randomValue());
  }

  public Battery mutate(Organism organism) {
    return new Battery(organism.mutateTrait(energyCap));
  }

  public void addEnergy(int energy) {
    this.energy += energy;
  }

  public boolean hasAtLeast(int quantity) {
    return energy >= quantity;
  }

  public void halve() {
    this.energy /= 2;
  }

  public boolean overCap() {
    return energy >= energyCap * 1.5;
  }

  public boolean empty() {
    return energy <= 0;
  }
}
