package galileo.test.dataset.feature;

import static org.junit.Assert.*;

import org.junit.Test;

import galileo.dataset.feature.Feature;
import galileo.dataset.feature.FeatureType;


/**
 * Tests Feature casting functionality.
 */
public class Casts {

    private static double EPSILON = 0.000001;

    @Test
    public void testInteger() {
        Feature f = new Feature("int", 0);
        assertEquals("FeatureType", f.getType(), FeatureType.INT);
        testEquality(f, 0, 0.0);

        testEquality(new Feature("int", 64), 64, 64.0);
        testEquality(new Feature("int", -2100483648),
                -2100483648, -2100483648.0);
        testEquality(new Feature("int", Integer.MAX_VALUE),
                Integer.MAX_VALUE, (double) Integer.MAX_VALUE);
        testEquality(new Feature("int", Integer.MIN_VALUE),
                Integer.MIN_VALUE, (double) Integer.MIN_VALUE);
    }

    @Test
    public void testLong() {
        Feature f = new Feature("long", 0L);
        assertEquals("FeatureType", f.getType(), FeatureType.LONG);
        testEquality(f, 0, 0.0);

        testEquality(new Feature("long", 64L), 64, 64.0);
        testEquality(new Feature("long", Long.MAX_VALUE),
                Long.MAX_VALUE, (double) Long.MAX_VALUE);
    }

    @Test
    public void testFloat() {
        Feature f = new Feature("float", 0.0f);
        assertEquals("FeatureType", f.getType(), FeatureType.FLOAT);
        testEquality(f, 0, 0.0);

        float fl = Float.MAX_VALUE;
        Feature f2 = new Feature("float", Float.MAX_VALUE);
        assertEquals("As integer", (int) fl, f2.getInt());
        assertEquals("As double", (double) fl, f2.getDouble(), EPSILON);
        assertEquals("As long", (long) fl, f2.getLong());

        testEquality(new Feature("float", 3.6f), 3, 3.6);
        testEquality(new Feature("float", Float.MIN_VALUE),
                (long) Float.MIN_VALUE, Float.MIN_VALUE);
    }

    @Test
    public void testDouble() {
        Feature f = new Feature("test", 0.0d);
        assertEquals("FeatureType", f.getType(), FeatureType.DOUBLE);
        testEquality(f, 0, 0.0);

        testEquality(new Feature("double", 3.6), 3, 3.6);

        double d = Double.MAX_VALUE;
        Feature f2 = new Feature("double", Double.MAX_VALUE);
        assertEquals("As integer", (int) d, f2.getInt());
        assertEquals("As float", (float) d, f2.getFloat(), EPSILON);
        assertEquals("As long", (long) d, f2.getLong());

        testEquality(new Feature("double", Double.MIN_VALUE),
                (long) Double.MIN_VALUE, Double.MIN_VALUE);
    }

    @Test
    public void testString() {
        Feature f = new Feature("string", "testing testing");
        assertEquals("FeatureType", FeatureType.STRING, f.getType());
        assertEquals("string", "testing testing", f.getString());

        Feature f2 = new Feature("long", 36L);
        assertEquals("string", "36", f2.getString());

        Feature f3 = new Feature("double", 86.4);
        assertEquals("string", "86.4", f3.getString());

        Feature f4 = new Feature("string", "12.3");
        assertEquals("to float", 12.3, f4.getFloat(), EPSILON);
    }

    public static void testEquality(Feature feature, long l, double d) {
        assertEquals("As Integer", (int) l, feature.getInt());
        assertEquals("As Float", (float) d, feature.getFloat(), EPSILON);

        assertEquals("As Long", l, feature.getLong());
        assertEquals("As double", d, feature.getDouble(), EPSILON);
    }
}
