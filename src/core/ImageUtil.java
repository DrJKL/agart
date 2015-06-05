package core;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class ImageUtil {

  public static int getRed(int y, int x, BufferedImage image) {
    return new Color(image.getRGB(y, x)).getRed();
  }

  public static int getGreen(int y, int x, BufferedImage image) {
    return new Color(image.getRGB(y, x)).getGreen();
  }

  public static int getBlue(int y, int x, BufferedImage image) {
    return new Color(image.getRGB(y, x)).getBlue();
  }

  public static void setRed(int y, int x, int newRed, BufferedImage image) {
    final Color color = new Color(image.getRGB(y, x));
    final Color newColor = new Color(newRed, color.getGreen(), color.getBlue());
    image.setRGB(y, x, newColor.getRGB());
  }

  public static void setGreen(int y, int x, int newGreen, BufferedImage image) {
    final Color color = new Color(image.getRGB(y, x));
    final Color newColor = new Color(color.getRed(), newGreen, color.getBlue());
    image.setRGB(y, x, newColor.getRGB());
  }

  public static void setBlue(int y, int x, int newBlue, BufferedImage image) {
    final Color color = new Color(image.getRGB(y, x));
    final Color newColor = new Color(color.getRed(), color.getGreen(), newBlue);
    image.setRGB(y, x, newColor.getRGB());
  }

  public static BufferedImage negative(BufferedImage image) {
    final RescaleOp op = new RescaleOp(-1.0f, 255f, null);
    final BufferedImage negative = op.filter(image, null);
    return negative;
  }
}
