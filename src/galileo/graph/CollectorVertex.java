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

import java.util.HashSet;
import java.util.Set;

/**
 * Vertex that "collects" incoming values by appending them to a set rather than
 * overwriting the old value.
 *
 * @author malensek
 */
public class CollectorVertex<L extends Comparable<L>, V> extends Vertex<L, V> {

    private Set<V> values = new HashSet<>();

    public CollectorVertex(L label) {
        this.label = label;
    }

    public CollectorVertex(L label, V value) {
        this.label = label;
        this.value = value;
        setValue(value);
    }

    public Set<V> getValues() {
        return values;
    }

    @Override
    public void setValue(V value) {
        if (value != null) {
            values.add(value);
        }
    }

    @Override
    protected String toString(int indent) {
        String ls = System.lineSeparator();
        String valueStr = "";
        for (V value : values) {
            valueStr += value + ",";
        }
        String str = "(" + getLabel() + ", [" + valueStr + "])" + ls;

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
}
