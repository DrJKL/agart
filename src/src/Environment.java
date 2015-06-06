package src;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import core.ImageUtil;

public class Environment {

  public final HashMap<Strain, LinkedList<Organism>> activeStrains = new HashMap<>();
  public final BufferedImage image;
  public int updates;

  ArrayList<Organism> graveyard = new ArrayList<>();
  ArrayList<Organism> kids = new ArrayList<>();

  private static int randomInt(int low, int high) {
    return low + (int) (Math.random() * (high - low + 1));
  }

  static int checkBounds(int c, int max) {
    return Math.min(Math.max(c, 0), max);
  }

  public Environment(BufferedImage bimage) {
    image = bimage;
    updates = 0;
  }

  public int getWidth() {
    return image.getWidth();
  }

  public int getHeight() {
    return image.getHeight();
  }

  public void add(int number, Strain str) {
    IntStream.range(0, number).forEach(i -> {
      int placeX, placeY;
      do {
        placeX = randomInt(0, this.getWidth());
        placeY = randomInt(0, this.getHeight());
      } while (this.orgAt(placeY, placeX));
      this.addOneAt(str, placeY, placeX);
    });
  }

  public Organism addOneAt(Strain str, int r, int c) {
    final Organism next = new Organism(this, str, //
        checkBounds(r, getHeight() - 1), checkBounds(c, getWidth() - 1));
    addToActiveStrains(next);
    return next;
  }

  public void addKid(Organism org) {
    kids.add(org);
  }

  private void addToActiveStrains(Organism org) {
    final Strain s = org.strain;
    final LinkedList<Organism> toAdd = activeStrains.getOrDefault(s, new LinkedList<>());
    toAdd.add(org);
    activeStrains.put(s, toAdd);
  }

  private void removeFromActiveStrains(Organism org) {
    final Strain sChar = org.strain;
    final LinkedList<Organism> toAdd = activeStrains.getOrDefault(sChar, new LinkedList<>());
    toAdd.remove(org);
    activeStrains.put(sChar, toAdd);
  }

  public int getActiveStrainSize(Strain strain) {
    return activeStrains.getOrDefault(strain, new LinkedList<>()).size();
  }

  public void update() {
    activeStrains.values().stream().flatMap(List::stream).forEach(o -> {
      o.update();
    });
    buryTheDead();
    raiseTheKids();
    updates++;
  }

  private void raiseTheKids() {
    kids.forEach(o -> {
      this.addToActiveStrains(o);
    });
    kids.clear();
  }

  private void buryTheDead() {
    graveyard.forEach(org -> {
      this.removeFromActiveStrains(org);
    });
    graveyard.clear();
  }

  public void exterminate(Strain str) {
    activeStrains.remove(str);
  }

  public boolean orgAt(int r, int c) {
    return activeStrains.values().stream().flatMap(List::stream).anyMatch(o -> {
      return o.getRow() == r && o.getCol() == c;
    });
  }

  public int livingOrgs() {
    return activeStrains.values().stream().mapToInt(List::size).sum();
  }

  private static String getDateTime() {
    return new SimpleDateFormat("MM-dd-HHmmss").format(new Date());
  }

  public void saveImage() {
    saveImage("", image);
  }

  public void saveNegative() {
    saveImage("Negative", ImageUtil.negative(image));
  }

  private static void saveImage(String suffix, BufferedImage image) {
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

  public boolean inBounds(int r, int c) {
    return r >= 0 && r < getHeight() && c >= 0 && c < getWidth();
  }

  public Color getColor(int r, int c) {
    return inBounds(c, r) ? new Color(image.getRGB(r, c)) : Color.BLACK;
  }

  public Color getColor(Point point) {
    return inBounds(point.x, point.y) ? new Color(image.getRGB(point.y, point.x)) : Color.BLACK;
  }

  public void setRed(int r, int c, int newRed) {
    ImageUtil.setRed(r, c, newRed, image);
  }

  public void setGreen(int r, int c, int newGreen) {
    ImageUtil.setGreen(r, c, newGreen, image);
  }

  public void setBlue(int r, int c, int newBlue) {
    ImageUtil.setBlue(r, c, newBlue, image);
  }
}
