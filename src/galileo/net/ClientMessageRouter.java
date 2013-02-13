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
