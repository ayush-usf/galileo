package galileo.samples;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import galileo.dataset.feature.Feature;
import galileo.query.Expression;
import galileo.query.Operation;
import galileo.query.Operator;
import galileo.query.Query;

public class RandomQuery {

    public static Random random = new Random();

    public static Operator randomOperator() {
        int i = random.nextInt(Operator.values().length - 2) + 1;
        return Operator.fromInt(i);
    }

    public static Feature randomFeature(String name, float min, float max) {
        float diff = max - min;
        float rand = random.nextFloat() * diff;
        float value = min + rand;
        return new Feature(name, value);
    }

    public static Query randomQuery() {
        Query q = new Query();

        List<Feature> features = new ArrayList<>();
        features.add(randomFeature("pressure", 1, 100));
        features.add(randomFeature("visibility", 1, 100));
        features.add(randomFeature("total_precipitation", 1, 100));
        features.add(randomFeature("temperature_surface", 1, 100));


        List<Expression> expressions = new ArrayList<>();
        for (Feature f : features) {
            expressions.add(new Expression(randomOperator(), f));
        }

        Operation op = new Operation(expressions.toArray(
                    new Expression[expressions.size()]));

        q.addOperation(op);

        return q;
    }

    public static void main(String[] args) {
        Query q = randomQuery();
        System.out.println(q);
    }
}
