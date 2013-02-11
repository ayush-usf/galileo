
package galileo.dataset;

import galileo.serialization.ByteSerializable;

public interface SpatialRange extends ByteSerializable {

    public float getUpperBoundForLatitude();

    public float getLowerBoundForLatitude();

    public float getUpperBoundForLongitude();

    public float getLowerBoundForLongitude();

    /**
     * Returns the center point of the spatial range, which is half way between
     * the upper and lower bounds for latitude and longitude.
     *
     * @return latitude, longitude coordinate pair
     */
    public Coordinates getCenterPoint();

    public boolean hasElevationBounds();

    public float getUpperBoundForElevation();

    public float getLowerBoundForElevation();

    public String getMeasurementUnitForElevation();

    public String getMeasurementUnitForLatitude();

    public String getMeasurementUnitForLongitude();
}
