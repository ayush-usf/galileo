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

package galileo.graph;

import java.io.BufferedReader;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import galileo.dataset.BlockMetadata;

import galileo.fs.Journal;
import galileo.util.GeoHash;

public class LogicalGraph {
    private LogicalGraphNode rootNode = new LogicalGraphNode();
    private Journal journal = Journal.getInstance();

    /**
     * Add a block to the logical graph representation.
     *
     * @param metadata
     *     <code>BlockMetadata</code> for the block being added to the graph.
     *
     * @param blockPath
     *     String that contains the block's physical location on disk.
     */
    public void addBlock(BlockMetadata metadata, String blockPath) {
        String nodePath = getGraphPath(metadata);

        try {
            journal.writeEntry(nodePath + "," + blockPath);
        } catch (IOException e) {
            System.out.println("Could not write to journal!");
            e.printStackTrace();
        }

        rootNode.addNodePath(nodePath, blockPath);
    }

    public void recoverFromJournal() {
        long numEntries = 0;
        long recoveryStart = System.nanoTime();
        System.out.print("Recovering metadata from journal...  ");

        try {
            BufferedReader reader = journal.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] nodeParts = line.split(",");
                rootNode.addNodePath(nodeParts[0], nodeParts[1]);
                ++numEntries;
            }
        } catch (IOException e) {
            System.out.println("Error reading journal!");
            e.printStackTrace();
        }

        long recoveryEnd = System.nanoTime();
        System.out.println("done.");
        System.out.println("Recovered " + numEntries + " entries in "
            + (recoveryEnd - recoveryStart) * 1E-6 + "ms.");
    }

    /**
     * Query the logical graph.
     *
     * Queries drill down through the graph.  Frontslashes ('/') are used as the
     * separator between sub-nodes.
     *
     * @param query
     *     String representing the query.
     *
     * @return array of logical graph nodes matching the query.
     */
    public LogicalGraphNode[] query(String query) {
        /* Remove leading slash, if present.  Trailing slashes are already
         * ignored by String.split(). */
        if (query.startsWith("/")) {
            query = query.substring(1);
        }

        String[] nodes = query.split("/");
        Iterator<String> queryIter = Arrays.asList(nodes).iterator();
        ArrayList<LogicalGraphNode> resultList
            = new ArrayList<LogicalGraphNode>();

        rootNode.subQuery(queryIter, resultList);

        LogicalGraphNode[] resultArray
            = new LogicalGraphNode[resultList.size()];
        resultList.toArray(resultArray);

        return resultArray;
    }

    private String getGraphPath(BlockMetadata metadata) {
        String path = "";
        Date blockDate = metadata.getTemporalRange().getLowerBound();

        /* Date */
        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.applyPattern("yyyy/M/d/H/");
        path = formatter.format(blockDate);

        /* GeoHash */
        path += GeoHash.encode(metadata.getSpatialRange(), 2);

        return path;
    }
}
