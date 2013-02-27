package galileo.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Path<L, V> implements Iterator<Vertex<L, V>> {

    private Queue<Vertex<L, V>> vertices = new LinkedList<>();

    public Path(Vertex<L, V>... vertices) {
        for (Vertex<L, V> vertex : vertices) {
            this.vertices.offer(vertex);
        }
    }

    @Override
    public boolean hasNext() {
        return (! vertices.isEmpty());
    }

    @Override
    public Vertex<L, V> next() {
        return vertices.poll();
    }

    @Override
    public void remove() {
        vertices.remove();
    }
}
