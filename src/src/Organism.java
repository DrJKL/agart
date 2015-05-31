package src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

public class Organism implements Comparable<Organism> {

    String orgName, causeOfDeath;
    Strain strain;
    private final Environment envr;
    private int generation;
    private int childrenSpawned, movesMade;

    public int updates;

    private int resourcesGathered, energy, red, blue, green;
    private ArrayList<Color> view;

    private int mutation, mutationX, moveCost, reprCost, reprX, energyCap;

    private int row, col, origRow, origCol;

    public int redX, greenX, blueX;
    public int northX, eastX, southX, westX;

    private boolean immortal = false;

    private static final String codDef = "Living";
    private static final String codStarved = "Starvation";
    private static final String codOldAge = "Old Age";
    @SuppressWarnings("unused")
    private static final String codCrowding = "Overcrowding";
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

    public int randomInt(int low, int high) {
        return low + (int) (Math.random() * (high - low + 1));
    }

    // New organism with random attributes at random location
    public Organism(Environment env, Strain str) {
        envr = env;
        strain = str;
        orgName = str.getStrainName();
        causeOfDeath = codDef;
        generation = 0;
        strain.youngest(0);

        row = randomInt(0, envr.height);
        col = randomInt(0, envr.width);
        origRow = row;
        origCol = col;

        energyCap = randomInt(capLow, capHigh);
        energy = energyCap / 2;

        moveCost = randomInt(moveLow, moveHigh);
        reprCost = randomInt(rprLow, rprHigh);
        reprX = randomInt(rprXLow, rprXHigh);
        mutation = randomInt(mutLow, mutHigh);
        mutationX = randomInt(mutXLow, mutXHigh);

        redX = randomInt(0, 100);
        greenX = randomInt(0, 100);
        blueX = randomInt(0, 100);
    }

    // New virus with random attributes at a set location
    public Organism(Environment env, Strain str, int r, int c) {
        envr = env;
        strain = str;
        orgName = str.getStrainName();
        causeOfDeath = codDef;
        generation = 0;
        strain.youngest(0);

        row = r;
        col = c;

        // setRandomAttributes();

        origRow = row;
        origCol = col;

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
        origRow = row;
        origCol = col;
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

    public void setRandomAttributes() {

        origRow = row;
        origCol = col;

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
            passOn();
            causeOfDeath = codStarved;
        }
        if (updates > lifeSpan) {
            passOn();
            causeOfDeath = codOldAge;
        }
        if (envr.getActiveStrainSize(strain) > popCap && generation + 1 < this.strain.getYoungest()) {
            passOn();
            causeOfDeath = codGericide;
        }
        if (onEdge()) {
            passOn();
            causeOfDeath = codHereBeDragons;
        }
    }

    private boolean onEdge() {
        return row == 0 || col == 0 || col == envr.width - 1 || row == envr.height - 1;
    }

    public void passOn() {
        envr.graveyard.add(this);
    }

    public void divide() {
        if (energy < energyCap || !hasSpace()) {
            return;
        } else {
            Organism child1, child2;
            child1 = repr();
            childrenSpawned++;
            child2 = repr();
            childrenSpawned++;
            envr.addKid(child1);
            envr.addKid(child2);
            strain.youngest(generation + 1);
            passOn();
        }
    }

