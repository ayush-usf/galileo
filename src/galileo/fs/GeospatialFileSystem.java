/*
Copyright (c) 2014, Colorado State University
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

package galileo.fs;

import java.io.File;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.dataset.Block;
import galileo.dataset.Metadata;
import galileo.graph.FeaturePath;
import galileo.graph.MetadataGraph;
import galileo.query.Query;
import galileo.serialization.SerializationException;
import galileo.serialization.Serializer;
import galileo.util.StackTraceToString;

public class GeospatialFileSystem extends FileSystem {

    private static final Logger logger = Logger.getLogger("galileo");

    private static final String metadataStore = "metadata.graph";

    public MetadataGraph metadataGraph;

    private GeospatialPhysicalGraph physicalGraph;

    public GeospatialFileSystem(String storageDirectory)
    throws FileSystemException, IOException, SerializationException {
        initialize(storageDirectory);

        File metaFile = new File(storageDirectory + "/" + metadataStore);
        if (metaFile.exists()) {
            metadataGraph = Serializer.restore(MetadataGraph.class, metaFile);
        } else {
            metadataGraph = new MetadataGraph();
        }
        physicalGraph = new GeospatialPhysicalGraph(storageDirectory);
    }

    public void storeBlock(Block block)
    throws FileSystemException, IOException {
        String physicalPath = physicalGraph.storeBlock(block);

        Metadata meta = block.getMetadata();
        FeaturePath<String> path = createPath(physicalPath, meta);

        try {
            metadataGraph.addPath(path);
        } catch (Exception e) {
            throw new FileSystemException("Error storing block: "
                    + e.getClass().getCanonicalName() + ":"
                    + System.lineSeparator() +
                    StackTraceToString.convert(e));
        }
    }

    protected FeaturePath<String> createPath(
            String physicalPath, Metadata meta) {

        FeaturePath<String> path = new FeaturePath<String>(
                physicalPath, meta.getAttributes().toArray());

        return path;
    }

    public MetadataGraph query(Query query) {
        return metadataGraph.evaluateQuery(query);
    }

    public void shutdown() {
        try {
            Serializer.persist(metadataGraph,
                    storageDirectory + "/" + metadataStore);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error writing persistent index file", e);
        }
    }
}
