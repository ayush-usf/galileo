
package galileo.dht;

public class NetworkManager {

    public void readConfiguration() {
        String storageRoot = System.getenv("GALILEO_CONF");
        if (storageRoot == null) {
            storageRoot = System.getProperty("configDirectory", "./config/");
        }


    }
}
