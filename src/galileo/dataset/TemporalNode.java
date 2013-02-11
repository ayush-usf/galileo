
package galileo.dataset;

import java.util.Iterator;

public interface TemporalNode extends GraphNode {

    public Iterator<TemporalNode> select(TemporalRange temporalRange);

    public Iterator<SpatialNode> getSpatialView(float lat, float lon,
            long distance, long direction);

    public Iterator<FeatureNode> getFeatureView(String feature);
}
