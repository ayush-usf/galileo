package edu.colostate.cs.galileo.stat;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

public class SyntheticData {

    private RealDistribution[] distributions;
    private RunningStatisticsND stats;

    public SyntheticData(RunningStatisticsND stats) {
        double[] means = stats.means();
        double[] stds = stats.stds();

        this.distributions = new NormalDistribution[stats.dimensions()];
        this.stats = stats;

        for (int i = 0; i < stats.dimensions(); ++i) {
            NormalDistribution nd = new NormalDistribution(means[i], stds[i]);
            distributions[i] = nd;
        }
    }

    public double nextSample(int dimension) {
        while (true) {
            double sample = distributions[dimension].sample();
            if (sample <= this.stats.maxes()[dimension]
                    && sample >= this.stats.mins()[dimension]) {
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
