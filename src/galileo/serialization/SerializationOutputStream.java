
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
