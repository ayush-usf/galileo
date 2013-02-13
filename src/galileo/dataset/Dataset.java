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
