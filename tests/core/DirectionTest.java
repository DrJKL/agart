package core;

import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;

public class DirectionTest {

  @Test
  public void translateTest() {
    final Point original = new Point(0, 0);
    Assert.assertEquals(new Point(0, 0), original);
    final Point northOfOriginal = Direction.NORTH.translatedCopy(original);
    Assert.assertEquals(new Point(0, 0), original);

    Assert.assertEquals(new Point(0, -1), northOfOriginal);
  }

  @Test
  public void testTurning() {
    Assert.assertEquals(Direction.EAST, Direction.NORTH.turnRight());
    Assert.assertEquals(Direction.WEST, Direction.NORTH.turnLeft());
  }
}
