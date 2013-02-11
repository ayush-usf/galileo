
package galileo.dataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class BlockMetadataImpl implements BlockMetadata {

    private RuntimeMetadata runtimeMetadata = new RuntimeMetadataImpl();

    private TemporalRange temporalRange;
    private SpatialRange  spatialRange;

    private FeatureSet features;
    private DeviceSet devices;

    public BlockMetadataImpl(TemporalRange temporalRange,
                             SpatialRange  spatialRange,
                             FeatureSet features,
                             DeviceSet devices) {

        this.temporalRange = temporalRange;
        this.spatialRange = spatialRange;
        this.features = features;
        this.devices  = devices;
    }

    @Override
    public String getIdentifier() {
        return "";
    }

    @Override
    public RuntimeMetadata getRuntimeMetadata() {
        return runtimeMetadata;
    }

    @Override
    public void setRuntimeMetadata(RuntimeMetadata runtimeMetadata) {
        this.runtimeMetadata = runtimeMetadata;
    }

    @Override
    public TemporalRange getTemporalRange() {
        return temporalRange;
    }

    @Override
    public SpatialRange getSpatialRange() {
        return spatialRange;
    }

    @Override
    public FeatureSet getFeatures() {
        return features;
    }

    @Override
    public DeviceSet getDevices() {
        return devices;
    }

    @Override
    public byte[] getChecksumInfo() {
        //TODO: ...
        return null;
    }

    @Override
    public void getAccessPermissions() {
        //TODO: ...
    }

    public BlockMetadataImpl(SerializationInputStream in)
    throws IOException {
        spatialRange = new SpatialRangeImpl(in);
        temporalRange = new TemporalRangeImpl(in);

//        features = new ArrayList<String>();
//        int numFeatures = inStream.readInt();
//        for (int i = 0; i < numFeatures; ++i) {
//            int stringSize = inStream.readInt();
//            byte[] byteString = new byte[stringSize];
//            inStream.read(byteString);
//            features.add(new String(byteString));
//        }
//
//        devices = new ArrayList<String>();
//        int numDevices = inStream.readInt();
//        for (int i = 0; i < numDevices; ++i) {
//            int stringSize = inStream.readInt();
//            byte[] byteString = new byte[stringSize];
//            inStream.read(byteString);
//            devices.add(new String(byteString));
//        }

        runtimeMetadata = new RuntimeMetadataImpl(in);
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeSerializable(spatialRange);
        out.writeSerializable(temporalRange);
        // TODO: features
        // TODO: devices
        //
        out.writeSerializable(runtimeMetadata);
    }
}
