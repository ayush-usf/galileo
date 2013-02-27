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

import java.util.Iterator;
import java.util.TreeMap;

public class Vertex<L, V> {

    private L label;
    private V value;
    private TreeMap<L, Vertex<L, V>> edges = new TreeMap<>();

    public Vertex() { }

    public Vertex(L label, V value) {
        this.label = label;
        this.value = value;
    }

    public boolean hasVertex(L label) {
        return edges.containsKey(label);
    }

    public Vertex<L, V> getVertex(L label) {
        return edges.get(label);
    }

    public Vertex<L, V> addVertex(Vertex<L, V> vertex) {
        L label = vertex.getLabel();
        Vertex<L, V> edge = edges.get(label);
        if (edge == null) {
            edges.put(label, vertex);
            return vertex;
        } else {
            edge.setValue(vertex.getValue());
            return edge;
        }
    }

    public void addPath(Iterator<Vertex<L, V>> path) {
        if (path.hasNext()) {
            Vertex<L, V> vertex = path.next();
            Vertex<L, V> edge = addVertex(vertex);
            edge.addPath(path);
        }
    }

    public Vertex<L, V> traversePath(Iterator<L> iter)
    throws GraphException, VertexNotFoundException {
        if (!iter.hasNext()) {
            throw new GraphException("Attempted to traverse empty path.");
        }

        L label = iter.next();
        Vertex<L, V> vertex = edges.get(label);
        if (vertex == null) {
            throw new VertexNotFoundException("Vertex not found: \n" +
                    value.toString());
        }

        if (iter.hasNext()) {
            return vertex.traversePath(iter);
        } else {
            return this;
        }
    }

    public L getLabel() {
        return label;
    }

    public void setLabel(L label) {
        this.label = label;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Pretty-print this vertex (and its children) with a given indent level.
     */
    private String toString(int indent) {
        String str = "(" + getLabel() + "," + getValue() + ")\n";

        String space = " ";
        for (int i = 0; i < indent; ++i) {
            space += "|  ";
        }
        space += "|-";
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
