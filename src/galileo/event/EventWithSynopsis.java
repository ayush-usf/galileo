package galileo.event;

import java.io.IOException;

import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

/**
 * Encapsulates a raw (byte[] based) event that includes a String representing
 * the event synopsis.  This can be used to essentially 'tag' particular blobs
 * of data without writing specific events.
 *
 * @author malensek
 */
public class EventWithSynopsis implements Event {

    private String synopsis;
    private byte[] data;
    private boolean compress = false;

    public EventWithSynopsis(String synopsis, byte[] data) {
        this.synopsis = synopsis;
        this.data = data;
    }

    /**
     * Enables compression when serializing this event.  When deserializing,
     * this setting has no effect.
     */
    public void enableCompression() {
        this.compress = true;
    }

    /**
     * Disables compression when serializing this event.  This is the default
     * behavior.  When deserializing, this setting has no effect.
     */
    public void disableCompression() {
        this.compress = false;
    }

    @Deserialize
    public EventWithSynopsis(SerializationInputStream in)
    throws IOException {
        this.synopsis = in.readString();
        this.data = in.readCompressableField();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeString(synopsis);
        out.writeCompressableField(data, compress);
    }
}
