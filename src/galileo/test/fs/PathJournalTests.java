
package galileo.test.fs;

import java.io.IOException;

import galileo.dataset.feature.Feature;
import galileo.fs.PathJournal;
import galileo.graph.FeaturePath;

import org.junit.Test;

import static org.junit.Assert.*;

public class PathJournalTests {

    @Test
    public void testSerialization() throws Exception {
        FeaturePath<String> fp = new FeaturePath<>();
        fp.add(new Feature("humidity", 24.3f));
        fp.add(new Feature("temperature", 4.1f));
        fp.add(new Feature("wind", 8.0f));
        fp.add(new Feature("snow", 0.32f));

        PathJournal pj = new PathJournal("/tmp/pathjournal");
        //pj.start();
        //pj.persistPath(fp);
        pj.recover();
    }
}
