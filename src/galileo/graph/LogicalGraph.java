
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
