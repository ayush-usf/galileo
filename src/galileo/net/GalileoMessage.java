
package galileo.net;

import java.io.IOException;

import java.nio.channels.SelectionKey;

import galileo.serialization.ByteSerializable;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

/**
 * The unit of data transmission in the Galileo DHT.  These packets are simple
 * in structure, containing a size prefix followed by the packet payload.
 *
 * @author malensek
 */
public class GalileoMessage implements ByteSerializable {

    private byte[] payload;

    public SelectionKey key;

    /**
     * Constructs a GalileoMessage from an array of bytes.
     *
     * @param payload message payload in the form of a byte array.
     */
    public GalileoMessage(byte[] payload) {
        this.payload = payload;
    }

    /**
     * Constructs a GalileoMessage from an array of bytes with an associated
     * {@link SelectionKey} of the message source.
     *
     * @param payload message payload in the form of a byte array.
     * @param key SelectionKey of the message source.
     */
    public GalileoMessage(byte[] payload, SelectionKey key) {
        this.payload = payload;
        this.key = key;
    }

    /**
     * Retrieves the payload for this GalileoMessage.
     *
     * @return the GalileoMessage payload
     */
    public byte[] getPayload() {
        return payload;
    }

    public SelectionKey getSelectionKey() {
        return key;
    }

    /**
     * Constructs a new GalileoMessage from a serialization stream.
     */
    public GalileoMessage(SerializationInputStream in)
    throws IOException {
        int dataSize = in.readInt();
        payload = new byte[dataSize];
        in.read(payload);
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeInt(payload.length);
        out.write(payload);
    }
}
