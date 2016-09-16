package edu.colostate.cs.galileo.adapters;

import edu.colostate.cs.galileo.graph2.Vertex;
import edu.colostate.cs.galileo.serialization.Serializer;

public class ReadVertexBundle {

    public static void main(String[] args) throws Exception {

        Vertex root = Serializer.restoreCompressed(Vertex.class, args[0]);
        System.out.println(root.numLeaves());
        System.out.println(root.numDescendants());
        System.out.println(root.numDescendantEdges());

    }
}
