
package galileo.dataset;

public interface DatasetModificationsListener {
    /**
     * Invoked after a node of view tree has changed. The node has not
     * changed the location in the view tree but attributes are changed.
     * The change is described in ViewEvent e. User e.getPath() to get the
     * parent node of the updated node. e.getChildIndices() will return
     * the array of indices for the modified children nodes.
     */

    public void onNodesChanged(String datasetIdentifier);

    /**
     * Invoked after nodes have been inserted into the view tree.
     * Use e.getPath() to get the parent of the new node(s). e.getChildIndices() will
     * return the indices of the newly added nodes in ascending order.
     */
    public void onNodesInserted(String datasetIdentifier);

    /**
     * Invoked after nodes have been removed from the view tree.
     * e.getPath() will return previous parent of the deleted node(s). e.getChildIndices()
     * will return the indices of deleted node had before being deleted.
     */

    public void onNodesRemoved(String datasetIdentifier);
}
