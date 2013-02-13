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

package galileo.util;

import java.math.BigInteger;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.security.SecureRandom;

import galileo.dataset.BlockMetadata;
import galileo.dataset.FileBlock;

public class StreamIdentifier {
    public static String streamFromHost() {
        String hostName = "";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.out.println("Fatal: cannot determine local host name!");
            e.printStackTrace();
        }
        String[] hostNameParts = hostName.split("-");

        int hostNum    = Integer.parseInt(hostNameParts[1]);
        int groupNum   = hostNum % 12;
        int machineNum = hostNum / 12;

        /* Set up the stream topic for this node. */
        String streamSynopsis = Version.PRODUCT_NAME + "/";
        streamSynopsis += groupNum + "/";
        streamSynopsis += machineNum;

        return streamSynopsis;
    }

    public static String streamFromBlock(FileBlock block) {
        String streamTopic = "Galileo/";
        BlockMetadata metadata = block.getMetadata();
        String geoHash = GeoHash.encode(metadata.getSpatialRange(), 12);

        BigInteger geoInt = null;
        BigInteger machineHash = null;
        try {
            geoInt      = SHA1.fromString(geoHash);
            machineHash = SHA1.fromByteSerializable(metadata);
        } catch(Exception e) {}

        streamTopic += geoInt.mod(BigInteger.valueOf(12)) + "/";
        streamTopic += machineHash.mod(BigInteger.valueOf(4)).toString();
        //streamTopic += 0;

        return streamTopic;
    }

    /**
     * Creates a client entity identifier.
     */
    public static int generateEntityId() {
        SecureRandom random = new SecureRandom();
        return random.nextInt();
    }

    public static String clientStream(int entityId) {
        return null;
    }
}
