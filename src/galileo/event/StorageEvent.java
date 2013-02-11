
package galileo.event;

import java.io.IOException;

import galileo.dataset.FileBlock;

import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

/**
 * Represents a client request for storage at a DHT {@link StorageNode}.
 */
public class StorageEvent implements GalileoEvent {

    private FileBlock block;

    public StorageEvent(FileBlock block) {
        this.block = block;
    }

    public FileBlock getBlock() {
        return block;
    }

    @Override
    public EventType getType() {
        return EventType.STORAGE;
    }

    /**
     * Deserializes a Storage event.
     */
    public StorageEvent(SerializationInputStream in)
    throws IOException {
        block = new FileBlock(in);
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        block.serialize(out);
    }
}
