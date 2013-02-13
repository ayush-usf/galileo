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
