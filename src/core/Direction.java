package core;

import static core.Numbers.randomInt;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Direction {
  NORTH(0, -1),
  EAST(-1, 0),
  SOUTH(0, 1),
  WEST(1, 0);

  public final Point point;

  private Direction(int dx, int dy) {
    this.point = new Point(dx, dy);
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
    return values()[randomInt(0, 3)];
  }

  private static final List<Direction> directionsToShuffle = Arrays.asList(values());

  public static List<Direction> shuffled() {
    Collections.shuffle(directionsToShuffle);
    return directionsToShuffle;
  }
}