    public void replicate() {
        if (energy < reprCost || !hasSpace()) {
            return;
        } else {
            Organism child;
            child = repr();
            energy /= 2;
            childrenSpawned++;
            envr.addKid(child);
            strain.youngest(generation + 1);
        }
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

    // dir is one of the four cardinal directions ("North", "South", "East",
    // "West")
    // or one "Up", "Down", "Left", "Right"
    // Case doesn't matter, abbreviations or single characters valid
    public void move(String dir) {

        int direction = -1;
        final char dirChar = dir.toLowerCase().charAt(0);

        if (dirChar == 'u' || dirChar == 'n') {
            direction = 0;
        }
        if (dirChar == 'r' || dirChar == 'e') {
            direction = 1;
        }
        if (dirChar == 'd' || dirChar == 's') {
            direction = 2;
        }
        if (dirChar == 'l' || dirChar == 'w') {
            direction = 3;
        }

        move(direction);
    }

    /*
     * dir >= 0 and dir <4
     */
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

        // envr.resetPosition(row, col, this);

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

    public boolean inRegion(int minX, int minY, int maxX, int maxY) {
        if (minX < 0) {
            minX = 0;
        }
        if (minY < 0) {
            minY = 0;
        }
        if (maxX >= envr.width) {
            maxX = envr.width - 1;
        }
        if (maxY >= envr.height) {
            maxY = envr.width - 1;
        }

        return (col >= minX && row >= minY && col <= maxX && row <= maxY);
    }

    public boolean inRegion(Region reg) {
        return reg.inRegion(this);
    }

    @SuppressWarnings("unused")
    private int neighborCount() {
        int neighbors = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; i <= col + 1; j++) {
                if (i < 0 || j < 0 || i >= envr.image.getHeight() || j >= envr.image.getWidth()) {
                    continue;
                } else if (envr.orgAt(j, i)) {
                    neighbors++;
                }
            }
        }
        return neighbors - 1;
    }

    public int distanceFromOrigin() {
        return distanceFrom(origCol, origRow);
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
        view = new ArrayList<Color>();
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
            view.add(0, Color.BLACK);
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

    @SuppressWarnings("unchecked")
    public int viewSecondRed() {
        setView();
        final ArrayList<Color> newView = (ArrayList<Color>) view.clone();
        newView.set(this.viewMaxRed(), null);
        int maxIdx = 0;
        int maxRed = 0;
        for (int i = 0; i < newView.size(); i++) {
            if (newView.get(i) != null) {
                final int currRed = newView.get(i).getRed();
                if (currRed >= maxRed) {
                    maxIdx = i;
                    maxRed = currRed;
                }
            }
        }
        return maxIdx;
    }

    @SuppressWarnings("unchecked")
    public int viewSecondGreen() {
        setView();
        final ArrayList<Color> newView = (ArrayList<Color>) view.clone();
        newView.set(this.viewMaxGreen(), null);
        int maxIdx = 0;
        int maxGreen = 0;
        for (int i = 0; i < newView.size(); i++) {
            if (newView.get(i) != null) {
                final int currGreen = newView.get(i).getGreen();
                if (currGreen >= maxGreen) {
                    maxIdx = i;
                    maxGreen = currGreen;
                }
            }
        }
        return maxIdx;
    }

    @SuppressWarnings("unchecked")
    public int viewSecondBlue() {
        setView();
        final ArrayList<Color> newView = (ArrayList<Color>) view.clone();
        newView.set(this.viewMaxBlue(), null);
        int maxIdx = 0;
        int maxBlue = 0;
        for (int i = 0; i < newView.size(); i++) {
            if (newView.get(i) != null) {
                final int currBlue = newView.get(i).getBlue();
                if (currBlue >= maxBlue) {
                    maxIdx = i;
                    maxBlue = currBlue;
                }
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

    @SuppressWarnings("unchecked")
    public int viewSecondAll() {
        setView();
        final ArrayList<Color> newView = (ArrayList<Color>) view.clone();
        newView.set(viewMaxAll(), null);
        int maxIdx = 0;
        int maxAll = 0;
        for (int i = 0; i < newView.size(); i++) {
            if (newView.get(i) != null) {
                final Color check = newView.get(i);
                final int currAll = check.getBlue() + check.getRed() + check.getGreen();
                if (currAll >= maxAll) {
                    maxIdx = i;
                    maxAll = currAll;
                }
            }
        }
        return maxIdx;
    }

    public void acquireRand() {
        final int rand = randomInt(1, 3);
        if (rand == 1) {
            acquireRed();
        }
        if (rand == 2) {
            acquireGreen();
        }
        if (rand == 3) {
            acquireBlue();
        }
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

    /**
     * @return the generation
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * @param generation
     *            the generation to set
     */
    public void setGeneration(int generation) {
        this.generation = generation;
    }

    /**
     * @return the movesMade
     */
    public int getMovesMade() {
        return movesMade;
    }

    /**
     * @param movesMade
     *            the movesMade to set
     */
    public void setMovesMade(int movesMade) {
        this.movesMade = movesMade;
    }

    /**
     * @return the resourcesGathered
     */
    public int getResourcesGathered() {
        return resourcesGathered;
    }

    /**
     * @param resourcesGathered
     *            the resourcesGathered to set
     */
    public void setResourcesGathered(int resourcesGathered) {
        this.resourcesGathered = resourcesGathered;
    }

    /**
     * @return the energy
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * @param energy
     *            the energy to set
     */
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    /**
     * @return the mutation
     */
    public double getMutation() {
        return mutation;
    }

    /**
     * @param mutation
     *            the mutation to set
     */
    public void setMutation(int mutation) {
        this.mutation = mutation;
    }

    /**
     * @return the moveCost
     */
    public int getMoveCost() {
        return moveCost;
    }

    /**
     * @param moveCost
     *            the moveCost to set
     */
    public void setMoveCost(int moveCost) {
        this.moveCost = moveCost;
    }

    /**
     * @return the reprCost
     */
    public int getReprCost() {
        return reprCost;
    }

    /**
     * @param reprCost
     *            the reprCost to set
     */
    public void setReprCost(int reprCost) {
        this.reprCost = reprCost;
    }

    /**
     * @return the energyCap
     */
    public int getEnergyCap() {
        return energyCap;
    }

    /**
     * @param energyCap
     *            the energyCap to set
     */
    public void setEnergyCap(int energyCap) {
        this.energyCap = energyCap;
    }

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @param row
     *            the row to set
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * @return the col
     */
    public int getCol() {
        return col;
    }

    /**
     * @param col
     *            the col to set
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * @return the red
     */
    public int getRed() {
        return red;
    }

    /**
     * @param red
     *            the red to set
     */
    public void setRed(int red) {
        this.red = red;
    }

    /**
     * @return the blue
     */
    public int getBlue() {
        return blue;
    }

    /**
     * @param blue
     *            the blue to set
     */
    public void setBlue(int blue) {
        this.blue = blue;
    }

    /**
     * @return the green
     */
    public int getGreen() {
        return green;
    }

    /**
     * @param green
     *            the green to set
     */
    public void setGreen(int green) {
        this.green = green;
    }

    /**
     * @return the redX
     */
    public int getRedX() {
        return redX;
    }

    /**
     * @return the greenX
     */
    public int getGreenX() {
        return greenX;
    }

    /**
     * @return the blueX
     */
    public int getBlueX() {
        return blueX;
    }

    /**
     * @param redX
     *            the redX to set
     */
    public void setRedX(int redX) {
        this.redX = redX;
    }

    /**
     * @param greenX
     *            the greenX to set
     */
    public void setGreenX(int greenX) {
        this.greenX = greenX;
    }

    /**
     * @param blueX
     *            the blueX to set
     */
    public void setBlueX(int blueX) {
        this.blueX = blueX;
    }

    public int getMutationX() {
        return mutationX;
    }

    public void setMutationX(int mutX) {
        this.mutationX = mutX;
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

    public void setImmortal(boolean immortal) {
        this.immortal = immortal;
    }

    public boolean isImmortal() {
        return immortal;
    }

    @Override
    public int compareTo(Organism org) {
        if (orgName.length() == org.orgName.length()) {
            return orgName.compareTo(org.orgName);
        } else {
            return orgName.length() - org.orgName.length();
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Organism [strain=");
        builder.append(orgName);
        builder.append(", causeOfDeath=");
        builder.append(causeOfDeath);
        builder.append(", generation=");
        builder.append(generation);
        builder.append(", childrenSpawned=");
        builder.append(getChildrenSpawned());
        builder.append(", movesMade=");
        builder.append(movesMade);
        builder.append(", updates=");
        builder.append(updates);
        builder.append(", resourcesGathered=");
        builder.append(resourcesGathered);
        builder.append(", energy=");
        builder.append(energy);
        builder.append(", red=");
        builder.append(red);
        builder.append(", blue=");
        builder.append(blue);
        builder.append(", green=");
        builder.append(green);
        builder.append(", mutation=");
        builder.append(mutation);
        builder.append(", moveCost=");
        builder.append(moveCost);
        builder.append(", reprCost=");
        builder.append(reprCost);
        builder.append(", energyCap=");
        builder.append(energyCap);
        builder.append(", row=");
        builder.append(row);
        builder.append(", col=");
        builder.append(col);
        builder.append("]");
        return builder.toString();
    }

    /**
     * @param strainSet
     */
    public void setStrain(Strain strainSet) {
        strain = strainSet;
        final String newName = strainSet.getStrainName()
                + orgName.substring(strain.getStrainName().length() - 1);
        orgName = newName;
    }

    public int getMovesEach() {
        return movesEach;
    }

    /**
     * @return reprX
     */
    public double getReprX() {
        return reprX;
    }

}

class Region {

    int minX, minY, maxX, maxY;

    private Region(int minimumX, int minimumY, int maximumX, int maximumY) {
        minX = minimumX;
        minY = minimumY;
        maxX = maximumX;
        maxY = maximumY;
    }

    boolean inRegion(Organism org) {
        final int col = org.getCol();
        final int row = org.getRow();
        return (col >= minX && row >= minY && col <= maxX && row <= maxY);
    }
}

// Comparators Below

class OrgSortByStrainID implements Comparator<Organism> {
    @Override
    public int compare(Organism o1, Organism o2) {
        if (o1.orgName.length() == o2.orgName.length()) {
            return o1.orgName.compareTo(o2.orgName);
        } else {
            return o1.orgName.length() - o2.orgName.length();
        }
    }
}

class OrgSortByGeneration implements Comparator<Organism> {
    @Override
    public int compare(Organism o1, Organism o2) {
        return o1.getGeneration() - o2.getGeneration();
    }
}

class OrgSortByCOD implements Comparator<Organism> {
    @Override
    public int compare(Organism o1, Organism o2) {
        return o1.causeOfDeath.compareTo(o2.causeOfDeath);
    }
}
