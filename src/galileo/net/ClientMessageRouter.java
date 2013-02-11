
package galileo.net;

import java.io.IOException;

import java.net.ConnectException;
import java.net.InetSocketAddress;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ClientMessageRouter extends MessageRouter {

    private SelectionKey key;
    private SocketChannel channel;

    private Thread selectorThread;

    /**
     * Connect to a server ({@link ServerMessageRouter}) using the specified
     * hostname and port.
     */
    public void connectTo(String host, int port)
    throws IOException {

        this.selector = Selector.open();

        channel = SocketChannel.open();
        channel.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress(host, port);
        channel.connect(address);

        TransmissionTracker tracker = new TransmissionTracker();

        /* Register with our Selector */
        key = channel.register(this.selector, SelectionKey.OP_CONNECT, tracker);

        selectorThread = new Thread(this);

        /* Run one iteration of the selection loop.  This ensures our connection
         * is set up before we return. */
        processSelectionKeys();

        if (!channel.isConnected()) {
            /* We're not connected; the connection was refused. */
            throw new ConnectException("Connection refused");
        }
    }

    /**
     * Shuts down the message processor and disconnects from the server.
     */
    public void shutdown() {
        this.online = false;
    }

    public boolean online() {
        return online;
    }

    @Override
    protected void connect(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();

            if (channel.finishConnect()) {
                key.interestOps(SelectionKey.OP_READ);
                this.online = true;
                selectorThread.start();
            }
        } catch (IOException e) {
            disconnect(key);
        }
    }

    /**
     * Sends a message to the connected server.
     */
    public void sendMessage(GalileoMessage message)
    throws IOException {
        sendMessage(key, message);
    }

    /**
     * Inform subscribed MessageListener instances that the connection to the
     * remote server has been terminated.  This is achieved by publishing a null
     * message to the MessageListener instances.
     *
     * @param key SelectionKey for the SocketChannel that was disconnected.
     */
    @Override
    protected void disconnect(SelectionKey key) {
        super.online = false;
        super.dispatchMessage(null);
        super.disconnect(key);
    }
}
