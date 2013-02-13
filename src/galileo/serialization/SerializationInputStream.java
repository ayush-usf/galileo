/*
Copyright (c) 2013, Colorado State University
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

package galileo.serialization;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.zip.GZIPInputStream;

public class SerializationInputStream extends DataInputStream {

    public SerializationInputStream(InputStream in) {
        super(in);
    }

    public String readString()
    throws IOException {
        byte[] strBytes = readField();
        return new String(strBytes);
    }

    public byte[] readField()
    throws IOException {
        int dataSize = readInt();
        byte[] data = new byte[dataSize];
        read(data);
        return data;
    }

    public byte[] readCompressableField()
    throws IOException {

        boolean compressed = readBoolean();

        if (compressed) {
            int dataSize = readInt();

            GZIPInputStream gIn = new GZIPInputStream(this);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            int size = 0;
            byte[] buffer = new byte[dataSize];
            while ((size = gIn.read(buffer)) > 0) {
                outStream.write(buffer, 0, size);
            }

            outStream.close();
            return outStream.toByteArray();
        } else {
            return readField();
        }
    }
}
