/*
Copyright (c) 2014, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package edu.colostate.cs.galileo.adapters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import edu.colostate.cs.galileo.dataset.Coordinates;
import edu.colostate.cs.galileo.dataset.MetaArray;
import edu.colostate.cs.galileo.dataset.Metadata;
import edu.colostate.cs.galileo.dataset.Pair;
import edu.colostate.cs.galileo.serialization.SerializationOutputStream;
import edu.colostate.cs.galileo.serialization.Serializer;
import edu.colostate.cs.galileo.util.FileNames;

public class NetCDFToMetaBlob {
    public static void main(String[] args)
    throws Exception {
        File f = new File(args[0]);
        Pair<String, String> nameParts = FileNames.splitExtension(f);
        String ext = nameParts.b;

        if (ext.equals("grb") || ext.equals("bz2") || ext.equals("gz")) {
            System.out.println("Reading netcdf...");
            Map<String, Metadata> metaMap
                = NetCDFToMetaBundle.readFile(f.getAbsolutePath());

            System.out.println("Creating bundle...");
            MetaArray metaBundle = new MetaArray();
            for (String s : metaMap.keySet()) {
                Metadata m = metaMap.get(s);
                metaBundle.add(m);
            }

            FileOutputStream fOut
                = new FileOutputStream(nameParts.a + ".mblob");
            BufferedOutputStream buffOut
                = new BufferedOutputStream(fOut);
            SerializationOutputStream out =
                new SerializationOutputStream(buffOut);

            System.out.println("Writing metadata blob...");
            out.writeInt(metaBundle.size());
            for (Metadata m : metaBundle) {
                Coordinates c = m.getSpatialProperties().getCoordinates();
                out.writeFloat(c.getLatitude());
                out.writeFloat(c.getLongitude());
                byte[] metaBytes = Serializer.serialize(m);
                out.writeField(metaBytes);
            }
            out.close();
            buffOut.close();

            System.out.println("Complete!");
        }
    }
}
