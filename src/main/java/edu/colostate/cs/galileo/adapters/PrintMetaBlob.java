package edu.colostate.cs.galileo.adapters;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import edu.colostate.cs.galileo.dataset.Metadata;
import edu.colostate.cs.galileo.serialization.SerializationInputStream;
import edu.colostate.cs.galileo.serialization.Serializer;

public class PrintMetaBlob {

    public static void main(String args[]) throws Exception {
        String fileName = args[0];
        System.out.println("Reading metadata blob: " + fileName);
        FileInputStream fIn = new FileInputStream(fileName);
        BufferedInputStream bIn = new BufferedInputStream(fIn);
        SerializationInputStream in = new SerializationInputStream(bIn);

        int num = in.readInt();
        System.out.println("Reading " + num);
        for (int i = 0; i < num; ++i) {
            /* Just ignore the lat/lon header: */
            in.readFloat();
            in.readFloat();

            byte[] metaBytes = in.readField();
            Metadata meta = Serializer.deserialize(
                    Metadata.class, metaBytes);
            System.out.println(meta);
        }

        in.close();
    }
}
