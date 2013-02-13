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
