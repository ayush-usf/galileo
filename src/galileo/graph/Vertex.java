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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * Provides a lightweight generic implementation of a graph vertex backed by a
 * TreeMap for extensibility.  This provides the basis of the hybrid
 * trees/graphs used in the system.
 *
 * @author malensek
 */
public class Vertex<L extends Comparable<L>, V> {

    protected L label;
    protected V value;
    protected TreeMap<L, Vertex<L, V>> edges = new TreeMap<>();

    public Vertex() { }

    public Vertex(L label) {
        this.label = label;
    }

    public Vertex(L label, V value) {
        this.label = label;
        setValue(value);
    }

    /**
     * Determines if two vertices are connected.
     *
     * @return true if the Vertex label is found on a connecting edge.
     */
    public boolean connectedTo(L label) {
        return edges.containsKey(label);
    }

    /**
     * Retrieve a neighboring Vertex.
     *
     * @param label Neighbor's label.
     *
     * @return Neighbor Vertex.
     */
    public Vertex<L, V> getNeighbor(L label) {
        return edges.get(label);
    }

    /**
     * Retrieve the labels of all neighboring vertices.
     *
     * @return Neighbor Vertex labels.
     */
    public Set<L> getNeighborLabels() {
        return edges.keySet();
    }

    /**
     * Traverse all edges to return all neighboring vertices.
     *
     * @return collection of all neighboring vertices.
     */
    public Collection<Vertex<L, V>> getAllNeighbors() {
        return edges.values();
    }

    /**
     * Connnects two vertices.  If this vertex is already connected to the
     * provided vertex label, then the already-connected vertex is returned, and
     * its <code>value</code> is updated.
     *
     * @param vertex The vertex to connect to.
     *
     * @return Connected vertex.
     */
    public Vertex<L, V> connect(Vertex<L, V> vertex) {
        L label = vertex.getLabel();
        Vertex<L, V> edge = getNeighbor(label);
        if (edge == null) {
            edges.put(label, vertex);
            return vertex;
        } else {
            edge.setValue(vertex.getValue());
            return edge;
        }
    }

    /**
     * Add and connect a collection of vertices in the form of a traversal path.
     */
    public void addPath(Iterator<Vertex<L, V>> path) {
        if (path.hasNext()) {
            Vertex<L, V> vertex = path.next();
            Vertex<L, V> edge = connect(vertex);
            edge.addPath(path);
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
    protected String toString(int indent) {
        String ls = System.lineSeparator();
        String str = "(" + getLabel() + "," + getValue() + ")" + ls;

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
