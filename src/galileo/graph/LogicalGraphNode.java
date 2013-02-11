
package galileo.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class LogicalGraphNode {
    private HashMap<String, LogicalGraphNode> edges
        = new HashMap<String, LogicalGraphNode>();

    private ArrayList<String> blockPaths
        = new ArrayList<String>();

    public void addNodePath(String nodePath, String blockPath) {
        String[] nodes = nodePath.split("/");
        Iterator<String> nodeIter = Arrays.asList(nodes).iterator();

        if (nodeIter.hasNext()) {
            linkNodes(nodeIter, blockPath);
        } else {
            //TODO This should NOT silently die here.  Fix.
        }
    }

    private void linkNodes(Iterator<String> nodes, String blockPath) {
        String nodeName = nodes.next();

        LogicalGraphNode nextNode = edges.get(nodeName);

        if (nextNode == null) {
            nextNode = new LogicalGraphNode();
            edges.put(nodeName, nextNode);
        }

        if (nodes.hasNext()) {
            nextNode.linkNodes(nodes, blockPath);
        } else {
            nextNode.addFilePath(blockPath);
        }
    }

    public void addFilePath(String blockPath) {
        blockPaths.add(blockPath);
    }

    /**
     * Query this node and any of its children.
     *
     * @param query
     *     Query string
     *
     * @param resultList
     *     ArrayList of logical graph nodes that results should be added to, if
     *     any.
     */
    public void subQuery(Iterator<String> query,
                         ArrayList<LogicalGraphNode> resultList) {
        if (!query.hasNext()) {
            resultList.add(this);
            for (LogicalGraphNode n : edges.values()) {
                n.subQuery(query, resultList);
            }
            return;
        }

        String nextNode = query.next();
        if (query.equals("*")) {
            for (LogicalGraphNode n : edges.values()) {
                n.subQuery(query, resultList);
            }
        }

        LogicalGraphNode node = edges.get(nextNode);
        if (node != null) {
            node.subQuery(query, resultList);
        }
    }

    public ArrayList<String> getBlockPaths() {
        return blockPaths;
    }
}
