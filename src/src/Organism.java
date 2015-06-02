package src;

import java.awt.Color;
import java.util.ArrayList;

public class Organism implements Comparable<Organism> {

    public String orgName;
    public String causeOfDeath = "";
    Strain strain;
    private final Environment envr;
    private final int generation;
    private int childrenSpawned, movesMade;

    public int updates;

    private int resourcesGathered, energy, red, blue, green;
    private ArrayList<Color> view;

    private final int mutation, mutationX, moveCost, reprCost, reprX, energyCap;

    private int row, col;

    public final int redX, greenX, blueX;
    public int northX, eastX, southX, westX;

    private static final String codDef = "Living";
    private static final String codStarved = "Starvation";
    private static final String codOldAge = "Old Age";
    private static final String codGericide = "Gericide";
    private static final String codHereBeDragons = "Dragons";

    private static final int mutLow = 0;
    private static final int mutHigh = 33;
    private static final int mutXLow = 2;
    private static final int mutXHigh = 8;
    private static final int moveLow = 20;
    private static final int moveHigh = 50;
    private static final int rprLow = 20;
    private static final int rprHigh = 50;
    private static final int rprXLow = 40;
    private static final int rprXHigh = 60;
    private static final int capLow = 200;
    private static final int capHigh = 500;

    private static final int maxKids = 3;
    private static final int breedCap = 80;

    private static final int movesEach = 20;
    private static final int lifeSpan = 40;
    private static final int popCap = 100;

    private static final int lowestRGBPer = 30;

    public static int randomInt(int low, int high) {
        return low + (int) (Math.random() * (high - low + 1));
    }

    // New organism with random attributes at random location
    public Organism(Environment env, Strain str) {
        this(env, str, randomInt(0, env.height), randomInt(0, env.width));
    }

    // New virus with random attributes at a set location
    public Organism(Environment env, Strain str, int r, int c) {
        row = r;
        col = c;

        envr = env;
        strain = str;
        orgName = str.getStrainName();
        causeOfDeath = codDef;
        generation = 0;
        strain.youngest(0);

        energyCap = randomInt(capLow, capHigh);
        energy = energyCap / 2;

        moveCost = randomInt(moveLow, moveHigh);
        reprCost = randomInt(rprLow, rprHigh);
        reprX = randomInt(rprXLow, rprXHigh);
        mutation = randomInt(mutLow, mutHigh);
        mutationX = randomInt(mutXLow, mutXHigh);

        redX = randomInt(1, 100);
        greenX = randomInt(1, 100);
        blueX = randomInt(1, 100);

    }

    // New Virus with non-random attributes
    public Organism(Environment env, String str, Strain xStrain, int r, int c, int gen, int mut,
            int mutX, int met, int rpr, int rprX, int cap, int rX, int gX, int bX, int nX, int eX,
            int sX, int wX) {
        orgName = str;
        strain = xStrain;
        causeOfDeath = codDef;
        envr = env;
        row = r;
        col = c;
        generation = gen;
        energy = cap / 2;
        mutation = mut;
        mutationX = mutX;
        moveCost = met;
        reprCost = rpr;
        reprX = rprX;
        energyCap = cap;

        redX = rX;
        greenX = gX;
        blueX = bX;

        northX = nX;
        eastX = eX;
        southX = sX;
        westX = wX;

    }

    public void update() {
        strain.update(this);
    }

    public void updateSpiral() {
        for (int i = 0; i < updates; i++) {
            move(updates);
            for (int j = 0; j < Math.pow(Math.log(updates), 2); j++) {
                this.acquireRand(redX, greenX, blueX);
            }
        }
        updates++;
    }

    public void check() {
        if (energy <= 0) {
            causeOfDeath = codStarved;
        }
        if (updates > lifeSpan) {
            causeOfDeath = codOldAge;
        }
        if (envr.getActiveStrainSize(strain) > popCap && generation + 1 < this.strain.getYoungest()) {
            causeOfDeath = codGericide;
        }
        if (onEdge()) {
            causeOfDeath = codHereBeDragons;
        }
        if (!causeOfDeath.equals("Living")) {
            passOn();
        }
    }

