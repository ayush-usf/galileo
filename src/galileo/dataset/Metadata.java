/*
Copyright (c) 2014, Colorado State University
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

import galileo.dataset.feature.Feature;
import galileo.dataset.feature.FeatureArray;
import galileo.dataset.feature.FeatureArraySet;
import galileo.dataset.feature.FeatureSet;
import galileo.serialization.ByteSerializable;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class Metadata implements ByteSerializable {

    private String name = "";

    /**
     * Metadata attributes: these Features are represented by a 1D array and
     * are accessed as a simple key-value store.
     */
    private FeatureSet attributes = new FeatureSet();

    /**
     * A key-value store for multidimensional {@link FeatureArray}s.
     */
    private FeatureArraySet features = new FeatureArraySet();

    /**
     * Maintains metadata information that is only valid at system run time.
     */
    private RuntimeMetadata runtimeMetadata = new RuntimeMetadata();

    public Metadata() { }

    public Metadata(String name) {
        this.name = name;
    }

    public void putAttribute(Feature feature) {
        attributes.put(feature);
    }

    public Feature getAttribute(String featureName) {
        return attributes.get(featureName);
    }

    public FeatureSet getAttributes() {
        return attributes;
    }

    public void putFeature(FeatureArray feature) {
        features.put(feature);
    }

    public FeatureArray getFeature(String featureName) {
        return features.get(featureName);
    }

    public FeatureArraySet getFeatures() {
        return features;
    }

    @Deserialize
    public Metadata(SerializationInputStream in)
    throws IOException {
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
    }
}
