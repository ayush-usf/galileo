
package galileo.dataset;

import java.io.IOException;

import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class SpatialRangeImpl implements SpatialRange {
    private float upperLat;
    private float lowerLat;
    private float upperLon;
    private float lowerLon;

    private boolean hasElevation;
    private float upperElevation;
    private float lowerElevation;

    public SpatialRangeImpl(float lowerLat, float upperLat,
                            float lowerLon, float upperLon) {
        this.lowerLat = lowerLat;
        this.upperLat = upperLat;
        this.lowerLon = lowerLon;
        this.upperLon = upperLon;

        hasElevation = false;
    }

    public SpatialRangeImpl(float lowerLat, float upperLat,
                            float lowerLon, float upperLon,
                            float upperElevation, float lowerElevation) {
        this.lowerLat = lowerLat;
        this.upperLat = upperLat;
        this.lowerLon = lowerLon;
        this.upperLon = upperLon;

        hasElevation = true;
        this.upperElevation = upperElevation;
        this.lowerElevation = lowerElevation;
    }

    @Override
    public float getUpperBoundForLatitude() {
        return upperLat;
    }

    @Override
    public float getLowerBoundForLatitude() {
        return lowerLat;
    }

    @Override
    public float getUpperBoundForLongitude() {
        return upperLon;
    }

    @Override
    public float getLowerBoundForLongitude() {
        return lowerLon;
    }

    @Override
    public Coordinates getCenterPoint() {
        float latDifference = upperLat - lowerLat;
        float latDistance = latDifference / 2;

        float lonDifference = upperLon - lowerLon;
        float lonDistance = lonDifference / 2;

        return new Coordinates(lowerLat + latDistance,
                               lowerLon + lonDistance);
    }

    @Override
    public boolean hasElevationBounds() {
        return hasElevation;
    }

    @Override
    public float getUpperBoundForElevation() {
        return upperElevation;
    }

    @Override
    public float getLowerBoundForElevation() {
        return lowerElevation;
    }

    @Override
    public String getMeasurementUnitForElevation() {
        //TODO: units?
        return "";
    }

    @Override
    public String getMeasurementUnitForLatitude() {
        //TODO: units?
        return "";
    }

    @Override
    public String getMeasurementUnitForLongitude() {
        //TODO: units?
        return "";
    }

    public SpatialRangeImpl(SerializationInputStream in)
    throws IOException {
        lowerLat = in.readFloat();
        upperLat = in.readFloat();
        lowerLon = in.readFloat();
        upperLon = in.readFloat();

        hasElevation = in.readBoolean();
        if (hasElevation) {
            lowerElevation = in.readFloat();
            upperElevation = in.readFloat();
        }
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeFloat(lowerLat);
        out.writeFloat(upperLat);
        out.writeFloat(lowerLon);
        out.writeFloat(upperLon);

        out.writeBoolean(hasElevation);
        if (hasElevation) {
            out.writeFloat(lowerElevation);
            out.writeFloat(upperElevation);
        }
    }
}
