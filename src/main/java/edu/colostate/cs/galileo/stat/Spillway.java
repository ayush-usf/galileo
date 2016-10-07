package edu.colostate.cs.galileo.stat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colostate.cs.galileo.dataset.Metadata;
import edu.colostate.cs.galileo.dataset.feature.Feature;
import edu.colostate.cs.galileo.serialization.SerializationInputStream;
import edu.colostate.cs.galileo.serialization.Serializer;
import edu.colostate.cs.galileo.util.Geohash;

public class Spillway {

    public static final int RESERVOIR_SIZE = 800;
    public Reservoir<Metadata> reservoir = new Reservoir<>(RESERVOIR_SIZE);

    public Spillway() {

    }

    public void put(Metadata record) {
        reservoir.put(record);
    }

    public static void main(String[] args) throws Exception {
        Map<String, Spillway> spillMap = new HashMap<>();

        RunningStatistics rs1 = new RunningStatistics();
        RunningStatistics rs2 = new RunningStatistics();

        int counter = 0;
        String current = "";

        for (String fileName : args) {
            FileInputStream fIn = new FileInputStream(fileName);
            BufferedInputStream bIn = new BufferedInputStream(fIn);
            SerializationInputStream in = new SerializationInputStream(bIn);

            int records = in.readInt();
            String segment = new File(fileName).getName().substring(0, 21);
            if (segment.equals(current) == false) {
                current = segment;
                counter++;
                System.err.println("-- cut --");
            }
            System.err.println(fileName);
            //System.err.println("Records: " + records);
            for (int i = 0; i < records; ++i) {
                float lat = in.readFloat();
                float lon = in.readFloat();
                String hash = Geohash.encode(lat, lon, 3);
                byte[] payload = in.readField();
                Metadata m = Serializer.deserialize(Metadata.class, payload);
                if (hash.startsWith("9xj")) {
                    //System.out.print('.');
                    Spillway sw = spillMap.get(hash);
                    if (sw == null) {
                        sw = new Spillway();
                        spillMap.put(hash, sw);
                    }
                    m.putAttribute(new Feature("count", counter));
                    m.putAttribute(new Feature("seg", segment));
                    sw.put(m);
                    rs1.put(m.getAttribute("temperature_surface").getDouble());
//                    Serializer.persist(m, m.getName());
                }
            }

            bIn.close();
            in.close();
        }

        List<Metadata> sample = spillMap.get("9xj").reservoir.sample();
        for (int i = 1; i <= counter; ++i) {
            RunningStatistics rs = new RunningStatistics();
            String segment = "";
            for (Metadata m : sample) {
                if (m.getAttribute("count").equals(new Feature(i))) {
                    rs.put(m.getAttribute("temperature_surface").getDouble());
                    segment = m.getAttribute("seg").getString();
                }
            }
            System.out.println("[" + segment + "]; cut: " + i + " " + rs.mean()
                        + "  " + rs.max() + "  " + rs.min());
        }
    }
}
