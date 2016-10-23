package edu.colostate.cs.galileo.stat;

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

    public static void main(String[] args) {
        RunningStatisticsND rnd = new RunningStatisticsND(1);
        rnd.put(3.0);
        rnd.put(10.0);
        rnd.put(1.0);
        rnd.put(-5.0);
        rnd.put(16.25);
        SyntheticData sd = new SyntheticData(rnd);
        System.out.println(rnd.mean(0));
        System.out.println(rnd.var(0));
        System.out.println(rnd.std(0));
    }
}
