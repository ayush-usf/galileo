/*
Copyright (c) 2013, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package galileo.stat;

/**
 * Provides an online method for computing mean, variance, and standard
 * deviation.  Based on "Note on a Method for Calculating Corrected Sums of
 * Squares and Products" by B. P. Welford.
 *
 * @author malensek
 */
public class RunningStatistics {
    private long n;
    private double mean;
    private double M2;

    /**
     * Creates a Welford running statistics instance without no observed values.
     */
    public RunningStatistics() { }

    /**
     * Creates a copy of a {@link RunningStatistics} instance.
     */
    public RunningStatistics(RunningStatistics other) {
        copyFrom(other);
    }

    /**
     * Create a new {@link RunningStatistics} instance by combining multiple
     * existing instances.
     */
    public RunningStatistics(RunningStatistics... others) {
        if (others.length == 0) {
            return;
        } else if (others.length == 1) {
            copyFrom(others[0]);
            return;
        }

        /* Calculate new n */
        for (RunningStatistics rs : others) {
            combine(rs);
        }
    }

    /**
     * Copies statistics from another RunningStatistics instance.
     */
    private void copyFrom(RunningStatistics other) {
        this.n = other.n;
        this.mean = other.mean;
        this.M2 = other.M2;
    }

    public void combine(RunningStatistics other) {
        long newN = n + other.n;
        double delta = mean - other.mean;
        mean = (n * mean + other.n * other.mean) / newN;
        M2 = M2 + other.M2 + delta * delta * n * other.n / newN;
        n = newN;
    }

    /**
     * Creates a Welford running statistics instance based on a number of
     * samples.
     */
    public RunningStatistics(double... samples ) {
        for (double sample : samples) {
            put(sample);
        }
    }

    /**
     * Add a number of new samples to the running statistics.
     */
    public void put(double... samples) {
        for (double sample : samples) {
            put(sample);
        }
    }

    /**
     * Add a new sample to the running statistics.
     */
    public void put(double sample) {
        n++;
        double delta = sample - mean;
        mean = mean + delta / n;
        M2 = M2 + delta * (sample - mean);
    }

    /**
     * Calculates the current running mean for the values observed thus far.
     *
     * @return mean of all the samples observed thus far.
     */
    public double mean() {
        return mean;
    }

    /**
     * Calculates the running sample variance.
     *
     * @return sample variance
     */
    public double var() {
        return var(1.0);
    }

    /**
     * Calculates the population variance.
     *
     * @return population variance
     */
    public double popVar() {
        return var(0.0);
    }

    /**
     * Calculates the running variance, given a bias adjustment.
     *
     * @param ddof delta degrees-of-freedom to use in the calculation.  Use 1.0
     * for the sample variance.
     *
     * @return variance
     */
    public double var(double ddof) {
        return M2 / (n - ddof);
    }

    /**
     * Calculates the standard deviation of the samples observed thus far.
     *
     * @return population standard deviation
     */
    public double std() {
        return Math.sqrt(var());
    }

    /**
     * Calculates the standard deviation of the values observed thus far, given
     * a bias adjustment.
     *
     * @param ddof delta degrees-of-freedom to use in the calculation.
     *
     * @return standard deviation
     */
    public double std(double ddof) {
        return Math.sqrt(var(ddof));
    }

    /**
     * Retrieves the number of samples submitted to the RunningStatistics
     * instance so far.
     *
     * @return number of samples
     */
    public long numSamples() {
        return n;
    }

    @Override
    public String toString() {
        String str = "";
        str += "Number of Samples: " + n + System.lineSeparator();
        str += "Mean: " + mean + System.lineSeparator();
        str += "Variance: " + var() + System.lineSeparator();
        str += "Std Dev: " + std();
        return str;
    }
}
