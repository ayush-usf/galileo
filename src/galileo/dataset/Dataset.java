
package galileo.dataset;

import java.util.Iterator;

public interface Dataset {

    /**
     * Retrieve the identifier for this dataset
     *
     * @return
     */
    public String getDatasetIdentifier();

    public TemporalRange getTemporalRange();

    public SpatialRange getSpatialRange();

    public Iterator<String> getListOfDevices();

    public Iterator<Feature> getListOfFeatures();


    /** This should be <code>GraphNode</code> or something along those lines.
    public LinkedList<String> getTraversalStack();


    /**
     * This view type will be a constant that is specified in the Views
     * interface
     *
     * @param viewType
     */
    public void orientView(int viewType);

    /**
     * If possible generate finer grained resolutions at that particular node in
     * the graph. Would probably be valid only for the space and time views.
     */
    public void finerGrainedResolution();

    /**
     * We will probably have different ideas about resolution depending on
     * whether we are talking about space, time, metrics and such.
     */
    public void coarserGrainedResolution();

    /** Allows a user to determine when the dataset was last synchronized. */
    public long timestampWhenLastSynchronized();

    /** Attempt to retrieve all the file blocks that might be available.
     * Callback functions for synchronizing with the nodes that are available. */
    public void synchronizeDataset();


    public void setDatasetModificationsListener(DatasetModificationsListener listener);


    public Bookmark setBookmark();

    public void revertToBookmarkedLocation(Bookmark bookmark);



    public String getViewSoFar();


    public void resetView();

    public void resetView(int level);

    public void getPointer();

    public SpatialNode select();


    public SpatialNode getSpatialNode();


    public long getDatasetSizeInMB();


}
