package edu.colostate.cs.galileo.stat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.colostate.cs.galileo.dataset.feature.Feature;
import edu.colostate.cs.galileo.dataset.feature.FeatureSet;
import edu.colostate.cs.galileo.serialization.SerializationOutputStream;
import edu.colostate.cs.galileo.serialization.Serializer;
import edu.colostate.cs.galileo.util.PerformanceTimer;

public class SpillTest {

    public static void main(String[] args) throws IOException {
        Random r = new Random();

        TreeMap<Integer, FeatureSet> tm = new TreeMap<>();

        FeatureSet fs = new FeatureSet();
        for (int i = 0; i < 100; ++i) {
            Feature f = new Feature("test", r.nextDouble());
            fs.put(f);
        }

        for (int i = 0; i < 9600; ++i) {
            tm.put(i, fs);
        }

        //int[] sizes = {10, 100, 200, 300, 400, 500, 600, 700, 800};
        //int[] sizes = {800, 900, 1000, 1100, 1200, 1300, 1400, 1500};
        int[] sizes = {1500, 1600, 1700, 1800, 1900, 2000, 2100, 2200};

        PerformanceTimer pt = new PerformanceTimer("query");
        for (int i = 0; i < 1000; ++i) {
            int a = r.nextInt(9600);
            int sz = sizes[r.nextInt(sizes.length)];
            int b = a + sz;
            pt.start();
            SortedMap<Integer, FeatureSet> m = tm.subMap(a, b);
            SerializationOutputStream sout = new SerializationOutputStream(
                    new BufferedOutputStream(
                        new ByteArrayOutputStream()));
            for (FeatureSet set : m.values()) {
                set.serialize(sout);
            }
            sout.close();
            pt.stopAndPrint();
        }
    }

}
