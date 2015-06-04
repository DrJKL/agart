package core;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ImageUtilTest {

  private BufferedImage image;

  // private static final Color BLACK = new Color(0, 0, 0);

  @Before
  public void setup() {
    this.image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    final Graphics2D graphics = image.createGraphics();
    graphics.setPaint(WHITE);
    graphics.fillRect(0, 0, 10, 10);
  }

  @Test
  public void testInitialColor() {
    Assert.assertEquals(WHITE.getRGB(), image.getRGB(0, 0));
  }

  @Test
  public void testSetAndGet_Red() {
    Assert.assertEquals(WHITE.getRGB(), image.getRGB(0, 0));
    Assert.assertEquals(WHITE.getRed(), ImageUtil.getRed(0, 0, image));
    ImageUtil.setRed(0, 0, 0, image);
    Assert.assertEquals(0, ImageUtil.getRed(0, 0, image));
  }

  @Test
  public void testNegative() {
    Assert.assertEquals(WHITE.getRGB(), image.getRGB(0, 0));
    Assert.assertEquals(WHITE.getRed(), ImageUtil.getRed(0, 0, image));
    image = ImageUtil.negative(image);
    Assert.assertEquals(BLACK.getRGB(), image.getRGB(0, 0));
    Assert.assertEquals(BLACK.getRed(), ImageUtil.getRed(0, 0, image));
  }
}
