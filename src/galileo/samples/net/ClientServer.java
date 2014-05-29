package galileo.samples.net;

import galileo.net.DualMessageRouter;

public class ClientServer {

    public static final int DEFALT_PORT = 7777;

    private int port;
    private DualMessageRouter messageRouter;

    public ClientServer() {
        this(DEFALT_PORT);
    }

    public ClientServer(int port) {
        this.port = port;
    }

    public void listen() {
    }

    public static void main(String[] args) {

    }
}
