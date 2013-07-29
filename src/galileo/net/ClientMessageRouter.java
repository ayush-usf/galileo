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

package galileo.net;

import galileo.client.EventPublisher;
import galileo.comm.Disconnect;

import java.io.IOException;

import java.net.ConnectException;
import java.net.InetSocketAddress;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ClientMessageRouter extends MessageRouter {

    private SocketChannel channel;

    private Map<NetworkDestination, SelectionKey> connectedHosts
        = new HashMap<>();
    private Map<SelectionKey, NetworkDestination> activeKeys
        = new HashMap<>();

    private Thread selectorThread;

    /**
     * Connect to a server ({@link ServerMessageRouter}) using the specified
     * hostname and port.
     */
    public NetworkDestination connectTo(String hostname, int port)
    throws IOException {
        return connectTo(new NetworkDestination(hostname, port));
    }

    /**
     * Connect to a server ({@link ServerMessageRouter}) using the specified
     * network destination.
     */
    private NetworkDestination connectTo(NetworkDestination destination)
    throws IOException {

        this.selector = Selector.open();

        channel = SocketChannel.open();
        channel.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress(
                destination.getHostname(), destination.getPort());
        channel.connect(address);

        TransmissionTracker tracker = new TransmissionTracker();

        /* Register with our Selector */
        SelectionKey key
            = channel.register(this.selector, SelectionKey.OP_CONNECT, tracker);
        connectedHosts.put(destination, key);
        activeKeys.put(key, destination);

        if (selectorThread == null) {
            selectorThread = new Thread(this);
        }

        /* Run one iteration of the selection loop.  This ensures our connection
         * is set up before we return. */
        processSelectionKeys();

        if (!channel.isConnected()) {
            /* We're not connected; the connection was refused. */
            throw new ConnectException("Connection refused");
        }

        return destination;
    }

    /**
     * Shuts down the message processor and disconnects from the server(s).
     */
    public void shutdown() {
        this.online = false;
        for (SelectionKey key : connectedHosts.values()) {
            disconnect(key);
        }
        selector.wakeup();
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
                if (!online()) {
                    this.online = true;
                    selectorThread.start();
                }
            }
        } catch (IOException e) {
            disconnect(key);
        }
    }

    /**
     * Broadcasts a message to the connected servers.
     */
    public void broadcastMessage(GalileoMessage message)
    throws IOException {
        for (SelectionKey key : connectedHosts.values()) {
            sendMessage(key, message);
        }
    }

    /**
     * Sends a message to the specified network destination.
     */
    public void sendMessage(NetworkDestination destination,
            GalileoMessage message)
    throws IOException {

        SelectionKey key = connectedHosts.get(destination);
        if (key == null) {
            throw new IOException("Not connected to destination: "
                    + destination);
        }

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
        NetworkDestination destination = activeKeys.get(key);
        Disconnect disconnect = new Disconnect(destination);

        GalileoMessage message = null;
        try {
            message = EventPublisher.wrapEvent(disconnect);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not publish Disconnect event.", e);
        }

        connectedHosts.remove(destination);
        activeKeys.remove(key);

        super.dispatchMessage(message);
        super.disconnect(key);
    }
}
