package src;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

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

  public void prepareForDeath(Organism org) {
    graveyard.add(org);
  }

  private void addToActiveStrains(Organism org) {
    activeStrains.computeIfAbsent(org.strain, k -> new LinkedList<>());
    activeStrains.get(org.strain).add(org);
  }

  private void removeFromActiveStrains(Organism org) {
    activeStrains.computeIfAbsent(org.strain, k -> new LinkedList<>());
    activeStrains.get(org.strain).remove(org);
  }

  public int getActiveStrainSize(Strain strain) {
    return activeStrains.getOrDefault(strain, new LinkedList<>()).size();
  }

  public void update() {
    activeStrains.values().stream().flatMap(List::stream).forEach(o -> o.update());
    buryTheDead();
    raiseTheKids();
    updates++;
  }

  private void raiseTheKids() {
    kids.forEach(this::addToActiveStrains);
    kids.clear();
  }

  private void buryTheDead() {
    graveyard.forEach(this::removeFromActiveStrains);
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

  public static String getDateTime() {
    return new SimpleDateFormat("MM-dd-HHmmss").format(new Date());
  }

  public void saveImage() {
    ImageUtil.saveImage("", image);
  }

  public void saveNegative() {
    ImageUtil.saveImage("Negative", ImageUtil.negative(image));
  }

  public boolean inBounds(int r, int c) {
    return r >= 0 && r < getHeight() && c >= 0 && c < getWidth();
  }

  public boolean inBounds(Point point) {
    return inBounds(point.y, point.x);
  }

  public Color getColor(Point point) {
    return inBounds(point) ? new Color(image.getRGB(point.x, point.y)) : Color.BLACK;
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
