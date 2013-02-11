
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
