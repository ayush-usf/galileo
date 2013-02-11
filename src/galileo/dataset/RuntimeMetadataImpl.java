
package galileo.dataset;

import java.io.IOException;

import galileo.serialization.ByteSerializable;

import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class RuntimeMetadataImpl implements RuntimeMetadata, ByteSerializable {

    private String storageNodeIdentifier = "";
    private String physicalGraphPath = "";

    public RuntimeMetadataImpl() {

    }

    public RuntimeMetadataImpl(String storageNodeIdentifier,
                               String physicalGraphPath) {

        this.storageNodeIdentifier = storageNodeIdentifier;
        this.physicalGraphPath = physicalGraphPath;
    }

    @Override
    public String getStorageNodeIdentifier() {
        return storageNodeIdentifier;
    }

    @Override
    public void setStorageNodeIdentifier(String identifier) {
        storageNodeIdentifier = identifier;
    }

    @Override
    public String getPhysicalGraphPath() {
        return physicalGraphPath;
    }

    @Override
    public void setPhysicalGraphPath(String path) {
        physicalGraphPath = path;
    }

    public RuntimeMetadataImpl(SerializationInputStream in)
    throws IOException {
        storageNodeIdentifier = new String(in.readField());
        System.out.println(storageNodeIdentifier);
        physicalGraphPath = new String(in.readField());
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeField(storageNodeIdentifier.getBytes());
        out.writeField(physicalGraphPath.getBytes());
    }
}
