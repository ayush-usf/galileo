package edu.colostate.cs.galileo.adapters;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.colostate.cs.galileo.dataset.Metadata;
import edu.colostate.cs.galileo.dataset.SpatialProperties;
import edu.colostate.cs.galileo.serialization.SerializationException;
import edu.colostate.cs.galileo.serialization.SerializationInputStream;
import edu.colostate.cs.galileo.serialization.Serializer;

public class ReadMetadata {

    public static List<Metadata> readMetaBlob(File file)
    throws FileNotFoundException, IOException, SerializationException {
        List<Metadata> metadataList = new ArrayList<>();

        FileInputStream fIn = new FileInputStream(file);
        BufferedInputStream bIn = new BufferedInputStream(fIn);
        SerializationInputStream in = new SerializationInputStream(bIn);

        int num = in.readInt();
        for (int i = 0; i < num; ++i) {
            float lat = in.readFloat();
            float lon = in.readFloat();
            byte[] payload = in.readField();

            Metadata m = Serializer.deserialize(Metadata.class, payload);
            m.setSpatialProperties(new SpatialProperties(lat, lon));
            metadataList.add(m);
        }

        in.close();
        bIn.close();

        return metadataList;
    }
}
