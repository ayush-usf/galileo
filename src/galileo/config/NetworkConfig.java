
package galileo.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.dht.GroupInfo;
import galileo.dht.NetworkInfo;
import galileo.dht.NodeInfo;

import galileo.util.FileNames;
import galileo.util.Pair;

/**
 * Reads Galileo DHT information from disk.  The on-disk format for a network
 * setup includes a directory with several *.group files within.  Each line in a
 * group file contains a hostname:port pair, although a port number is not
 * required if the server in question is using the default port number.
 *
 * @author malensek
 */
public class NetworkConfig {

    private static final Logger logger = Logger.getLogger("galileo");

    //TODO config option
    public static final int DEFAULT_PORT = 5555;

    public static final String GROUP_EXT = "group";

    /**
     * Reads a network description directory from disk.
     *
     * @param directory full path name of the network description directory.
     *
     * @return NetworkInfo containing the network information read from the
     * directory.
     */
    public static NetworkInfo readNetworkDescription(String directory)
    throws IOException {

        NetworkInfo network = new NetworkInfo();

        File dir = new File(directory);

        for (File file : dir.listFiles()) {

            Pair<String, String> pair = FileNames.splitExtension(file);
            String ext = pair.b;

            if (ext.toLowerCase().equals(GROUP_EXT)) {
                GroupInfo group = readGroupFile(file);
                network.addGroup(group);
            }
        }

        return network;
    }

    /**
     * Read host:port pairs from a group description file (*.group).
     *
     * @param file File containing the group members.
     *
     * @return GroupInfo containing the hosts read from file.
     */
    public static GroupInfo readGroupFile(File file)
    throws IOException {
        Pair<String, String> pair = FileNames.splitExtension(file);
        String groupName = pair.a;

        GroupInfo group = new GroupInfo(groupName);

        FileReader fReader = new FileReader(file);

        BufferedReader reader = new BufferedReader(fReader);
        int lineNum = 0;
        String line;

        while ((line = reader.readLine()) != null) {
            ++lineNum;
            line = line.trim().replaceAll("\\s","");

            String[] hostInfo = line.split(":", 2);
            String nodeName = hostInfo[0];
            if (nodeName.equals("")) {
                logger.warning("Could not determine StorageNode " +
                        "hostname for group '" + groupName + "' on line " +
                        lineNum + "; ignoring entry.");

                continue;
            }

            int port = DEFAULT_PORT;
            if (hostInfo.length > 1) {
                try {
                    port = Integer.parseInt(hostInfo[1]);
                } catch (NumberFormatException e) {
                    logger.log(Level.WARNING, "Could not parse " +
                            "StorageNode port number on line " + lineNum +
                            ";  ignoring entry.", e);
                }
            }

            NodeInfo node = new NodeInfo(hostInfo[0], port);
            group.addNode(node);
        }
        reader.close();

        return group;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(NetworkConfig.readNetworkDescription("/s/chopin/b/grad/malensek/res/galileo/Galileo/trunk/config/network"));
    }
}
