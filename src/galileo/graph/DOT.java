
package galileo.graph;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Provides functionality for visualizing graphs using Graphviz.  This class
 * outputs the graphs in DOT format.
 *
 * @author malensek
 */
public class DOT {

    /**
     * Traverses the graph provided by a root vertex, writing out the graph
     * representation in DOT format.
     *
     * @param vertex root vertex of the graph to convert
     * @param out OutputStream to write the graph to.
     */
    public static void toDOT(Vertex<?, ?> vertex, OutputStream out) {
        DOT.toDOT(vertex, "Untitled", out);
    }

    /**
     * Traverses the graph provided by a root vertex, writing out the graph
     * representation in DOT format.
     *
     * @param vertex root vertex of the graph to convert
     * @param title title of the graph
     * @param out OutputStream to write the graph to.
     */
    public static void toDOT(Vertex<?, ?> vertex, String title,
            OutputStream out) {

        PrintStream ps = new PrintStream(out);
        writeGraphHeader(title, ps);
        writeVertex(vertex, ps);
        writeGraphFooter(ps);
    }

    /**
     * Writes a vertex's information to a DOT file.
     */
    private static void writeVertex(Vertex<?, ?> v, PrintStream ps) {
        ps.println("\"" + v.hashCode() + "\""
                + " [label=\"" + v.getLabel() + "\"];");

        for (Vertex<?, ?> child : v.getAllNeighbors()) {
            connect(v.hashCode(), child.hashCode(), ps);
        }

        for (Vertex<?, ?> child : v.getAllNeighbors()) {
            writeVertex(child, ps);
        }
    }

    /**
     * Connects two vertices in DOT format (v1 -- v2).
     *
     * @param from source vertex.  Generally the hashCode() of the object can be
     * used as a unique identifier for the vertex.
     * @param to destination vertex.  In general, use hashCode().
     */
    private static void connect(int from, int to, PrintStream ps) {
        ps.println("\"" + from + "\" -- \"" + to + "\"");
    }

    /**
     * Writes graph header information, such as the graph title and settings.
     *
     * @param title Graph title
     */
    private static void writeGraphHeader(String title, PrintStream ps) {
        ps.println("graph " + title + " {");
        ps.println("node [fontname=\"Helvetica\" fontsize=\"20\"]");
    }

    /**
     * Write any concluding DOT format information.  This includes the closing
     * brace for the graph.
     */
    private static void writeGraphFooter(PrintStream ps) {
        ps.println("}");
    }

}
