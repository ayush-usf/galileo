
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
