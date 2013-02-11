
package galileo.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NodeTest {
    public static void main(String[] args) {
        Node n = new Node();
        Queue<Integer> vals = new LinkedList<Integer>();
        vals.add(5);
        vals.add(10);
        vals.add(3);
        vals.add(1);

        n.put(vals.iterator(), 22);

        List<Integer> query = new ArrayList<Integer>();
        query.add(5);
        query.add(10);
        query.add(3);
        query.add(1);

        System.out.println(n.get(query.iterator()));
        n.calc(0);
    }
}
