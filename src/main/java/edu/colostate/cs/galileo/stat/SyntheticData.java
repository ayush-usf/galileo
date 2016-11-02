package edu.colostate.cs.galileo.stat;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

public class SyntheticData {

    private RealDistribution[] distributions;
    private double[] mins;
    private double[] maxes;
    private double[] means;

    public SyntheticData(RunningStatisticsND stats) {
        this.mins = stats.mins();
        this.maxes = stats.maxes();
        this.means = stats.means();
        double[] stds = stats.stds();

        this.distributions = new NormalDistribution[stats.dimensions()];
        for (int i = 0; i < stats.dimensions(); ++i) {
            NormalDistribution nd = new NormalDistribution(means[i], stds[i]);
            distributions[i] = nd;
        }
    }

    public double nextSample(int dimension) {
        while (true) {
            double sample = distributions[dimension].sample();
            if (sample <= maxes[dimension] && sample >= mins[dimension]) {
                return sample;
            }
        }
    }

    public void test() {
        for (int i = 0; i < distributions.length; ++i) {
            RunningStatistics rs = new RunningStatistics();
            for (int j = 0; j < 10000; ++j) {
                double sample = nextSample(i);
                rs.put(sample);
            }
            System.out.println(rs);
        }
    }

    public void testBoolean() {
        for (int i = 0; i < distributions.length; ++i) {
            RunningStatistics rs = new RunningStatistics();
            for (int j = 0; j < 100000000; ++j) {
                double sample = distributions[i].sample();
                if (sample <= 0.5) {
                    rs.put(0.0);
                } else if (sample > 0.5) {
                    rs.put(1.0);
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
