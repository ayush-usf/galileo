
package galileo.dataset;

import java.util.Iterator;

public interface SpatialNode extends GraphNode{


    public String getApproximateGeoHash(float lat, float lon);


    public GraphNode getFirstChildNode();

    public GraphNode getNextChildNode();



    /**
     * Allows the application to find the node that provides the minumum spatial
     * area (rectangle) to contain the point specified by lat and lon.
     * This method will return the leaf node that matches as many as possible
     * digits with digits of the geohash code from the lat and lon.
     */
    public Iterator<SpatialNode> select(float lat, float lon, long distance, long direction);


    public void selectArea(float pointInspace, long radius);

    public int getNumberOfChildNodes();

    public String getTypeOfChildNode();


    public Iterator<TemporalNode> getTemporalView(TemporalRange temporalRange);


    public Iterator <FeatureNode> getFeatureView(String feature);

}
