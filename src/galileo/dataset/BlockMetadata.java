
package galileo.dataset;

import galileo.serialization.ByteSerializable;

public interface BlockMetadata extends ByteSerializable {

    public String getIdentifier();

    public RuntimeMetadata getRuntimeMetadata();
    public void setRuntimeMetadata(RuntimeMetadata runtimeMetadata);

    public TemporalRange getTemporalRange();

    public SpatialRange getSpatialRange();

    public FeatureSet getFeatures();

    public DeviceSet getDevices();

    public byte[] getChecksumInfo();

    /** This needs to be elaborated on a little more. */
    public void getAccessPermissions();
}
