package edu.colostate.cs.galileo.adapters;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.colostate.cs.galileo.dataset.Coordinates;
import edu.colostate.cs.galileo.dataset.Metadata;
import edu.colostate.cs.galileo.dataset.SpatialProperties;
import edu.colostate.cs.galileo.dataset.TemporalProperties;
import edu.colostate.cs.galileo.dataset.feature.Feature;
import edu.colostate.cs.galileo.serialization.SerializationOutputStream;
import edu.colostate.cs.galileo.serialization.Serializer;

public class AirQualityToMetaBlob {

    public static void main(String[] args) throws Exception {
        String fileName = args[0];
        Reader in = new FileReader(fileName);
        Iterable<CSVRecord> records
            = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

        FileOutputStream fOut
            = new FileOutputStream(fileName + ".mblob");
        BufferedOutputStream buffOut
            = new BufferedOutputStream(fOut);
        SerializationOutputStream out =
            new SerializationOutputStream(buffOut);

        /* Write a placeholder for the blob length */
        out.writeInt(0);

        System.out.print("Generating metadata...");
        int counter = 0;
        for (CSVRecord record : records) {
            Metadata meta = new Metadata();

            float lat = Float.parseFloat(record.get("Latitude"));
            float lon = Float.parseFloat(record.get("Longitude"));
            meta.setSpatialProperties(new SpatialProperties(lat, lon));

            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .parse(record.get("Date GMT") + " " + record.get("Time GMT"));
            TemporalProperties tp = new TemporalProperties(date.getTime());
            meta.setTemporalProperties(tp);

            String featureType = record.get("Parameter Name");
            float featureValue = Float.parseFloat(
                    record.get("Sample Measurement"));
            Feature feature = new Feature(featureType, featureValue);
            meta.putAttribute(feature);

            /* Write out the data */
            Coordinates c = meta.getSpatialProperties().getCoordinates();
            out.writeFloat(c.getLatitude());
            out.writeFloat(c.getLongitude());
            byte[] metaBytes = Serializer.serialize(meta);
            out.writeField(metaBytes);

            ++counter;
            if (counter % 100000 == 0) {
                System.out.print('.');
            }
        }

        System.out.println();
        out.close();
        buffOut.close();

        System.out.println("Writing blob size: " + counter);
        RandomAccessFile raf = new RandomAccessFile(fileName + ".mblob", "rw");
        raf.writeInt(counter);
        raf.close();

        System.out.println("Complete!");
    }
}
