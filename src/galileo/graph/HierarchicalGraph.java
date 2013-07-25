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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import java.util.logging.Logger;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

import galileo.dataset.Feature;
import galileo.dataset.FeatureType;
import galileo.dataset.NullFeature;

import galileo.query.Expression;
import galileo.query.Operation;
import galileo.query.Operator;
import galileo.query.Query;

/**
 * A type-aware hierarchical graph implementation with each type occupying a
 * level in the hierarchy.
 *
 * @author malensek
 */
public class HierarchicalGraph<T> {

    private static final Logger logger = Logger.getLogger("galileo");

    private Vertex<Feature, T> root = new Vertex<>();
    private Map<String, Integer> featureOrder = new HashMap<>();
    private Queue<String> features = new LinkedList<>();

    public void evaluateQuery(Query q) {
        HierarchicalQueryTracker<T> tracker
            = new HierarchicalQueryTracker<>(root);

        for (String feature : features) {
            List<Operation> operations = q.getOperations(feature);
            List<Vertex<Feature, T>> results
                = evaluateOperations(operations, tracker);
            tracker.addResults(results);
        }
        System.out.println(tracker);

        System.out.println("Results: --- ");
        for (Vertex<Feature, T> result : tracker.getQueryResults()) {
            System.out.println(result);
        }
    }

    public List<Vertex<Feature, T>> evaluateOperations(
            List<Operation> operations, HierarchicalQueryTracker<T> tracker) {

        List<Vertex<Feature, T>> results = new ArrayList<>();

        if (operations == null) {
            tracker.skipLevel();

            // traverse all neighbors
            for (Vertex<Feature, T> vertex : tracker.getCurrentResults()) {
                results.addAll(vertex.getAllNeighbors());
            }
        } else {
            tracker.addLevel();

            for (Operation op : operations) {
                for (Vertex<Feature, T> vertex : tracker.getCurrentResults()) {
                    Collection<Vertex<Feature, T>> resultCollection
                        = evaluateExpressions(op.getExpressions(), vertex);
                    results.addAll(resultCollection);
                }
            }
        }

        return results;
    }

    private Collection<Vertex<Feature, T>> evaluateExpressions(
            List<Expression> expressions, Vertex<Feature, T> vertex) {

        Set<Vertex<Feature, T>> resultSet
            = new HashSet<>(vertex.getAllNeighbors());

        for (Expression expression : expressions) {
            if (expression.operator == Operator.EQUAL) {
                Vertex<Feature, T> neighbor
                    = vertex.getNeighbor(new Feature("", expression.value));
                Set<Vertex<Feature, T>> neighborSet = new HashSet<>();
                neighborSet.add(neighbor);
                resultSet.retainAll(neighborSet);
            }
        }

        return resultSet;
    }

    public void addPath(Path<Feature, T> path) {
        addNullFeatures(path);
        reorientPath(path);
        optimizePath(path);

        /* Place the path value (traversal result) at the end of this path. */
        path.get(path.size() - 1).setValue(path.getValue());

        root.addPath(path.iterator());
    }

    /**
     * For missing feature values, add a null feature to a path.  This maintains
     * the graph structure for sparse schemas or cases where a feature reading
     * is not available.
     */
    private void addNullFeatures(Path<Feature, T> path) {
        Set<String> knownFeatures = new HashSet<>(featureOrder.keySet());
        Set<String> pathFeatures = new HashSet<>();
        for (Feature feature : path.getLabels()) {
            pathFeatures.add(feature.getName());
        }

        knownFeatures.removeAll(pathFeatures);

        /* Create null features for missing values */
        for (String featureName : knownFeatures) {
            Vertex<Feature, T> v = new Vertex<>();
            v.setLabel(new NullFeature(featureName));
            path.add(v);
        }
    }

    /**
     * Reorients a nonhierarchical path in place to match the current graph
     * hierarchy.
     */
    private void reorientPath(Path<Feature, T> path) {
        path.sort(new Comparator<Vertex<Feature, T>>() {
            public int compare(Vertex<Feature, T> a, Vertex<Feature, T> b) {
                int o2 = getOrder(b.getLabel());
                int o1 = getOrder(a.getLabel());
                return o1 - o2;
            }
        });
    }

    /**
     * Perform optimizations on a path to reduce the number of vertices inserted
     * into the graph.
     */
    private void optimizePath(Path<Feature, T> path) {
        /* Remove all trailing null features.  During a traversal, trailing null
         * features are unnecessary to traverse. */
        for (int i = path.size() - 1; i >= 0; --i) {
            if (path.get(i).getLabel().getType() == FeatureType.NULL) {
                path.remove(i);
            } else {
                break;
            }
        }
    }

    /**
     * Determines the numeric order of a Feature based on the current
     * orientation of the graph.  For example, humidity features may come first,
     * followed by temperature, etc.  If the feature in question has not yet
     * been added to the graph, then it is connected to the current leaf nodes,
     * effectively placing it at the bottom of the hierarchy, and its order
     * number is set to the current number of feature types in the graph.
     *
     * @return int representing the list ordering of the Feature
     */
    private int getOrder(Feature feature) {
        Integer order = featureOrder.get(feature.getName());
        if (order == null) {
            order = addNewFeature(feature.getName());
        }

        return order;
    }

    private int addNewFeature(String feature) {
        logger.info("New feature: " + feature);
        Integer order = featureOrder.keySet().size();
        featureOrder.put(feature, order);
        features.offer(feature);

        return order;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
