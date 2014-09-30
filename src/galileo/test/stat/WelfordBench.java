/*
Copyright (c) 2014, Colorado State University
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

package galileo.test.stat;

import java.util.Random;

import galileo.stat.RunningStatistics;
import galileo.util.PerformanceTimer;

/**
 * Benchmarks the Welford running statistics tracker.
 */
public class WelfordBench {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: WelfordBench iterations num_values");
            System.exit(1);
        }

        int iters = Integer.parseInt(args[0]);
        int values = Integer.parseInt(args[1]);

        for (int i = 0; i < iters; ++i) {
            testUpdate(values);
            testCalc(values);
        }
    }

    private static void testUpdate(int values) {
            /* Generate our incoming samples */
            Random rand = new Random();
            double[] samples = new double[values];
            for (int j = 0; j < values; ++j) {
                samples[j] = rand.nextDouble();
            }

            RunningStatistics rs = new RunningStatistics();
            PerformanceTimer pt = new PerformanceTimer("welford-update");
            pt.start();
            for (int j = 0; j < values; ++j) {
                rs.put(samples[j]);
            }
            pt.stopAndPrint();
    }

    private static void testCalc(int values) {
            /* Generate our incoming samples */
            Random rand = new Random();
            double[] samples = new double[values];
            for (int j = 0; j < values; ++j) {
                samples[j] = rand.nextDouble();
            }

            RunningStatistics rs = new RunningStatistics();
            for (int j = 0; j < values; ++j) {
                rs.put(samples[j]);
            }

            PerformanceTimer pt = new PerformanceTimer("welford-mean");
            double total = 0;
            pt.start();
            for (int j = 0; j < values; ++j) {
                total += rs.mean();
            }
            pt.stopAndPrint();
            /* Print out a sanity check */
            System.out.println(total);

            pt = new PerformanceTimer("welford-std");
            total = 0;
            pt.start();
            for (int j = 0; j < values; ++j) {
                total += rs.std();
            }
            pt.stopAndPrint();
            System.out.println(total);
    }
}
