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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a graph path.  A path contains an arbitrary number of vertices,
 * and can optionally have a value payload for storing items within a graph.
 *
 * @author malensek
 */
public class Path<L extends Comparable<L>, V>
implements Iterable<Vertex<L, V>> {

    private V value;
    private List<Vertex<L, V>> vertices = new ArrayList<>();

    /**
     * Create a Path with a value payload an a number of vertices pre-populated.
     */
    @SafeVarargs
    public Path(V value, Vertex<L, V>... vertices) {
        setValue(value);

        for (Vertex<L, V> vertex : vertices) {
            this.vertices.add(vertex);
        }
    }

    /**
     * Create a Path with a number of vertices pre-populated.
     */
    @SafeVarargs
    public Path(Vertex<L, V>... vertices) {
        for (Vertex<L, V> vertex : vertices) {
            this.vertices.add(vertex);
        }
    }

    public int size() {
        return vertices.size();
    }

    public void add(Vertex<L, V> vertex) {
        vertices.add(vertex);
    }

    public void add(L label, V value) {
        vertices.add(new Vertex<>(label, value));
    }

    public void remove(int index) {
        vertices.remove(index);
    }

    public Vertex<L, V> get(int index) {
        return vertices.get(index);
    }

    /**
     * Retrieve a list of the {@link Vertex} labels in this Path.
     */
    public List<L> getLabels() {
        List<L> labels = new ArrayList<>();
        for (Vertex<L, V> vertex : vertices) {
            labels.add(vertex.getLabel());
        }

        return labels;
    }

    public void sort(Comparator<? super Vertex<L, V>> c) {
        Collections.sort(vertices, c);
    }

    /**
     * Sets the value payload for this Path.
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Retrieves the value payload for this Path.
     */
    public V getValue() {
        return this.value;
    }

    @Override
    public Iterator<Vertex<L, V>> iterator() {
        return vertices.iterator();
    }

    @Override
    public String toString() {
        String str = "";
        for (Vertex<L, V> vertex : vertices) {
            str += vertex;
        }

        return str;
    }
}
