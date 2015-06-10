package src;

import static core.Numbers.randomInt;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import core.ImageUtil;

public class Environment {

  private static final int MAX_POPULATION = 100;
  public final HashMap<Strain, LinkedList<Organism>> activeStrains = new HashMap<>();
  public final BufferedImage image;
  public int updates;

  ArrayList<Organism> graveyard = new ArrayList<>();
  ArrayList<Organism> nursery = new ArrayList<>();

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
      this.addOneAt(str, findEmptyLocation());
    });
  }

  private Point findEmptyLocation() {
    final Point potentialLocation = new Point();
    do {
      potentialLocation.move( //
          randomInt(0, this.getWidth() - 1), //
          randomInt(0, this.getHeight() - 1));
    } while (this.orgAt(potentialLocation));
    return potentialLocation;
  }

  private Organism addOneAt(Strain str, Point point) {
    final Organism next = new Organism(this, str, point);
    addToActiveStrains(next);
    return next;
  }

  public void addKid(Organism org) {
    nursery.add(org);
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

  public boolean overPopulated(Strain strain) {
    return getActiveStrainSize(strain) > MAX_POPULATION;
  }

  public void update() {
    activeStrains.values().stream().flatMap(List::stream).forEach(o -> o.update());
    buryTheDead();
    raiseTheKids();
    updates++;
  }

  private void raiseTheKids() {
    nursery.forEach(this::addToActiveStrains);
    nursery.clear();
  }

  private void buryTheDead() {
    graveyard.forEach(this::removeFromActiveStrains);
    graveyard.clear();
  }

  public void exterminate(Strain str) {
    activeStrains.remove(str);
  }

  public boolean orgAt(int r, int c) {
    return activeStrains.values().stream().flatMap(List::stream)
        .anyMatch(o -> o.getLocation().equals(new Point(c, r)));
  }

  public boolean orgAt(Point point) {
    return orgAt(point.y, point.x);
  }

  public int livingOrgs() {
    return activeStrains.values().stream().mapToInt(List::size).sum();
  }

  public void saveImage() {
    ImageUtil.saveImage("", image);
  }

  public void saveNegative() {
    ImageUtil.saveImage("Negative", ImageUtil.negative(image));
  }

  public boolean inBounds(Point point) {
    return ImageUtil.inBounds(image, point);
  }

  public Color getColor(Point point) {
    return inBounds(point) ? new Color(image.getRGB(point.x, point.y)) : Color.BLACK;
  }

  public int takeRed(Point point, int newRed) {
    return ImageUtil.takeRed(image, point, newRed);
  }

  public int takeGreen(Point point, int newGreen) {
    return ImageUtil.takeGreen(image, point, newGreen);
  }

  public int takeBlue(Point point, int newBlue) {
    return ImageUtil.takeBlue(image, point, newBlue);
  }
}
