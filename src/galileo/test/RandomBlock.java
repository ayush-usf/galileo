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

package galileo.test;

import galileo.dataset.Device;
import galileo.dataset.DeviceSet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import galileo.dataset.BlockMetadata;
import galileo.dataset.BlockMetadataImpl;
import galileo.dataset.FeatureImpl;
import galileo.dataset.FeatureSet;
import galileo.dataset.FileBlock;
import galileo.dataset.TemporalRange;
import galileo.dataset.TemporalRangeImpl;
import galileo.dataset.SpatialRange;
import galileo.util.GeoHash;

public class RandomBlock {
    private static Random randomGenerator = new Random(System.nanoTime());

    public static FileBlock generateBlock(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();

        //year = randomInt(2002, 2011);
        //year = 2004;

        /* Calendar uses 0-based month indices */
        //month = randomInt(0, 11);
        //month = 7;

        day = randomInt(1, 28);
        //day = 1;

        calendar.set(year, month, day);

        /* Convert the random values to a start time, then add 1ms for the end
         * time.  This simulates 1ms worth of data. */
        long startTime = calendar.getTimeInMillis();
        long endTime   = startTime + 1;

        TemporalRange tempRange = new TemporalRangeImpl(startTime, endTime);

        // The continental US
        String[] geoRand = { "c2", "c8", "cb", "f0", "f2",
                             "9r", "9x", "9z", "dp", "dr",
                             "9q", "9w", "9y", "dn", "dq",
                             "9m", "9t", "9v", "dj" };

        String geoPre = geoRand[randomInt(0, geoRand.length - 1)];
        String hash = geoPre;

        for (int i = 0; i < 10; ++i) {
            int random = randomInt(0, GeoHash.charMap.length - 1);
            hash += GeoHash.charMap[random];
        }

        SpatialRange spatialRange = GeoHash.decodeHash(hash);

        String[] featSet = { "wind_speed", "wind_direction", "condensation",
                             "temperature", "humidity", "awesomeness" };

        FeatureSet features = new FeatureSet();
        for (int i = 0; i < 5; ++i) {
            String featureName = featSet[randomInt(0, featSet.length - 1)];
            features.put(new FeatureImpl(featureName, randomFloat() * 100));
        }

        Device d = new Device("test");
        DeviceSet devices = new DeviceSet();
        devices.put(d);

        BlockMetadata metadata =
            new BlockMetadataImpl(tempRange, spatialRange, features, devices);

        // For now, data consists of 1000 random floats
        float[] blockData = new float[1000];
        for (int i = 0; i < 1000; ++i) {
            blockData[i] = randomFloat();
        }

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream outStream =
            new DataOutputStream(new BufferedOutputStream(byteStream));

        try {
            for (float data : blockData) {
                outStream.writeFloat(data);
            }
            outStream.close();
        } catch (IOException e) { /*TODO: (temporary; remove this later)*/ }

        byte[] blockBytes = byteStream.toByteArray();

        FileBlock block = new FileBlock(blockBytes, metadata);

        return block;
    }

    public static int randomInt(int start, int end) {
        return randomGenerator.nextInt(end - start + 1) + start;
    }

    public static float randomFloat() {
        return randomGenerator.nextFloat();
    }
}
