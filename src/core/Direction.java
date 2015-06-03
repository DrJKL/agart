package core;

import src.Organism;

public enum Direction {
    NORTH, EAST, SOUTH, WEST;
    public static Direction random() {
        return fromInt(Organism.randomInt(0, 3));
    }

    public static Direction fromInt(int dir) {
        switch (dir % 4) {
        case 0:
            return NORTH;
        case 1:
            return EAST;
        case 2:
            return SOUTH;
        case 3:
            return WEST;
        default:
            throw new RuntimeException();
        }
    }
}