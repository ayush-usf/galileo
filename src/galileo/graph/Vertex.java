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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

public class Vertex<L, V> {

    public static int nodeCounter = 0;

    private GraphElement<L, V> element;
    private Map<L, Vertex<L, V>> edges = new HashMap<L, Vertex<L, V>>();

    public Vertex() { }

    public Vertex(GraphElement<L, V> element) {
        this.element = element;
    }

    public boolean hasChild(L label) {
        return edges.containsKey(label);
    }

    public Vertex<L, V> getChild(L label) {
        return edges.get(label);
    }

    public Vertex<L, V> addChild(GraphElement<L, V> element) {
        Vertex<L, V> vertex = edges.get(element.label);
        if (vertex == null) {
            nodeCounter++;
            vertex = new Vertex<L, V>(element);
            edges.put(element.label, vertex);
        }

        return vertex;
    }

//    public Vertex<L, V> traversePath(Iterator<L> iter)
//    throws GraphException, VertexNotFoundException {
//        if (!iter.hasNext()) {
//            throw new GraphException("Attempted to traverse empty path.");
//        }
//
//        V value = iter.next();
//        Vertex<L, V> vertex = edges.get(value);
//        if (vertex == null) {
//            throw new VertexNotFoundException("Vertex not found: \n" +
//                    value.toString());
//        }
//
//        if (iter.hasNext()) {
//            return vertex.traversePath(iter);
//        } else {
//            return this;
//        }
//    }
//

    public GraphElement<L, V> getElement() {
        return element;
    }

    public L getLabel() {
        return element.label;
    }

    public V getValue() {
        return element.value;
    }

    private String toString(int indent) {
        String str = element.label + ":\n";

        String space = "";
        for (int i = 0; i <= indent; ++i) {
            space += " ";
        }
        ++indent;

        for (Vertex<L, V> vertex : edges.values()) {
            str += space + vertex.toString(indent);
        }

        return str;
    }

    @Override
    public String toString() {
        return toString(0);
    }
}