    private boolean onEdge() {
        return row == 0 || col == 0 || col == envr.width - 1 || row == envr.height - 1;
    }

    public void passOn() {
        envr.graveyard.add(this);
    }

    public void replicate() {
        if (energy < reprCost || !hasSpace()) {
            return;
        }
        Organism child;
        child = repr();
        energy /= 2;
        childrenSpawned++;
        envr.addKid(child);
        strain.youngest(generation + 1);
    }

    // Creates new Virus with mutated attributes
    public Organism repr() {
        int r = row;
        int c = col;
        while (r == row || c == col || r < 0 || c < 0 || r >= envr.image.getHeight()
                || c >= envr.image.getWidth()) {
            r = randomInt(row - 3, row + 3);
            c = randomInt(col - 3, col + 3);
        }
        final int gen = generation + 1;
        final String str = orgName + getChildrenSpawned();
        int mut, mutX, met, rpr, rprX, cap;

        int rX, gX, bX;

        int nX, eX, sX, wX;

        if (Math.random() * 100 > mutation) {
            mut = Math.abs(randomInt(mutation - mutationX, mutation + mutationX));
        } else {
            mut = mutation;
        }

        if (Math.random() * 100 > mutation) {
            mutX = Math.abs(randomInt(mutationX - mutationX, mutationX + mutationX));
        } else {
            mutX = mutationX;
        }

        if (Math.random() * 100 > mutation) {
            met = Math.abs(randomInt(moveCost - mutationX, moveCost + mutationX));
        } else {
            met = moveCost;
        }

        if (Math.random() * 100 > mutation) {
            rpr = Math.abs(randomInt(reprCost - mutationX, reprCost + mutationX));
        } else {
            rpr = reprCost;
        }

        if (Math.random() * 100 > mutation) {
            rprX = Math.abs(randomInt(reprX - mutationX, reprX + mutationX));
        } else {
            rprX = reprX;
        }

        if (Math.random() * 100 > mutation) {
            cap = Math.abs(randomInt(energyCap - mutationX, energyCap + mutationX));
        } else {
            cap = energyCap;
        }

        if (Math.random() * 100 > mutation) {
            rX = Math.abs(randomInt(redX - mutationX, redX + mutationX));
        } else {
            rX = redX;
        }
        if (Math.random() * 100 > mutation) {
            gX = Math.abs(randomInt(greenX - mutationX, greenX + mutationX));
        } else {
            gX = greenX;
        }
        if (Math.random() * 100 > mutation) {
            bX = Math.abs(randomInt(blueX - mutationX, blueX + mutationX));
        } else {
            bX = blueX;
        }

        if (Math.random() * 100 > mutation) {
            nX = Math.abs(randomInt(northX - mutationX, northX + mutationX));
        } else {
            nX = northX;
        }
        if (Math.random() * 100 > mutation) {
            eX = Math.abs(randomInt(eastX - mutationX, eastX + mutationX));
        } else {
            eX = eastX;
        }
        if (Math.random() * 100 > mutation) {
            sX = Math.abs(randomInt(southX - mutationX, southX + mutationX));
        } else {
            sX = southX;
        }
        if (Math.random() * 100 > mutation) {
            wX = Math.abs(randomInt(westX - mutationX, westX + mutationX));
        } else {
            wX = westX;
        }

        final Organism spawn = new Organism(envr, str, strain, r, c, gen, mut, mutX, met, rpr,
                rprX, cap, rX, gX, bX, nX, eX, sX, wX);

        return spawn;
    }

    public void move() {
        final int direction = randomInt(0, 3);
        move(direction);
    }

    /** dir >= 0 and dir <4 */
    public void move(int dir) {
        int direction = -1;

        direction = Math.abs(dir % 4);

        if (direction == 0) {
            if (row - 1 >= 0) {
                row--;
            } else {
                return;
            }
        } else if (direction == 1) {
            if (col + 1 < envr.width) {
                col++;
            } else {
                return;
            }
        } else if (direction == 2) {
            if (row + 1 < envr.height) {
                row++;
            } else {
                return;
            }
        } else if (direction == 3) {
            if (col - 1 >= 0) {
                col--;
            } else {
                return;
            }
        } else {
            return;
        }

        energy -= moveCost;
        movesMade++;
    }

