
package galileo.dht;

import java.util.ArrayList;
import java.util.List;

public class GroupInfo {

    private String name;

    List<NodeInfo> nodes = new ArrayList<>();

    public GroupInfo(String name) {
        this.name = name;
    }

    public void addNode(NodeInfo node) {
        nodes.add(node);
    }

    @Override
    public String toString() {
        String str = "Group: " + name + System.lineSeparator();
        for (NodeInfo node : nodes) {
            str += "    " + node + System.lineSeparator();
        }
        return str;
    }
}
