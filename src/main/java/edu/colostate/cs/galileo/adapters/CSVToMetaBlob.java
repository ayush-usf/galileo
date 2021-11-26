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

public class CSVToMetaBlob {

  private static final String NAME_ID = "Parameter Name";
  private static final String DATE_ID = "Date GMT";
  private static final String TIME_ID = "Time GMT";
  private static final String LAT_ID = "Latitude";
  private static final String LON_ID = "Longitude";

  public static void main(String[] args) throws Exception {
    String fileName = args[0];
    Reader in = new FileReader(fileName);
    Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

    FileOutputStream fOut = new FileOutputStream(fileName + ".mblob");
    BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
    SerializationOutputStream out = new SerializationOutputStream(buffOut);

    /* Write a placeholder for the blob length */
    out.writeInt(0);

    System.out.print("Generating metadata...");
    int counter = 0;

    for (CSVRecord record : records) {
      Metadata meta = new Metadata();
      float lat = 0.0f;
      float lon = 0.0f;
      String dateStr = "";
      String timeStr = "";

      Map<String, String> recordMap = record.toMap();
      for (String key : recordMap.keySet()) {
        String value = recordMap.get(key);

        if (key.equals(NAME_ID)) {
          meta.putAttribute(new Feature(key, value));
          continue;
        }

        /* If this is a date, handle differently */
        if (key.equals(DATE_ID)) {
          dateStr = value;
          continue;
        } else if (key.equals(TIME_ID)) {
          timeStr = value;
          continue;
        }

        /* Everything past this point is assumed to be a floating-point
         * number. */

        float floatValue = 0.0f;
        try {
          floatValue = Float.parseFloat(value);
        } catch (NumberFormatException e) {
          //System.out.println("Skipping non-float value: "
          //        + "[" + key + " = " + value + "]");
          continue;
        }

        /* If this is a location, handle differently */
        if (key.equals(LAT_ID)) {
          lat = floatValue;
          continue;
        } else if (key.equals(LON_ID)) {
          lon = floatValue;
          continue;
        }

        Feature feature = new Feature(key, floatValue);
        meta.putAttribute(feature);
      }

      Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm")
          .parse(dateStr + " " + timeStr);
      TemporalProperties tp = new TemporalProperties(date.getTime());
      meta.setTemporalProperties(tp);
      meta.setSpatialProperties(new SpatialProperties(lat, lon));

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
