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
