package core;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import src.Environment;
import src.Organism;
import src.Strain;

public class EnvironmentDataFormatter {
  private final Environment environment;

  public EnvironmentDataFormatter(Environment environment) {
    this.environment = environment;
  }

  public String listLiving() {
    environment.activeStrains.values().forEach(Collections::sort);
    int i = 1;
    int columns = 0;
    final int spacePer = this.getLongestLivingOrgName().length() + 15;
    final StringBuilder builder = new StringBuilder();
    builder.append("\n" + environment.livingOrgs() + " Living:\n");
    for (final LinkedList<Organism> strain : environment.activeStrains.values()) {
      for (final Organism o : strain) {
        String str = o.orgName;
        final int spaces = this.getLongestLivingOrgName().length() - o.orgName.length() + 1;
        for (int j = 0; j < spaces; j++) {
          str += " ";
        }
        str += o.distanceFromCenter();
        final int moreSpaces = 3 - Integer.toString(o.distanceFromCenter()).length();
        for (int j = 0; j < moreSpaces; j++) {
          str += " ";
        }
        str += "from OP";
        builder.append(str);
        columns += str.length();
        while (columns % spacePer != 0) {
          builder.append(" ");
          columns++;
        }
        if (i % 1 == 0) {
          builder.append("\n");
          columns = 0;
        }
        i++;
      }
    }
    return builder.toString();
  }

  public String getLongestLivingOrgName() {
    String res = "";
    for (final List<Organism> orgs : environment.activeStrains.values()) {
      for (final Organism o : orgs) {
        if (o.orgName.length() > res.length()) {
          res = o.orgName;
        }
      }
    }
    return res;
  }

  public String getLongestLivingOrgName_new() {
    return environment.activeStrains.values().stream().flatMap(LinkedList::stream)
        .map(Organism::getOrganismName).max(Comparator.comparing(String::length)).get();
  }

  public String listStrains() {
    final StringBuilder res = new StringBuilder();
    for (final Strain c : environment.strains.keySet()) {
      final LinkedList<Organism> orgsIn = environment.strains.get(c);
      Collections.sort(orgsIn);
      res.append(orgsIn.size() + " in Strain " + c.getStrainName() + ":" + "\n");
      for (final Organism o : orgsIn) {
        res.append(o.orgName + "\n");
      }
      res.append("-------------------" + "\n");
    }
    return res.toString();
  }

  public String listStrainData() {
    final StringBuilder res = new StringBuilder();
    res.append("<html>");
    for (final Strain s : environment.strains.keySet()) {
      res.append("Strain " + s.getStrainName() + ": \t" + environment.getStrainSize(s) + "<BR>");
    }
    res.append("</html>");
    return res.toString();
  }

  public String listActiveStrainData() {
    final StringBuilder res = new StringBuilder();
    res.append("<html>");
    for (final Strain s : environment.activeStrains.keySet()) {
      res.append("Strain " + s.getStrainName() + ": \t" + environment.getActiveStrainSize(s)
          + "<BR>");
    }
    res.append("</html>");
    return res.toString();
  }

}
