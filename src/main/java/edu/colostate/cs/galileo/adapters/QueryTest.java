package edu.colostate.cs.galileo.adapters;

import edu.colostate.cs.galileo.dataset.feature.Feature;
import edu.colostate.cs.galileo.dataset.feature.FeatureType;
import edu.colostate.cs.galileo.graph.FeatureHierarchy;
import edu.colostate.cs.galileo.graph2.Sketch;
import edu.colostate.cs.galileo.query2.Expression;
import edu.colostate.cs.galileo.query2.Operator;
import edu.colostate.cs.galileo.query2.RelationalQuery;
import edu.colostate.cs.galileo.util.PerformanceTimer;

public class QueryTest {

    public static void main(String[] args)
    throws Exception {
        for (String featureName : TestConfiguration.FEATURE_NAMES) {
            ReadMetaBlob.activeFeatures.add(featureName);
        }

        FeatureHierarchy fh = new FeatureHierarchy();
        for (String featureName : TestConfiguration.FEATURE_NAMES) {
            System.out.println(
                    TestConfiguration.quantizers.get(featureName).numTicks()
                    + "   " + featureName);
            fh.addFeature(featureName, FeatureType.FLOAT);
        }
        fh.addFeature("location", FeatureType.STRING);
        Sketch s = new Sketch(fh);

        for (String f : args) {
            ReadMetaBlob.loadData(f, s);
        }

        RelationalQuery mq = new RelationalQuery();
        mq.addExpression(new Expression(
                    Operator.STR_PREFIX, new Feature("location", "9xqj")));
        mq.addExpression(
                new Expression(
                    Operator.RANGE_INC,
                    new Feature("temperature_surface", 300.0f),
                    new Feature(320.0f)));
        mq.addExpression(
                new Expression(
                    Operator.GREATER,
                    new Feature("relative_humidity_zerodegc_isotherm", 70.0f)));

        mq.execute(s.getRoot());
        PerformanceTimer statQuery = new PerformanceTimer("relQuery");
        for (int i = 0; i < 1000; ++i) {
        statQuery.start();
        s.query(mq);
        statQuery.stopAndPrint();
        }
    }
}
