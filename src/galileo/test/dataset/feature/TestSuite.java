package galileo.test.dataset.feature;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    ArrayTest.class,
    Casts.class,
    Serialization.class,
})
public class TestSuite { }
