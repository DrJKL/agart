package src;

class Region {

  int minX, minY, maxX, maxY;

  private Region(int minimumX, int minimumY, int maximumX, int maximumY) {
    minX = minimumX;
    minY = minimumY;
    maxX = maximumX;
    maxY = maximumY;
  }

  boolean inRegion(Organism org) {
    final int col = org.getCol();
    final int row = org.getRow();
    return (col >= minX && row >= minY && col <= maxX && row <= maxY);
  }
}