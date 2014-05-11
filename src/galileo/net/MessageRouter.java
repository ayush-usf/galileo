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

import java.net.Socket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.serialization.Serializer;

/**
 * Provides an abstract implementation for consuming and publishing messages on
 * both the server and client side.
 *
 * @author malensek
 */
public abstract class MessageRouter implements Runnable {

    protected static final Logger logger = Logger.getLogger("galileo");

    /** The default read buffer size is 8 MB. */
    public static final int DEFAULT_READ_BUFFER_SIZE = 8388608;

    /** The default write queue allows 100 items to be inserted before it
     * starts blocking.  This prevents situations where the MessageRouter is
     * overwhelmed by an extreme number of write requests, exhausting available
     * resources. */
    public static final int DEFAULT_WRITE_QUEUE_SIZE = 100;

    /** System property that overrides the read buffer size. */
    public static final String READ_BUFFER_PROPERTY
        = "galileo.net.MessageRouter.readBufferSize";

    /** System property that overrides the write queue maximum size. */
    public static final String WRITE_QUEUE_PROPERTY
        = "galileo.net.MessageRouter.writeQueueSize";

    protected boolean online;

    private List<MessageListener> listeners = new ArrayList<>();

    protected Selector selector;

    private Set<SelectionKey> pendingWriters
        = Collections.synchronizedSet(new HashSet<SelectionKey>());

    protected int readBufferSize;
    protected int writeQueueSize;
    private ByteBuffer readBuffer;

    public MessageRouter() {
        this(DEFAULT_READ_BUFFER_SIZE, DEFAULT_WRITE_QUEUE_SIZE);
    }

    public MessageRouter(int readBufferSize, int maxWriteQueueSize) {
        String readSz = System.getProperty(READ_BUFFER_PROPERTY);
        if (readSz == null) {
            this.readBufferSize = readBufferSize;
        } else {
            this.readBufferSize = Integer.parseInt(readSz);
        }

        String queueSz = System.getProperty(WRITE_QUEUE_PROPERTY);
        if (queueSz == null) {
            this.writeQueueSize = maxWriteQueueSize;
        } else {
            this.writeQueueSize = Integer.parseInt(queueSz);
        }

        readBuffer = ByteBuffer.allocateDirect(this.readBufferSize);
    }

    /**
     * As long as the MessageRouter is online, monitor connection operations
     * through the Selector instance.
     */
    @Override
    public void run() {
        while (online) {
            try {
                processPendingWriters();
                processSelectionKeys();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error in selector thread", e);
            }
        }
    }

