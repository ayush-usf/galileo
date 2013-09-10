package galileo.test.dataset.feature;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import galileo.dataset.feature.Feature;
import galileo.serialization.Serializer;

public class Serialization {

    @Test
    public void testDouble() throws Exception {

        Feature f1 = new Feature("test", 3.6);
        byte[] bytes = Serializer.serialize(f1);
        Feature f2 = Serializer.deserialize(Feature.class, bytes);

        assertEquals("As Integer", f1.compareTo(f2), 0);
    }
}
