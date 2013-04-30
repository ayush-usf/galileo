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
import java.util.List;

import galileo.dataset.Feature;

/**
 * Tracks a {@link galileo.query.Query} as it traverses through a graph
 * hierarchy.
 *
 * @author malensek
 */
public class HierarchicalQueryTracker<T> {

    private List<List<Vertex<Feature, T>>> results = new ArrayList<>();
    private int skip = 0;
    private int farthestDepth = 0;

    public HierarchicalQueryTracker(Vertex<Feature, T> root) {
        List<Vertex<Feature, T>> l = new ArrayList<>(1);
        l.add(root);
        results.add(l);
    }

    public void addResults(List<Vertex<Feature, T>> results) {
        this.results.add(results);
    }

    public int getCurrentLevel() {
        return results.size();
    }

    public List<Vertex<Feature, T>> getCurrentResults() {
        return results.get(getCurrentLevel() - 1);
    }

    public void skipLevel() {
        if (getCurrentLevel() == skip) {
            skip++;
        }
    }

    public void addLevel() {
        farthestDepth = getCurrentLevel();
    }

    public List<Vertex<Feature, T>> getQueryResults() {

        return results.get(farthestDepth);
    }

    @Override
    public String toString() {
        String s = "";
        int level = skip;

        for (int i = skip; i < results.size(); ++i) {
            s += "Level: " + level + System.lineSeparator();
            level++;

            for (Vertex<Feature, T> result : results.get(i)) {
                s += result.getLabel() + System.lineSeparator();
            }
        }

        System.out.println(skip);
        System.out.println(farthestDepth);
        return s;
    }
}
