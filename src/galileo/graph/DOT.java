
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

    private static void connect(int from, int to, PrintStream ps) {
        ps.println("\"" + from + "\" -- \"" + to + "\"");
    }

    private static void writeGraphHeader(String title, PrintStream ps) {
        ps.println("graph " + title + " {");
        ps.println("node [fontname=\"Helvetica\" fontsize=\"20\"]");
    }

    private static void writeGraphFooter(PrintStream ps) {
        ps.println("}");
    }

}
