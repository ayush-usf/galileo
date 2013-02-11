
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