    protected void processPendingWriters() {
        synchronized (pendingWriters) {
            Iterator<SelectionKey> it = pendingWriters.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                TransmissionTracker tracker = TransmissionTracker.fromKey(key);
                if (tracker.getPendingWriteQueue().isEmpty() == true) {
                    continue;
                }

                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            }
        }
    }

    /**
     * Performs a select operation, and then processes the resulting
     * SelectionKey set based on interest ops.
     */
    protected void processSelectionKeys()
    throws IOException {

        selector.select();

        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
            SelectionKey key = keys.next();
            keys.remove();

            if (key.isValid() == false) {
                continue;
            }

            try {

                if (key.isAcceptable()) {
                    accept(key);
                    continue;
                }

                if (key.isConnectable()) {
                    connect(key);
                    continue;
                }

                if (key.isWritable()) {
                    write(key);
                }

                if (key.isReadable()) {
                    read(key);
                }

            } catch (CancelledKeyException e) {
                /* SelectionKey was cancelled by another thread. */
                continue;
            }
        }
    }

    /**
     * Accepts new connections.
     *
     * @param key The SelectionKey for the connecting client.
     */
    protected void accept(SelectionKey key)
    throws IOException {
        ServerSocketChannel servSocket = (ServerSocketChannel) key.channel();
        SocketChannel channel = servSocket.accept();
        TransmissionTracker tracker = new TransmissionTracker(writeQueueSize);
        logger.info("Accepted connection: " + getClientString(channel));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, tracker);

        dispatchConnect(getDestination(channel));
    }

    /**
     * Finishes setting up a connection on a SocketChannel.
     *
     * @param key SelectionKey for the SocketChannel.
     */
    protected void connect(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();

            if (channel.finishConnect()) {
                key.interestOps(SelectionKey.OP_READ);
            }

            dispatchConnect(getDestination(channel));
        } catch (IOException e) {
            logger.log(Level.INFO, "Connection finalization failed", e);
            disconnect(key);
        }
    }

    /**
     * Read data from a SocketChannel.
     *
     * @param key SelectionKey for the SocketChannel.
     */
    protected void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        readBuffer.clear();

        int bytesRead = 0;

        try {
            /* Read data from the channel */
            while ((bytesRead = channel.read(readBuffer)) > 0) {
                readBuffer.flip();
                processIncomingMessage(key);
            }
        } catch (IOException e) {
            /* Abnormal termination */
            disconnect(key);
            return;
        } catch (BufferUnderflowException e) {
            /* Incoming packets lied about their size! */
            logger.log(Level.WARNING, "Incoming packet size mismatch", e);
        }

        if (bytesRead == -1) {
            /* Connection was terminated by the client. */
            disconnect(key);
            return;
        }
    }

    /**
     * Process data received from a client SocketChannel.  This method is
     * chiefly concerned with processing incoming data streams into
     * GalileoMessage packets to be consumed by the system.
     *
     * @param key SelectionKey for the client.
     */
    protected void processIncomingMessage(SelectionKey key) {
        TransmissionTracker transmission = TransmissionTracker.fromKey(key);
        if (transmission.expectedBytes == 0) {
            /* We don't know how much data the client is sending yet.
             * Read the message prefix to determine the payload size. */
            boolean ready = readPrefix(readBuffer, transmission);

            /* Check if we have read the payload size prefix yet.  If
             * not, then we're done for now. */
            if (ready == false) {
                return;
            }
        }

        int readSize = transmission.expectedBytes - transmission.readPointer;
        if (readSize > readBuffer.remaining()) {
            readSize = readBuffer.remaining();
        }

        readBuffer.get(transmission.payload,
                transmission.readPointer, readSize);
        transmission.readPointer += readSize;

        if (transmission.readPointer == transmission.expectedBytes) {
            /* The payload has been read */
            GalileoMessage msg = new GalileoMessage(
                    transmission.payload,
                    new MessageContext(this, key));
            dispatchMessage(msg);
            transmission.resetCounters();

            if (readBuffer.hasRemaining()) {
                /* There is another payload to read */
                processIncomingMessage(key);
            }
        }
    }

    /**
     * Read the payload size prefix from a channel.
     * Each message in Galileo is prefixed with a payload size field; this is
     * read to allocate buffers for the incoming message.
     *
     * @return true if the payload size has been determined; false otherwise.
     */
    protected static boolean readPrefix(ByteBuffer buffer,
            TransmissionTracker transmission) {
        /* Make sure the prefix hasn't already been read. */
        if (transmission.expectedBytes != 0) {
            return true;
        }

        /* Can we determine the payload size in one shot? (1 int = 4 bytes) */
        if (transmission.prefixPointer == 0 && buffer.remaining() >= 4) {
            transmission.expectedBytes = buffer.getInt();
            transmission.allocatePayload();
            return true;
        } else {
            /* Keep reading until we have at least 4 bytes to determine the
             * payload size.  */

            int prefixLeft = 4 - transmission.prefixPointer;
            if (buffer.remaining() < prefixLeft) {
                prefixLeft = buffer.remaining();
            }

            buffer.get(transmission.prefix,
                    transmission.prefixPointer, prefixLeft);
            transmission.prefixPointer += prefixLeft;

            if (transmission.prefixPointer >= 4) {
                ByteBuffer buf = ByteBuffer.wrap(transmission.prefix);
                transmission.expectedBytes = buf.getInt();
                transmission.allocatePayload();
                return true;
            }
        }

        return false;
    }

    /**
     * Attempts to write out directly on a SocketChannel, and, if unsuccessful,
     * registers the OP_WRITE interest op to tell the Selector to deal with the
     * write.  When letting the Selector deal with the write, pending data is
     * added to a blocking queue.  This means that if the queue reaches a set
     * limit, this function may block to prevent queuing too much data.
     *
     * @param key SelectionKey for the channel.
     * @param message GalileoMessage to publish on the channel.
     */
    public void sendMessage(SelectionKey key, GalileoMessage message)
    throws IOException {
        if (this.isOnline() == false) {
            throw new IOException("MessageRouter is not online.");
        }

        TransmissionTracker tracker = TransmissionTracker.fromKey(key);
        ByteBuffer payload = ByteBuffer.wrap(Serializer.serialize(message));
        BlockingQueue<ByteBuffer> pendingWriteQueue
            = tracker.getPendingWriteQueue();

        try {
            pendingWriteQueue.put(payload);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Sender thread interrupted");
        }

        pendingWriters.add(key);

        selector.wakeup();
        return;

    }


    /**
     * When a {@link SelectionKey} is writable, push as much pending data
     * out on the channel as possible.  This method is called when a message
     * couldn't be published directly by its originating thread.
     *
     * @param key {@link SelectionKey} of the channel to write to.
     */
    private void write(SelectionKey key) {
        TransmissionTracker tracker = TransmissionTracker.fromKey(key);
        SocketChannel channel = (SocketChannel) key.channel();
        BlockingQueue<ByteBuffer> pendingWrites
            = tracker.getPendingWriteQueue();


        while (pendingWrites.isEmpty() == false) {
            ByteBuffer buffer = pendingWrites.peek();
            if (buffer == null) {
                break;
            }

            int written = 0;
            while (buffer.hasRemaining()) {
                try {
                    written = channel.write(buffer);
                } catch (IOException e) {
                    /* Broken pipe */
                    disconnect(key);
                    return;
                }

                if (buffer.hasRemaining() == false) {
                    /* Done writing */
                    pendingWrites.remove();
                }

                if (written == 0) {
                    /* Return now, to keep our OP_WRITE interest op set. */
                    return;
                }
            }
        }

        /* At this point, the queue is empty. */
        key.interestOps(SelectionKey.OP_READ);
        return;
    }

    /**
     * Handle termination of connections.
     *
     * @param key The SelectionKey of the SocketChannel that has disconnected.
     */
    protected void disconnect(SelectionKey key) {
        if (key.isValid() == false) {
            return;
        }

        SocketChannel channel = (SocketChannel) key.channel();
        NetworkDestination destination = getDestination(channel);
        logger.info("Terminating connection: " + destination.toString());

        try {
            key.cancel();
            key.channel().close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to disconnect channel", e);
        }

        dispatchDisconnect(destination);
    }

    /**
     * Adds a message listener (consumer) to this MessageRouter.  Listeners
     * receive messages that are published by this MessageRouter.
     *
     * @param listener {@link MessageListener} that will consume messages
     * published by this MessageRouter.
     */
    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    /**
     * Dispatches a message to all listening consumers.
     *
     * @param message {@link GalileoMessage} to dispatch.
     */
    protected void dispatchMessage(GalileoMessage message) {
        for (MessageListener listener : listeners) {
            listener.onMessage(message);
        }
    }

    /**
     * Informs all listening consumers that a connection to a remote endpoint
     * has been made.
     */
    protected void dispatchConnect(NetworkDestination endpoint) {
        for (MessageListener listener : listeners) {
            listener.onConnect(endpoint);
        }
    }

    /**
     * Informs all listening consumers that a connection to a remote endpoint
     * has been terminated.
     */
    protected void dispatchDisconnect(NetworkDestination endpoint) {
        for (MessageListener listener : listeners) {
            listener.onDisconnect(endpoint);
        }
    }

    /**
     * Determines whether or not this MessageRouter is online.  As long as the
     * router is online, the selector thread will continue to run.
     *
     * @return true if the MessageRouter instance is online and running.
     */
    public boolean isOnline() {
        return this.online;
    }

    /**
     * Determines a connection's hostname and port, then concatenates the two
     * values, separated by a colon (:).
     *
     * @param channel Channel to get client information about.
     */
    protected static String getClientString(SocketChannel channel) {
        Socket socket = channel.socket();
        return socket.getInetAddress().getHostName() + ":" + socket.getPort();
    }

    /**
     * Determines a connection's endpoint information (hostname and port) and
     * encapsulates them in a {@link NetworkDestination}.
     *
     * @param channel The SocketChannel of the network endpoint.
     *
     * @return NetworkDestination representation of the endpoint.
     */
    protected static NetworkDestination getDestination(SocketChannel channel) {
        Socket socket = channel.socket();
        return new NetworkDestination(
                socket.getInetAddress().getHostName(),
                socket.getPort());
    }
}