    /*
     * nX = 3 eX = 5 sX = 7 wX = 9 totX = 24 01, 02, 03 = north 04, 05, 06, 07,
     * 08 = east 09, 10, 11, 12, 13, 14, 15 = south 16, 17, 18, 19, 20, 21, 22,
     * 23, 24 = west north ( >0 , <= nX ) east ( >nX , <= nX+eX ) south ( >nX+eX
     * , <= nX+eX+sX) west ( >nX+eX+sX, <= totX )
     */
    public void move(int nX, int eX, int sX, int wX) {
        final int totX = nX + eX + sX + wX;
        if (totX < 1) {
            return;
        }
        final int rand = randomInt(1, totX);
        if (rand > 0 && rand <= nX) {
            move(0);
        } else if (rand > nX && rand <= (nX + eX)) {
            move(1);
        } else if (rand > (nX + eX) && rand <= (nX + eX + sX)) {
            move(2);
        } else {
            move(3);
        }
    }

    private boolean hasSpace() {
        for (int i = getRow() - 3; i <= getRow() + 3; i++) {
            for (int j = getCol() - 3; i <= getCol() + 3; j++) {
                if (i < 0 || j < 0 || i >= envr.image.getHeight() || j >= envr.image.getWidth()) {
                    continue;
                } else if (!envr.orgAt(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int distanceFromCenter() {
        final int x = envr.width / 2;
        final int y = envr.height / 2;

        return distanceFrom(x, y);
    }

    public int distanceFrom(int x, int y) {
        final int deltX = (col - x);
        final int deltY = (row - y);

        final int change = (int) Math.sqrt((deltX * deltX) + (deltY * deltY));

        return change;
    }

    private void setView() {
        view = new ArrayList<>();
        final int r = row;
        final int c = col;
        if (r < 0 || c < 0 || r >= envr.height || c >= envr.width) {
            return;
        }
        final int North = r - 1;
        final int East = c + 1;
        final int South = r + 1;
        final int West = c - 1;
        if (North >= 0) {
            view.add(0, new Color(envr.image.getRGB(c, North)));
        } else {
            view.add(0, Color.BLACK);
        }
        if (East < envr.width) {
            view.add(1, new Color(envr.image.getRGB(East, r)));
        } else {
            view.add(1, Color.BLACK);
        }
        if (South < envr.height) {
            view.add(2, new Color(envr.image.getRGB(c, South)));
        } else {
            view.add(2, Color.BLACK);
        }
        if (West >= 0) {
            view.add(3, new Color(envr.image.getRGB(West, r)));
        } else {
            view.add(3, Color.BLACK);
        }
    }

    public int viewMaxRed() {
        setView();
        int maxIdx = 0;
        int maxRed = 0;
        for (int i = 0; i < view.size(); i++) {
            final int currRed = view.get(i).getRed();
            if (currRed >= maxRed) {
                maxIdx = i;
                maxRed = currRed;
            }
        }
        return maxIdx;
    }

    public int viewMaxGreen() {
        setView();
        int maxIdx = 0;
        int maxGreen = 0;
        for (int i = 0; i < view.size(); i++) {
            final int currGreen = view.get(i).getGreen();
            if (currGreen >= maxGreen) {
                maxIdx = i;
                maxGreen = currGreen;
            }
        }
        return maxIdx;
    }

    public int viewMaxBlue() {
        setView();
        int maxIdx = 0;
        int maxBlue = 0;
        for (int i = 0; i < view.size(); i++) {
            final int currBlue = view.get(i).getBlue();
            if (currBlue >= maxBlue) {
                maxIdx = i;
                maxBlue = currBlue;
            }
        }
        return maxIdx;
    }

    public int viewMaxAll() {
        setView();
        int maxIdx = 0;
        int maxAll = 0;
        for (int i = view.size() - 1; i >= 0; i--) {
            if (view.get(i) != null) {
                final Color check = view.get(i);
                final int currAll = check.getBlue() + check.getRed() + check.getGreen();
                if (currAll >= maxAll) {
                    maxIdx = i;
                    maxAll = currAll;
                }
            }
        }
        return maxIdx;
    }

    /*
     * rX, gX, bX = totalChance 3, 4, 5 = 12 1 2 3 = Red 1 >= r < Green 4 5 6 7
     * = Green Green <= r <= Red+Green 8 9 10 11 12 = Blue Red+Green < r <=
     * totalChance
     */
    public void acquireRand(int rX, int gX, int bX) {
        final int totX = rX + gX + bX;
        if (envr.getRed(col, row) + envr.getBlue(col, row) + envr.getGreen(col, row) < lowestRGBPer) {
            return;
        }
        if (totX < 1) {
            return;
        }
        final int rand = randomInt(1, totX);
        if (rand > 0 && rand <= rX) {
            acquireRed();
        } else if (rand >= rX && rand <= (rX + gX)) {
            acquireGreen();
        } else if (rand > (rX + gX) && rand <= totX) {
            acquireBlue();
        }
    }

    public void acquireRed() {
        if (envr.getRed(col, row) > lowestRGBPer) {
            final int orig = envr.getRed(col, row);
            final int take = randomInt(1, 20);
            red += take;
            resourcesGathered += take;
            energy += take;
            envr.setRed(col, row, orig - take);
        } else {
            return;
        }
    }

    public void acquireGreen() {
        if (envr.getGreen(col, row) > lowestRGBPer) {
            final int orig = envr.getGreen(col, row);
            final int take = randomInt(1, 20);
            green += take;
            resourcesGathered += take;
            energy += take;
            envr.setGreen(col, row, orig - take);
        } else {
            return;
        }
    }

    public void acquireBlue() {
        if (envr.getBlue(col, row) > lowestRGBPer) {
            final int orig = envr.getBlue(col, row);
            final int take = randomInt(1, 20);
            blue += take;
            resourcesGathered += take;
            energy += take;
            envr.setBlue(col, row, orig - take);
        } else {
            return;
        }
    }

    public String getOrganismName() {
        return orgName;
    }

    public int getGeneration() {
        return generation;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public int getEnergyCap() {
        return energyCap;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getRedX() {
        return redX;
    }

    public int getGreenX() {
        return greenX;
    }

    public int getBlueX() {
        return blueX;
    }

    public int getBreedcap() {
        return breedCap;
    }

    public int getChildrenSpawned() {
        return childrenSpawned;
    }

    public int getMaxkids() {
        return maxKids;
    }

    @Override
    public int compareTo(Organism org) {
        if (orgName.length() == org.orgName.length()) {
            return orgName.compareTo(org.orgName);
        }
        return orgName.length() - org.orgName.length();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Organism [strain=").append(orgName);
        builder.append(", causeOfDeath=").append(causeOfDeath);
        builder.append(", generation=").append(generation);
        builder.append(", childrenSpawned=").append(getChildrenSpawned());
        builder.append(", movesMade=").append(movesMade);
        builder.append(", updates=").append(updates);
        builder.append(", resourcesGathered=").append(resourcesGathered);
        builder.append(", energy=").append(energy);
        builder.append(", red=").append(red);
        builder.append(", blue=").append(blue);
        builder.append(", green=").append(green);
        builder.append(", mutation=").append(mutation);
        builder.append(", moveCost=").append(moveCost);
        builder.append(", reprCost=").append(reprCost);
        builder.append(", energyCap=").append(energyCap);
        builder.append(", row=").append(row);
        builder.append(", col=").append(col);
        builder.append("]");
        return builder.toString();
    }

    public void setStrain(Strain strainSet) {
        strain = strainSet;
        final String newName = strainSet.getStrainName()
                + orgName.substring(strain.getStrainName().length() - 1);
        orgName = newName;
    }

    public int getMovesEach() {
        return movesEach;
    }

    public double getReprX() {
        return reprX;
    }

}
