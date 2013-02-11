
package galileo.dataset;

import galileo.serialization.ByteSerializable;

public interface RuntimeMetadata extends ByteSerializable {

    /** Retrieve the storage node identifier for the node currently hosting
     * associated metadata or blocks.
     *
     * @return String with the storage node UUID.
     */
    public String getStorageNodeIdentifier();

    /** Set the storage node identifier for this metadata.
     *
     * @param identifier String containing the storage node UUID.
     */
    public void setStorageNodeIdentifier(String identifier);

    /**
     * Retrieve the location of this Metadata on disk.
     * The file path is relative to $GALILEO_ROOT so if the root is moved the
     * paths should still be valid.
     *
     * @return String with the location of the Metadata.
     */
    public String getPhysicalGraphPath();

    /**
     * Set the physical graph path for this metadata.
     *
     * @param path The path of the data in the physical graph.
     */
    public void setPhysicalGraphPath(String path);
}
