
package galileo.serialization;

import java.io.IOException;

/**
 * Describes an interface for classes that can be serialized to portable
 * byte form.
 */
public interface ByteSerializable {

    /**
     * Serializes this object to binary form by passing it through a
     * serialization stream.
     *
     * @param out stream to serialize to.
     */
    public void serialize(SerializationOutputStream out) throws IOException;
}
