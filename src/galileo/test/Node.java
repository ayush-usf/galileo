
package galileo.test;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Node implements Serializable {
    public static int leafCounter = 0;
    public static int fullLeaves = 0;
    public static int nodeCounter = 0;

    public Map<Integer, Node> children = new HashMap<Integer, Node>();
    public List<Integer> machines = new ArrayList<Integer>();
    private Integer value = null;

    public int ctr;

    public Node() { }

    public Node(int value) {
        this.value = value;
    }

    public void put(Iterator<Integer> it, int machine) {
        if (!machines.contains(machine)) {
            machines.add(machine);
        }

        if (it.hasNext()) {

            int i = it.next();
            Node child = children.get(i);
            if (child == null) {
                nodeCounter++;
                child = new Node(i);
                children.put(i, child);
            }
            child.ctr++;
            child.put(it, machine);
        }
    }

    public Integer[] get(Iterator<Integer> it) {
        if (it.hasNext()) {
            int i = it.next();
            //System.out.println(i);
            Node child = children.get(i);
            if (child == null) {
                //System.out.println("--");
                for (int x : children.keySet()) {
                    //System.out.println(x);
                }
                return null;
            }

            if (child.machines.size() == 0) {
                return this.get(it);
            }

            return child.get(it);
        }

        Integer[] result = new Integer[machines.size()];
        machines.toArray(result);
        return result;
    }

    public void calc(int level) {
        System.out.print(level + " : " + machines.size() + " ");
        int num = 0;
        for (int i : machines) {
            if (i >= 8 && i <= 15) {
                num ++;
            }
        }
        System.out.println(num);
        level++;
        for (Node n : children.values()) {
            n.calc(level);
        }
    }

    public void findLeaf() {
        if (children.size() > 0) {
            for (Node n : children.values()) {
                if (n.children.size() == 0) {
                    leafCounter++;
                    if (machines.size() >= 40) {
                        fullLeaves++;
                    } else {
                        //System.out.println(this.hashCode() + ":" + machines.size());
//                        String list = "";
//                        for (int i : machines) {
//                            list += i + ",";
//                        }
//                        System.out.println(this.hashCode() + ":" + list);
                    }
                } else {
                    n.findLeaf();
                }
            }
        }
    }

    public int edges() {
        int numEdges = 0;
        numEdges += machines.size();
        for (Node child : children.values()) {
            numEdges += child.edges();
        }
        return numEdges;
    }

    @Override
    public String toString() {
        if (machines.size() == 0) {
            return "";
        }
        String str = "";
        for (Node node : children.values()) {
            str += " " + node;
        }
        return str + this.hashCode() + ":" + machines.size() + "\n";
    }
}
