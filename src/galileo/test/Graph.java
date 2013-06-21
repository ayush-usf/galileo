package galileo.test;

import java.util.LinkedList;

import galileo.dataset.Feature;

import galileo.graph.HierarchicalGraph;
import galileo.graph.Path;
import galileo.graph.CollectorVertex;

import galileo.query.Expression;
import galileo.query.Operation;
import galileo.query.Query;

public class Graph {
    public static void main(String[] args) {
        CollectorVertex<Integer, String> root = new CollectorVertex<>(0, "root");

        LinkedList<CollectorVertex<Integer, String>> ll = new LinkedList<>();
        ll.add(new CollectorVertex<>(6, "hi"));
        ll.add(new CollectorVertex<>(2, "yo"));
        ll.add(new CollectorVertex<>(3, "sup"));
        //root.addPath(ll.iterator());

//        ll = new LinkedList<>();
//        ll.add(new CollectorVertex<>(6, "hi"));
//        ll.add(new CollectorVertex<>(1, "yo"));
//        ll.add(new CollectorVertex<>(3, "sup"));
//        root.addPath(ll);
//
//        ll = new LinkedList<>();
//        ll.add(new CollectorVertex<>(6, "hi"));
//        ll.add(new CollectorVertex<>(2, "yea"));
//        root.addPath(ll);
//
//        ll = new LinkedList<>();
//        ll.add(new CollectorVertex<>(7, "hi"));
//        ll.add(new CollectorVertex<>(4, "yo"));
//        ll.add(new CollectorVertex<>(5, "sup"));
//        root.addPath(ll);
//
//        root.addPath(new Path<Integer, String>(
//                    new CollectorVertex<>(3, "matthew"),
//                    new CollectorVertex<>(4, "monte"),
//                    new CollectorVertex<>(5, "malensek")));

        Feature f = new Feature("humidity", 0.35);
        Feature f2 = new Feature("wind", 2.3);

        HierarchicalGraph<String> g = new HierarchicalGraph<>();
        Path<Feature, String> p = new Path<Feature, String>();
        p.add(new CollectorVertex<>(f2, "monte"));
        p.add(new CollectorVertex<>(f, "matthew"));

                    //new CollectorVertex<>(new Feature("wind", 2.3), "malensek")));
        g.addPath(p);

        Path<Feature, String> pp = new Path<Feature, String>(
                new CollectorVertex<Feature, String>(new Feature("humidity", 0.99), "boo"),
                new CollectorVertex<Feature, String>(new Feature("snow", 3.2), "awes"));

        g.addPath(pp);

        g.addPath(new Path<Feature, String>("node1",
                    new CollectorVertex<Feature, String>(new Feature("humidity", 0.99)),
                    new CollectorVertex<Feature, String>(new Feature("wind", 4.2)),
                    new CollectorVertex<Feature, String>(new Feature("snow", 3.8))));

        g.addPath(new Path<Feature, String>("node2",
                    new CollectorVertex<Feature, String>(new Feature("wind", 4.2)),
                    new CollectorVertex<Feature, String>(new Feature("mattness", 6)),
                    new CollectorVertex<Feature, String>(new Feature("humidity", 0.36))));

        g.addPath(new Path<Feature, String>("node3",
                    new CollectorVertex<Feature, String>(new Feature("wind", 4.2)),
                    new CollectorVertex<Feature, String>(new Feature("mattness", 6)),
                    new CollectorVertex<Feature, String>(new Feature("humidity", 0.36))));

        g.addPath(new Path<Feature, String>(
                    new CollectorVertex<Feature, String>(new Feature("humidity", 0.83), "test")));

        System.out.println(g);

        Query q = new Query();
        //q.addOperation(new Operation("humidity", new Expression("<=", 32.6), new Expression(">", 6)));
        q.addOperation(new Operation("poop", new Expression("==", 2.3)));
        //q.addOperation(new Operation("temperature", new Expression(">", 92)));
        q.addOperation(new Operation("poop", new Expression("==", 33)));
        q.addOperation(new Operation("pee", new Expression("==", 77)));
        //q.addOperation(new Operation("wind", new Expression("==", 4.2)));
        //q.addOperation(new Operation("wind", new Expression("==", 4.2)));
        q.addOperation(new Operation("humidity", new Expression("==", .99)));
        //q.addOperation(new Operation("mattness", new Expression("==", 6)));

        g.evaluateQuery(q);
    }
}
