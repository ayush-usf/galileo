package edu.colostate.cs.galileo.stat;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ReservoirEvaluator {

  private RunningStatistics stats = new RunningStatistics();
  private Reservoir<Float> reservoir = new Reservoir<>(120);

  public ReservoirEvaluator() throws Exception {
    Files.lines(Paths.get("testFile.txt"))
        .forEach(this::addLine);

    System.out.println(stats);
    //System.out.println(reservoir);
    System.out.println();

    RunningStatistics eval = new RunningStatistics();
    for (Float f : reservoir.sample()) {
      eval.put(f);
    }
    System.out.println(eval);
  }

  public static void main(String[] args) throws Exception {
    ReservoirEvaluator re = new ReservoirEvaluator();
  }

  private void addLine(String line) {
    float f = Float.parseFloat(line.split("\\s+")[1]);
    stats.put(f);
    reservoir.put(f);
  }
}
