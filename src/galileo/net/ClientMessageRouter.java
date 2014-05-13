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

import galileo.serialization.Serializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides client-side message routing capabilities. This includes connecting
 * to a remote server, and transmitting messages in non-blocking mode.
 *
 * @author malensek
 */
public class ClientMessageRouter extends MessageRouter {

    protected static final Logger logger = Logger.getLogger("galileo");

    protected Map<SocketChannel, NetworkDestination> connections
        = new HashMap<>();
    protected Map<NetworkDestination, SocketChannel> connectedHosts
        = new HashMap<>();

    protected Map<SocketChannel, TransmissionTracker> tt = new HashMap<>();

    protected Queue<SocketChannel> pendingRegistrations
        = new ConcurrentLinkedQueue<>();

    public ClientMessageRouter()
    throws IOException {
        super();
        initializeSelector();
    }

    public ClientMessageRouter(int readBufferSize, int maxWriteQueueSize)
    throws IOException {
        super(readBufferSize, maxWriteQueueSize);
        initializeSelector();
    }

    private void initializeSelector()
    throws IOException {
        this.selector = Selector.open();
        this.online = true;
        Thread selectorThread = new Thread(this);
        selectorThread.start();
    }

    /**
     * Connects to a server.
     *
     * @param hostname name of the host to connect to
     * @param port port on the destination host to connect to
     */
    public NetworkDestination connectTo(String hostname, int port)
    throws IOException {
        return connectTo(new NetworkDestination(hostname, port));
    }

    /**
     * Connects to a server at the specified network destination.
     *
     * @param destination NetworkDestination to connect to.
     */
    public NetworkDestination connectTo(NetworkDestination destination)
    throws IOException {
        if (connectedHosts.containsKey(destination)) {
            return destination;
        }

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress(
                destination.getHostname(), destination.getPort());
        channel.connect(address);

        tt.put(channel, new TransmissionTracker(writeQueueSize));
        pendingRegistrations.offer(channel);

        connectedHosts.put(destination, channel);
        return destination;
    }

    /**
     * Handles pending registration operations on the Selector thread.
     */
    private void processPendingRegistrations()
    throws ClosedChannelException {
        Iterator<SocketChannel> it = pendingRegistrations.iterator();
        while (it.hasNext() == true) {
            SocketChannel channel = it.next();
            it.remove();
            TransmissionTracker tracker = tt.get(channel);
            channel.register(selector, SelectionKey.OP_CONNECT, tracker);
        }
    }

    @Override
    public void run() {
        while (online) {
            try {
                processPendingRegistrations();
                processSelectionKeys();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    protected void connect(SelectionKey key) {
//        super.connect(key);
//        waitingKeys.get((SocketChannel) key.channel()).offer(key);
//    }

    /**
     * Forcibly shuts down the message processor and disconnects from any
     * connected server(s).  If pending writes have been queued, they will be
     * discarded.
     */
    public void forceShutdown() {
        shutdown(true);
    }

    /**
     * Shuts down the message processor and disconnects from the server(s).  If
     * pending writes have been queued, this method will block until the queue
     * is empty.
     */
    public void shutdown() {
        shutdown(false);
    }

    /**
     * Shuts down the message processor and disconnects from the server(s).
     *
     * @param forcible Whether or not to forcibly shut down (discard queued
     * messages).
     */
    private void shutdown(boolean forcible) {
        for (SocketChannel channel : connectedHosts.values()) {
            SelectionKey key = channel.keyFor(this.selector);

            /* If this is not a forcible shutdown, then we need to check each
             * TransmissionTracker's pending write queue, and make sure the
             * items in the queues get sent before shutdown happens. */
            if (forcible == false) {
                safeShutdown(key);
            }

            if (key != null) {
                disconnect(key);
            }
        }
        this.online = false;
        selector.wakeup();
    }

    /**
     * This method checks a given SelectionKey's write queue for pending
     * writes, and then does a series of sleep-checks until the queue is empty.
     *
     * @param key SelectionKey to monitor for pending writes.
     */
    private void safeShutdown(SelectionKey key) {
        final int initialWait = 1000;
        final int longestWait = 5000;

        TransmissionTracker tracker = TransmissionTracker.fromKey(key);
        BlockingQueue<ByteBuffer> pendingWrites
            = tracker.getPendingWriteQueue();

        int wait = initialWait;
        int size = pendingWrites.size();
        if (pendingWrites.isEmpty() == false) {
            do {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "Received interrupt "
                            + "during safe shutdown.", e);
                }

                if (pendingWrites.isEmpty() == false) {
                    /* Still not empty; we'll log something now. */
                    logger.info("Waiting to shut down; " + pendingWrites.size()
                            + " items remaining in write queue.");

                    /* If the queue didn't get any smaller, increase the amount
                     * of time we'll wait before polling. */
                    if (pendingWrites.size() >= size && wait < longestWait) {
                        wait += initialWait;
                    }
                    size = pendingWrites.size();

                    /* Make sure the SelectionKey is still valid. */
                    if (key.isValid() == false) {
                        logger.severe("Connection terminated while emptying "
                                + "send buffer!");
                        return;
                    }
                }

            } while (pendingWrites.isEmpty() == false);
        }
    }

    /**
     * Broadcasts a message to the connected servers.
     */
    public void broadcastMessage(GalileoMessage message)
    throws IOException {
        for (NetworkDestination dest : connections.values()) {
            this.sendMessage(dest, message);
        }
    }

    /**
     * Sends a message to the specified network destination.  Connections are
     * completed lazily during the first send operation.
     */
    public void sendMessage(NetworkDestination destination,
            GalileoMessage message)
    throws IOException {

        SocketChannel channel = connectedHosts.get(destination);
        if (channel == null) {
            throw new IOException("Not connected to destination: "
                    + destination);
        }

        SelectionKey key = channel.keyFor(this.selector);
        if (key == null) {
            ByteBuffer payload
                = ByteBuffer.wrap(Serializer.serialize(message));
            try {
            tt.get(channel).getPendingWriteQueue().put(payload);
            } catch (InterruptedException e) { }
            selector.wakeup();
            return;
        }
//            if (!pendingRegistrations.contains(channel)) {
//                throw new IOException("Not connected to destination: "
//                        + destination);
//            } else {
//                this.selector.wakeup();
//
//                try {
//                    key = waitingKeys.get(channel).take();
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    throw new IOException("Sender thread interrupted.");
//                }
//
//                if (!key.isValid()) {
//                    throw new IOException("Connection refused.");
//                }
//            }
//        }
//
        super.sendMessage(key, message);
    }

    /**
     * Inform subscribed MessageListener instances that the connection to the
     * remote server has been terminated.
     *
     * @param key SelectionKey for the SocketChannel that was disconnected.
     */
    @Override
    protected void disconnect(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        NetworkDestination destination = connections.get(channel);
        connectedHosts.remove(destination);
        connections.remove(channel);
        super.disconnect(key);
    }
}
