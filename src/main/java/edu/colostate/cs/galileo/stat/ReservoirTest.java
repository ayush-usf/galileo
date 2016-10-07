package edu.colostate.cs.galileo.stat;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ReservoirTest {

    public class TRecord {
        public TRecord() { }
        public double value;
        public int index;
    }

    public void go(String fn) throws Exception {
        Reservoir<TRecord> res = new Reservoir<>(360);
        RunningStatistics quant = new RunningStatistics();
        RunningStatistics actual = new RunningStatistics();
        List<Double> values = Files.lines(
                Paths.get(fn), Charset.defaultCharset())
            .map(line -> Double.parseDouble(line))
            .collect(Collectors.toList());

        int idx = 0;
        int counter = 0;
        for (Double v : values) {
            counter++;
            if (counter == 900) {
                idx++;
                counter = 0;
            }
            actual.put(v);
            TRecord rec = new TRecord();
            rec.value = v;
            rec.index = idx;
            res.put(rec);
        }

        for (int i = 0; i < idx; ++i) {
            RunningStatistics stat = new RunningStatistics();
            for (TRecord r : res.sample()) {
                if (r.index == i) {
                    stat.put(r.value);
                }
            }
            System.out.println(stat.mean());
        }
    }

    public static void main(String[] args) throws Exception {
        ReservoirTest rt = new ReservoirTest();
        rt.go(args[0]);
    }
}
