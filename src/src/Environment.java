package src;

import java.awt.Color;
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

import strains.DefaultStrain;
import core.ImageUtil;

public class Environment {

  public final BufferedImage image;

  public final int width;
  public final int height;
  public int updates;

  public final HashMap<Strain, LinkedList<Organism>> activeStrains = new HashMap<>();

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
    width = image.getWidth();
    height = image.getHeight();
    updates = 0;
  }

  public Environment(int w, int h, boolean rand) {
    image = setEnvironment(w, h, rand);
    width = w;
    height = h;
    updates = 0;
  }

  private static BufferedImage setEnvironment(int w, int h, boolean rand) {
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

  public void add(Strain str) {
    this.add(1, str);
  }

  public void add(int number, Strain str) {
    IntStream.range(0, number).forEach(i -> {
      int placeX, placeY;
      do {
        placeX = randomInt(0, this.width);
        placeY = randomInt(0, this.height);
      } while (this.orgAt(placeY, placeX));
      this.addOneAt(str, placeY, placeX);
    });
  }

  public Organism addOneAt(Strain str, int r, int c) {
    final Organism next = new Organism(this, new DefaultStrain(), checkBounds(r, height - 1),
        checkBounds(c, width - 1));
    final Strain strainSet = str;
    next.setStrain(strainSet);
    str.youngest(0);
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
    bringOutDead();
    addKids();
    updates++;
  }

  private void addKids() {
    kids.forEach(o -> {
      this.addToActiveStrains(o);
    });
    kids.clear();
  }

  private void bringOutDead() {
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
    return r >= 0 && r < height //
        && c >= 0 && c < width;
  }

  public Color getColor(int r, int c) {
    return new Color(image.getRGB(r, c));
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