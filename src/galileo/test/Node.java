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

package galileo.test;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Node implements Serializable {

    private static final long serialVersionUID = -8845938019887421925L;

    public static int leafCounter = 0;
    public static int fullLeaves = 0;
    public static long nodeCounter = 0;

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

            if (i == -1) {
                for (Node n : children.values()) {
                    n.get(it);
                }
                Integer[] x = new Integer[1];
                x[0] = 0;
                return x;
            }

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
