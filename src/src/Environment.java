package src;

import static core.Randomness.randomInt;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
    IntStream.range(0, number).forEach(
        i -> {
          final Point potentialLocation = new Point();
          do {
            potentialLocation.move(randomInt(0, this.getWidth() - 1),
                randomInt(0, this.getHeight() - 1));
          } while (this.orgAt(potentialLocation));
          this.addOneAt(str, potentialLocation);
        });
  }

  private Organism addOneAt(Strain str, Point point) {
    final Organism next = new Organism(this, str, point);
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
    return new Rectangle(getWidth(), getHeight()).contains(point);
  }

  public Color getColor(Point point) {
    return inBounds(point) ? new Color(image.getRGB(point.x, point.y)) : Color.BLACK;
  }

  public void setRed(Point point, int newRed) {
    ImageUtil.setRed(point, newRed, image);
  }

  public void setGreen(Point point, int newGreen) {
    ImageUtil.setGreen(point, newGreen, image);
  }

  public void setBlue(Point point, int newBlue) {
    ImageUtil.setBlue(point, newBlue, image);
  }
}
