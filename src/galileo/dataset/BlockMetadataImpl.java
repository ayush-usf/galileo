/*
Copyright (c) 2013, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

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
