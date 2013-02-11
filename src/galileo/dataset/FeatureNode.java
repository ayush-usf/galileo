
package galileo.dataset;

import java.util.Iterator;

public interface FeatureNode  extends GraphNode{



    public Iterator<SpatialNode> getSpatialView(float lat, float lon, long distance, long direction);

    public Iterator<TemporalNode> getTemporalView(TemporalRange temporalRange);
}
