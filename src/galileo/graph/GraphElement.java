
package galileo.graph;

public class GraphElement<L, V> {
    public final L label;
    public final V value;

    public GraphElement(L label, V value) {
        this.label = label;
        this.value = value;
    }
}
