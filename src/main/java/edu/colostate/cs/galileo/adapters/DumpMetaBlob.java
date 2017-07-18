package edu.colostate.cs.galileo.adapters;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;

import edu.colostate.cs.galileo.dataset.Metadata;
import edu.colostate.cs.galileo.dataset.feature.Feature;
import edu.colostate.cs.galileo.serialization.SerializationInputStream;
import edu.colostate.cs.galileo.serialization.Serializer;
import edu.colostate.cs.galileo.util.Geohash;

public class DumpMetaBlob {

    private static Set<String> activeFeatures = new HashSet<>();

    public static void main(String[] args) throws Exception {
        for (String featureName : TestConfiguration.FEATURE_NAMES) {
            activeFeatures.add(featureName);
        }

        for (String fileName : args) {
            System.err.println("Reading metadata blob: " + fileName);
            loadData(fileName);
        }
    }

    public static void loadData(String fileName)
    throws Exception {
        FileInputStream fIn = new FileInputStream(fileName);
        BufferedInputStream bIn = new BufferedInputStream(fIn);
        SerializationInputStream in = new SerializationInputStream(bIn);

        int num = in.readInt();
        System.err.println("Records: " + num);

        for (int i = 0; i < num; ++i) {
            float lat = in.readFloat();
            float lon = in.readFloat();
            byte[] payload = in.readField();

            String location = Geohash.encode(lat, lon, 4);

            Metadata m = Serializer.deserialize(Metadata.class, payload);

            String outStr = fileName + "    " + location + "    ";
            for (Feature f : m.getAttributes()) {
                String featureName = f.getName();
                if (activeFeatures.contains(featureName) == false) {
                    continue;
                }
                System.out.println(f.getName());

                outStr += f.getDouble() + "    ";
            }
            System.out.println(outStr);
        }

        in.close();
    }
}
