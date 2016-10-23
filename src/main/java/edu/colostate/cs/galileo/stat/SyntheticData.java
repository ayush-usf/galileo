package edu.colostate.cs.galileo.stat;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.math3.distribution.NormalDistribution;

public class SyntheticData {

    private NormalDistribution[] distributions;

    public SyntheticData(RunningStatisticsND stats) {
        double[] means = stats.means();
        double[] stds = stats.stds();
        double[] mins = stats.mins();
        double[] maxes = stats.maxes();

        this.distributions = new NormalDistribution[stats.dimensions()];
        for (int i = 0; i < stats.dimensions(); ++i) {
            NormalDistribution nd = new NormalDistribution(means[i], stds[i]);
            distributions[i] = nd;

            RunningStatistics rs = new RunningStatistics();
            for (int j = 0; j < 10000; ++j) {
                double sample;
                while (true) {
                    sample = nd.sample();
                    if (sample <= maxes[i] && sample >= mins[i]) {
                        rs.put(sample);
                        break;
                    }
                }
            }
            System.out.println(rs);
        }
    }

    public static void main(String[] args) throws Exception {
        RunningStatisticsND rnd = new RunningStatisticsND(1);
        Files.lines(Paths.get(args[0]))
            .map(Double::parseDouble)
            .forEach(item -> rnd.put(item));
        SyntheticData sd = new SyntheticData(rnd);
        System.out.println(rnd.mean(0));
        System.out.println(rnd.std(0));
    }
}
