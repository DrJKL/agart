package core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import src.Strain;
import strains.DefaultStrain;

public class YoungestTest {

  final Strain strain = new DefaultStrain();

  @Test
  public void shouldBeZeroWhenUnset() {
    assertEquals(0, Youngest.getYoungest(strain));
  }

  @Test
  public void shouldBeMaxWhenSet() {
    assertEquals(0, Youngest.getYoungest(strain));
    Youngest.updateYoungest(strain, 10);
    assertEquals(10, Youngest.getYoungest(strain));
    Youngest.updateYoungest(strain, 5);
    assertEquals(10, Youngest.getYoungest(strain));
  }

  @Test
  public void shouldBeZeroWhenReset() {
    assertEquals(0, Youngest.getYoungest(strain));
    Youngest.updateYoungest(strain, 10);
    assertEquals(10, Youngest.getYoungest(strain));
    Youngest.resetYoungest(strain);
    assertEquals(0, Youngest.getYoungest(strain));
  }

}
