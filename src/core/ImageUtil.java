package core;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class ImageUtil {

  public static int getRed(int r, int c, BufferedImage image) {
    return new Color(image.getRGB(r, c)).getRed();
  }

  public static int getGreen(int r, int c, BufferedImage image) {
    return new Color(image.getRGB(r, c)).getGreen();
  }

  public static int getBlue(int r, int c, BufferedImage image) {
    return new Color(image.getRGB(r, c)).getBlue();
  }

  public static void setRed(int r, int c, int newRed, BufferedImage image) {
    final Color color = new Color(image.getRGB(r, c));
    final Color newColor = new Color(newRed, color.getGreen(), color.getBlue());
    image.setRGB(r, c, newColor.getRGB());
  }

  public static void setGreen(int r, int c, int newGreen, BufferedImage image) {
    final Color color = new Color(image.getRGB(r, c));
    final Color newColor = new Color(color.getRed(), newGreen, color.getBlue());
    image.setRGB(r, c, newColor.getRGB());
  }

  public static void setBlue(int r, int c, int newBlue, BufferedImage image) {
    final Color color = new Color(image.getRGB(r, c));
    final Color newColor = new Color(color.getRed(), color.getGreen(), newBlue);
    image.setRGB(r, c, newColor.getRGB());
  }

  public static BufferedImage negative(BufferedImage image) {
    final RescaleOp op = new RescaleOp(-1.0f, 255f, null);
    final BufferedImage negative = op.filter(image, null);
    return negative;
  }
}
