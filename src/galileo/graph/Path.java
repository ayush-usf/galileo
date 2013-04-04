package galileo.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Path<L extends Comparable<L>, V>
implements Iterable<Vertex<L, V>> {

    private List<Vertex<L, V>> vertices = new ArrayList<>();

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
