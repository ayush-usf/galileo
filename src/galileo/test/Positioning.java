
package galileo.test;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import galileo.dataset.BlockMetadata;
import galileo.dataset.Feature;
import galileo.dataset.FileBlock;

import galileo.util.GeoHash;
import galileo.util.SHA1;
import galileo.util.StreamIdentifier;

public class Positioning {

    public static void main(String[] args) {
        List<String> prefixes = new ArrayList<String>();

        Group[] groups = new Group[12];
        for (int i = 0; i < 12; ++i) {
            groups[i] = new Group(4);
        }
        int[][] counts = new int[12][4];

        for (int i = 0; i < 1000000; ++i) {
            FileBlock block = RandomBlock.generateBlock(2012, 2, 24);
            BlockMetadata metadata = block.getMetadata();
            String geoHash = GeoHash.encode(metadata.getSpatialRange(), 4);
            String geoHash2 = GeoHash.encode(metadata.getSpatialRange(), 11);

            BigInteger geoInt = null;
            BigInteger machineHash = null;
            try {
                //geoInt      = SHA1.fromString(geoHash);
                machineHash = SHA1.fromByteSerializable(metadata);
            } catch(Exception e) {}

            long geoLong = GeoHash.hashToLong(geoHash);
            int group = (int) (geoLong % 12);
            int machine = machineHash.mod(BigInteger.valueOf(4)).intValue();

            counts[group][machine]++;

            Machine m = groups[group].machines.get(machine);

            Feature feature = metadata.getFeatures().get("temperature");
            if (feature != null) {
                m.record(feature.getValue());
            }

            String key = geoHash.substring(0, 1);
            Integer in = m.stuff.get(geoHash2);
            if (in == null) {
                m.stuff.put(geoHash2, new Integer(1));
            } else {
                m.stuff.put(geoHash2, new Integer(in + 1));
            }
        }

        for (int i = 0; i < 12; ++i) {
            System.out.print(i + ":  ");
            int grp = 0;
            for (int j = 0; j < 4; ++j) {
                System.out.print(counts[i][j] + " ");
                grp += counts[i][j];
            }
            System.out.println(" = " + grp);
        }

        System.out.println(" --- ");

//        for (int i = 0; i < 12; ++i) {
//            System.out.println(i + ":  ");
//            int grp = 0;
//            for (Machine m : groups[i].machines) {
//                System.out.println("    " + m);
//                for (String s : new TreeSet<String>(m.stuff.keySet())) {
//                    System.out.println("        " + s + "=" + m.stuff.get(s));
//                }
//                System.out.println("        " + m.stuff.keySet().size());
//            }
//        }
//
        Collections.sort(prefixes);
        for (String s : prefixes) {
            //System.out.println(s);
            //
        }

        System.out.println(GeoHash.hashToLong("f2zv") % 12);
        System.out.println(" --- ");

        for (Group g : groups) {
            for (Machine m : g.machines) {
                System.out.println(m);
                System.out.println(m.rec.get(76));
//                for (Double d : m.rec.keySet()) {
//                    System.out.print(d + ": ");
//                    System.out.println(m.rec.get(d));
//                }
            }
        }
    }
}
