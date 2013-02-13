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

package galileo.dht;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.event.EventContainer;
import galileo.event.EventType;
import galileo.event.StorageEvent;

import galileo.fs.FileSystem;
import galileo.fs.FileSystemException;

import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.ServerMessageRouter;

import galileo.serialization.Serializer;

import galileo.util.Version;

/**
 * Primary communication component in the Galileo DHT.  StorageNodes service
 * client requests and communication from other StorageNodes to disseminate
 * state information throughout the DHT.
 *
 * @author malensek
 */
public class StorageNode implements MessageListener {

    private static final Logger logger = Logger.getLogger("galileo");

    private int port;

    private ServerMessageRouter messageRouter;

    private static final int THREADS = 4;
    private Scheduler scheduler;

    private FileSystem fs;

    public StorageNode(int port) {
        this.port = port;
    }

    /**
     * Begins Server execution.
     */
    public void start()
    throws IOException {
        Version.printSplash();
        System.out.println("Storage node starting.");

        /* Initialize the Scheduler */
        scheduler = new QueueScheduler(THREADS);

        /* Set up the FileSystem. */
        try {
            setupFileSystem();
        } catch (FileSystemException e) {
            logger.log(Level.SEVERE,
                    "Could not initialize the Galileo File System!", e);
            return;
        }

        /* Start listening for incoming messages. */
        messageRouter = new ServerMessageRouter(port);
        messageRouter.addListener(this);
        messageRouter.listen();
    }

    /**
     * Initialize the Galileo file system.
     *
     * The following are used to determine where files should be stored:
     * 1. GALILEO_ROOT environment variable
     * 2. storageDirectory system property
     * 3. Current working directory
     */
    private void setupFileSystem()
    throws FileSystemException {
        String storageRoot = System.getenv("GALILEO_ROOT");
        if (storageRoot == null) {
            storageRoot = System.getProperty("storageDirectory", ".");
        }

        fs = new FileSystem(storageRoot);
        fs.recoverMetadata();
    }

    @Override
    public void onMessage(GalileoMessage message) {
        try {
            EventContainer container = Serializer.deserialize(
                    EventContainer.class, message.getPayload());

            EventHandler handler = getHandler(container);
            if (handler == null) {
                EventType type = container.getEventType();
                logger.log(Level.WARNING,
                        "No handler found for event type " + type.toInt());
                return;
            }

            handler.message = message;
            handler.eventContainer = container;

            scheduler.schedule(handler);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to process incoming message", e);
        }
    }

    /**
     * Provides a mapping between events (implementations of
     * {@link GalileoEvent}) and their respective {@link EventHandler}s.
     */
    private EventHandler getHandler(EventContainer container) {
        EventType type = container.getEventType();

        switch (type) {
            case STORAGE: return new storageHandler();
            case QUERY: return new queryHandler();
            default: return null;
        }
    }

    private class storageHandler extends EventHandler {
        public void handleEvent() {
            try {
                StorageEvent store = Serializer.deserialize(StorageEvent.class,
                        eventContainer.getEventPayload());

                fs.storeBlock(store.getBlock());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error handling storage event", e);
            }
        }
    }

    private class queryHandler extends EventHandler {
        public void handleEvent() {
        //Query query = Serializer.deserialize(Query.class, packet.getPayload());
        //String queryString = new String(packet.getPayload());

//        byte[] queryResult = storageNode.query(query.getQueryString());
//
//        ExchangePacket resultPacket =
//            new ExchangePacket(PacketType.QUERY_RESPONSE, queryResult, false);
//
//        Results results = map.createResults(true, true);
//        results.setResultPayload(Serializer.serialize(resultPacket));
//        map.writeResults(query.getReplySynopsis(), results);
        }
    }

    /**
     * Executable entrypoint for a Galileo DHT Storage Node
     */
    public static void main(String[] args)
    throws Exception {
        int port = 5555;
        StorageNode node = new StorageNode(port);
        node.start();
    }
}
