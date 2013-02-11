
package galileo.dataset;

import java.io.IOException;

import galileo.serialization.ByteSerializable;
import galileo.serialization.SerializationException;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;
import galileo.serialization.Serializer;

public class FileBlock implements ByteSerializable {
    private byte[] data;
    private BlockMetadata metadata;

    /**
     * Construct a <code>FileBlock</code> (including metadata) from a
     * byte array of data and a <code>BlockMetadata</code> object.
     *
     * @param data
     *     Data for the new FileBlock
     *
     * @param metadata
     *     Metadata for the new FileBlock
     */
    public FileBlock(byte[] data, BlockMetadata metadata) {
        this.data = data;
        this.metadata = metadata;
    }

    /**
     * Get this FileBlock's metadata.
     *
     * @return BlockMetadata
     */
    public BlockMetadata getMetadata() {
        return metadata;
    }

    /**
     * Get the data portion of the <code>FileBlock</code>.
     *
     * @return byte array containing the FileBlock's data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Construct a <code>FileBlock</code> from separate data, metadata streams.
     *
     * @param data
     *     Data for the new FileBlock
     *
     * @param metadata
     *     Metadata stream for the new FileBlock
     */
    public FileBlock(byte[] data, byte[] metadata)
    throws IOException, SerializationException {
        this.data = data;
        this.metadata =
            Serializer.deserialize(BlockMetadataImpl.class, metadata);
    }

    /**
     * Construct a complete FileBlock (including metadata) from a byte stream.
     *
     * @param in Stream to construct the FileBlock from.
     */
    public FileBlock(SerializationInputStream in)
    throws IOException {
        metadata = new BlockMetadataImpl(in);
        data = in.readField();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        metadata.serialize(out);
        out.writeField(data);
    }
}
