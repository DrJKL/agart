package core;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

public class ImageUtil {

  public static int takeRed(BufferedImage image, Point point, int delta) {
    final Color color = new Color(image.getRGB(point.x, point.y));
    final int original = color.getRed();
    final int newValue = Math.max(0, original + delta);
    image.setRGB(point.x, point.y, new Color(newValue, color.getGreen(), color.getBlue()).getRGB());
    return original - newValue;
  }

  public static int takeGreen(BufferedImage image, Point point, int delta) {
    final Color color = new Color(image.getRGB(point.x, point.y));
    final int original = color.getGreen();
    final int newValue = Math.max(0, original + delta);
    image.setRGB(point.x, point.y, new Color(color.getRed(), newValue, color.getBlue()).getRGB());
    return original - newValue;
  }

  public static int takeBlue(BufferedImage image, Point point, int delta) {
    final Color color = new Color(image.getRGB(point.x, point.y));
    final int original = color.getBlue();
    final int newValue = Math.max(0, original + delta);
    image.setRGB(point.x, point.y, new Color(color.getRed(), color.getGreen(), newValue).getRGB());
    return original - newValue;
  }

  public static BufferedImage negative(BufferedImage image) {
    final RescaleOp op = new RescaleOp(-1.0f, 255f, null);
    final BufferedImage negative = op.filter(image, null);
    return negative;
  }

  public static BufferedImage setupNewEnvironment(int w, int h, boolean rand) {
    // Create buffered image that does not support transparency
    final BufferedImage bimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    IntStream.range(0, w).forEach(x -> {
      IntStream.range(0, h).forEach(y -> {
        final int R = rand ? (int) (Math.random() * 256) : 255;
        final int G = rand ? (int) (Math.random() * 256) : 255;
        final int B = rand ? (int) (Math.random() * 256) : 255;
        final Color color = new Color(R, G, B);
        bimage.setRGB(x, y, color.getRGB());
      });
    });
    return bimage;
  }

  public static void saveImage(String suffix, BufferedImage image) {
    final File file = new File(System.getProperty("user.home"), String.format(
        "/outputImages/%s%s.png", getDateTime(), suffix));
    try {
      file.getParentFile().mkdirs();
      file.createNewFile();
      ImageIO.write(image, "png", file);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String getDateTime() {
    return new SimpleDateFormat("MM-dd-HHmmss").format(new Date());
  }
}
