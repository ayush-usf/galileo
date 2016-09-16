package edu.colostate.cs.galileo.adapters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import edu.colostate.cs.galileo.dataset.Coordinates;
import edu.colostate.cs.galileo.dataset.MetaArray;
import edu.colostate.cs.galileo.dataset.Metadata;
import edu.colostate.cs.galileo.dataset.Pair;
import edu.colostate.cs.galileo.serialization.SerializationOutputStream;
import edu.colostate.cs.galileo.serialization.Serializer;
import edu.colostate.cs.galileo.util.FileNames;

public class ConvertMetaBundle {

    public static void main(String[] args)
    throws Exception {

        File bundle = new File(args[0]);

        System.out.println("Reading metadata bundle...");
        MetaArray ma = Serializer.restore(MetaArray.class, bundle);

        Pair<String, String> nameParts = FileNames.splitExtension(bundle);
        FileOutputStream fOut = new FileOutputStream(nameParts.a + ".mblob");
        BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
        SerializationOutputStream out =
            new SerializationOutputStream(buffOut);

        System.out.println("Writing metadata blob...");
        out.writeInt(ma.size());
        for (Metadata m : ma) {
            Coordinates c = m.getSpatialProperties().getCoordinates();
            out.writeFloat(c.getLatitude());
            out.writeFloat(c.getLongitude());
            byte[] metaBytes = Serializer.serialize(m);
            out.writeField(metaBytes);
        }
        out.close();
        buffOut.close();
    }
}
