package core;

import java.awt.Point;
import java.util.Random;

public enum Direction {
  NORTH(0, -1),
  EAST(-1, 0),
  SOUTH(0, 1),
  WEST(1, 0);

  private static final Random RANDOM = new Random();
  public final Point point;

  private Direction(int dx, int dy) {
    this.point = new Point(dx, dy);
  }

  public Direction turnRight() {
    return values()[(ordinal() + 1) % values().length];
  }

  public Direction turnLeft() {
    return values()[(ordinal() + values().length - 1) % values().length];
  }

  public Point translatedCopy(Point other) {
    final Point newPoint = other.getLocation();
    newPoint.translate(point.x, point.y);
    return newPoint;
  }

  public void tranlate(Point other) {
    other.translate(point.x, point.y);
  }

  public static Direction random() {
    return values()[RANDOM.nextInt(4)];
  }
}