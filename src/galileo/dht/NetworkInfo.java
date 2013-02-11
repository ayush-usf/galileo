
package galileo.dht;

import java.util.ArrayList;
import java.util.List;

public class NetworkInfo {

    private List<GroupInfo> groups = new ArrayList<>();

    public void addGroup(GroupInfo group) {
        groups.add(group);
    }

    @Override
    public String toString() {
        String str = "Network Information:" + System.lineSeparator();
        for (GroupInfo group : groups) {
            str += group;
        }

        return str;
    }
}
