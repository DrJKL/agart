package core;

import java.awt.Point;

import src.Organism;

public enum Direction {
  NORTH(0, -1),
  EAST(-1, 0),
  SOUTH(0, 1),
  WEST(1, 0);

  public final Point point;

  private Direction(int dx, int dy) {
    this.point = new Point(dx, dy);
  }

  public Point transform(Point other) {
    final Point newPoint = other.getLocation();
    newPoint.translate(point.x, point.y);
    return newPoint;
  }

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