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

import galileo.dataset.feature.Feature;
import galileo.dataset.feature.FeatureType;

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

    /** The root vertex. */
    private Vertex<Feature, T> root = new Vertex<>();

    /** Describes each level in the hierarchy. */
    private Map<String, Level> levels = new HashMap<>();

    /**
     * We maintain a separate Queue with Feature names inserted in
     * hierarchical order.  While levels.keySet() contains the same information,
     * there is no contractual obligation for HashMap to return the keyset in
     * the original insertion order (although in practice, it probably does).
     */
    private Queue<String> features = new LinkedList<>();


    /**
     * Tracks information about each level in the graph hierarchy.
     */
    private class Level {

        public Level(int order, FeatureType type) {
            this.order = order;
            this.type = type;
        }

        public int order;
        public FeatureType type;
    }

    public HierarchicalGraph() { }

    /**
     * Creates a HierarchicalGraph with a set Feature hierarchy.  Features are
     * entered into the hierarchy in the order they are received.  Feature
     * values are not inspected by this constructor.
     *
     * @param featureOrdering a List of Features that represents how the
     * hierarchy in the graph should be built.
     */
    public HierarchicalGraph(List<Feature> featureOrdering) {
        for (Feature feature : featureOrdering) {
            getOrder(feature);
        }
    }

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

            /* Traverse all neighbors */
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

    /**
     * Adds a new {@link Path} to the Hierarchical Graph.
     */
    public void addPath(Path<Feature, T> path, T payload)
    throws FeatureTypeMismatchException {
        checkFeatureTypes(path);
        addNullFeatures(path);
        reorientPath(path);
        optimizePath(path);

        /* Place the path payload (traversal result) at the end of this path. */
        path.get(path.size() - 1).addValue(payload);

        root.addPath(path.iterator());
    }

    /**
     * This method ensures that the Features in the path being added have the
     * same FeatureTypes as the current hierarchy.  This ensures that different
     * FeatureTypes (such as an int and a double) get placed on the same level
     * in the hierarchy.
     *
     * @param path the Path to check for invalid FeatureTypes.
     *
     * @throws FeatureTypeMismatchException if an invalid type is found
     */
    private void checkFeatureTypes(Path<Feature, T> path)
    throws FeatureTypeMismatchException {
        for (Feature feature : path.getLabels()) {

            /* If this feature is NULL, then it's effectively a wildcard. */
            if (feature.getType() == FeatureType.NULL) {
                continue;
            }

            Level level = levels.get(feature.getName());
            if (level != null) {
                if (level.type != feature.getType()) {
                    throw new FeatureTypeMismatchException(
                            "Feature insertion at graph level " + level.order
                            + " is not possible due to a FeatureType mismatch. "
                            + "Expected: " + level.type + ", "
                            + "found: " + feature.getType());
                }
            }
        }
    }

    /**
     * For missing feature values, add a null feature to a path.  This maintains
     * the graph structure for sparse schemas or cases where a feature reading
     * is not available.
     */
    private void addNullFeatures(Path<Feature, T> path) {
        Set<String> knownFeatures = new HashSet<>(levels.keySet());
        Set<String> pathFeatures = new HashSet<>();
        for (Feature feature : path.getLabels()) {
            pathFeatures.add(feature.getName());
        }

        knownFeatures.removeAll(pathFeatures);

        /* Create null features for missing values */
        for (String featureName : knownFeatures) {
            Vertex<Feature, T> v = new Vertex<>();
            v.setLabel(new Feature(featureName));
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
     * Removes all null Features from a path.  This includes any Features that
     * are the standard Java null, or Features with a NULL FeatureType.
     *
     * @param path Path to remove null Features from.
     */
    private void removeNullFeatures(Path<Feature, T> path) {
        List<Vertex<Feature, T>> removals = new ArrayList<>();

        for (Vertex<Feature, T> v : path) {
            Feature f = v.getLabel();
            if (f == null || f.getType() == FeatureType.NULL) {
                removals.add(v);
            }
        }

        for (Vertex<Feature, T> v : removals) {
            path.remove(v);
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
        int order;
        Level level = levels.get(feature.getName());
        if (level != null) {
            order = level.order;
        } else {
            order = addNewFeature(feature);
        }

        return order;
    }

    /**
     * Update the hierarchy levels and known Feature list with a new Feature.
     */
    private int addNewFeature(Feature feature) {
        logger.info("New feature: " + feature.getName());
        Integer order = levels.keySet().size();
        levels.put(feature.getName(), new Level(order, feature.getType()));
        features.offer(feature.getName());

        return order;
    }

    /**
     * Retrieves the ordering of Feature names in this graph hierarchy.
     */
    public Queue<String> getFeatureHierarchy() {
        return new LinkedList<String>(features);
    }

    public List<Path<Feature, T>> getAllPaths() {
        List<Path<Feature, T>> paths = root.descendantPaths();
        for (Path<Feature, T> path : paths) {
            removeNullFeatures(path);

            /* Remove inter-vertex links */
            for (Vertex<Feature, T> vertex : path.getVertices()) {
                vertex.clearEdges();
            }
        }
        return paths;
    }

    public Vertex<Feature, T> getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
