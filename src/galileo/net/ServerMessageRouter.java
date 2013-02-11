
package galileo.net;

import java.io.IOException;

import java.net.InetSocketAddress;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class ServerMessageRouter extends MessageRouter {

    private int port;
    private ServerSocketChannel serverChannel;

    public ServerMessageRouter(int port) {
        this.port = port;
    }

    /**
     * Initializes the server socket channel for incoming client connections and
     * begins listening for messages.
     */
    public void listen()
    throws IOException {
        this.selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(this.port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        this.online = true;

        Thread selectorThread = new Thread(this);
        selectorThread.start();
    }
}
