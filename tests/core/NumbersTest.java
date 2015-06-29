package core;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.IntStream;

import org.junit.Test;

public class NumbersTest {

  private static final Random RANDOM = new Random();

  @Test
  public void notATestOfRandom() {
    final Map<Integer, Integer> count = new TreeMap<>();
    IntStream.range(0, 10000).forEach(i -> {
      final int val = 10 - RANDOM.nextInt(21);
      count.compute(val, (k, v) -> v == null ? 1 : v + 1);
    });
    System.out.println(count);
    IntStream.range(-10, 11).forEach(i -> {
      System.out.printf("%d ", i, count.get(i));
      IntStream.range(0, count.get(i) + 1).forEach(j -> System.out.print("."));
      System.out.println("");
    });
  }
}
