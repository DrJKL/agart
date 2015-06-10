package core;

import src.Organism;

public class Battery {
  private int energy;
  private final int replicationEnergyCap;

  private Battery(int replicationEnergyCap) {
    this.replicationEnergyCap = replicationEnergyCap;
    this.energy = replicationEnergyCap / 2; // Start half full.
  }

  public static Battery random() {
    return new Battery(TraitLimit.ENERGY_CAP.randomValue());
  }

  public Battery mutate(Organism organism) {
    return new Battery(organism.mutateTrait(replicationEnergyCap));
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
    return energy >= replicationEnergyCap;
  }

  public boolean empty() {
    return energy <= 0;
  }
}
