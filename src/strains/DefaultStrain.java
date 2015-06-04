package strains;

import java.util.stream.IntStream;

import src.Organism;
import src.Strain;

public class DefaultStrain implements Strain {

  final String strainName;
  int youngest = -1;

  public DefaultStrain(String str) {
    strainName = str;
  }

  public DefaultStrain() {
    strainName = "Default VIRUS ";
  }

  @Override
  public String getStrainName() {
    return strainName;
  }

  @Override
  public void youngest(int orgGen) {
    if (orgGen > youngest) {
      youngest = orgGen;
    }
  }

  @Override
  public int getYoungest() {
    return youngest;
  }

  @Override
  public void update(Organism org) {
    IntStream.range(0, 15).forEach(j -> {
      org.acquireRand(org.getRedX(), org.getGreenX(), org.getBlueX());
    });

    org.replicate();

    final int randomInt = Organism.randomInt(0, org.getMovesEach());
    for (int i = 0; i < randomInt; i++) {
      if (org.getEnergy() < org.getMoveCost()) {
        break;
      }
      org.move();
    }
    org.updates++;
    org.check();
  }
}