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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

public class SerializationOutputStream extends DataOutputStream {

    private int compressionLevel = Deflater.DEFAULT_COMPRESSION;

    public SerializationOutputStream(OutputStream out) {
        super(out);
    }

    public void writeString(String field)
    throws IOException {
        byte[] strBytes = field.getBytes();
        writeField(strBytes);
    }

    public void writeField(byte[] field)
    throws IOException {
        writeInt(field.length);
        write(field);
    }

    public void writeCompressableField(byte[] field, boolean compress)
    throws IOException {

        writeBoolean(compress);

        if (compress) {
            ByteArrayOutputStream compressedBytes = new ByteArrayOutputStream();
            GZIPOutputStream gOut = new GZIPOutputStream(compressedBytes) {
                {
                    def.setLevel(compressionLevel);
                }
            };

            gOut.write(field);
            gOut.close();

            byte[] compressedArray = compressedBytes.toByteArray();
            writeField(compressedArray);
        } else {
            writeField(field);
        }
    }

    public void writeSerializable(ByteSerializable object)
    throws IOException {
        object.serialize(this);
    }

    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }
}
