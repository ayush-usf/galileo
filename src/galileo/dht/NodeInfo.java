
package galileo.dht;

public class NodeInfo {

    private String hostname;
    private int port;

    public NodeInfo(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public String toString() {
        return hostname + ":" + port;
    }
}
