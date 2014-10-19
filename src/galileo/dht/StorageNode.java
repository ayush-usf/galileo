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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.comm.DebugEvent;
import galileo.comm.GalileoEventMap;
import galileo.comm.QueryEvent;
import galileo.comm.QueryPreamble;
import galileo.comm.QueryRequest;
import galileo.comm.QueryResponse;
import galileo.comm.StorageEvent;
import galileo.comm.StorageRequest;
import galileo.config.SystemConfig;
import galileo.dataset.Block;
import galileo.dataset.Metadata;
import galileo.dataset.feature.Feature;
import galileo.dht.hash.HashException;
import galileo.dht.hash.HashTopologyException;
import galileo.event.Event;
import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.fs.FileSystemException;
import galileo.fs.GeospatialFileSystem;
import galileo.graph.Path;
import galileo.net.ClientConnectionPool;
import galileo.net.HostIdentifier;
import galileo.net.PortTester;
import galileo.net.ServerMessageRouter;
import galileo.util.Version;

/**
 * Primary communication component in the Galileo DHT.  StorageNodes service
 * client requests and communication from other StorageNodes to disseminate
 * state information throughout the DHT.
 *
 * @author malensek
 */
public class StorageNode {

    private static final Logger logger = Logger.getLogger("galileo");
    private StatusLine nodeStatus;

    private int port;
    private String rootDir;

    private File pidFile;

    private NetworkInfo network;

    private ServerMessageRouter messageRouter;
    private ClientConnectionPool connectionPool;
    private GeospatialFileSystem fs;

    private GalileoEventMap eventMap = new GalileoEventMap();
    private EventReactor eventReactor = new EventReactor(this, eventMap);

    private Partitioner<Metadata> partitioner;

    private ConcurrentHashMap<String, QueryTracker> queryTrackers
        = new ConcurrentHashMap<>();

    private String sessionId;

    public StorageNode() {
        this.port = NetworkConfig.DEFAULT_PORT;
        this.rootDir = SystemConfig.getRootDir();

        this.sessionId = HostIdentifier.getSessionId(port);
        nodeStatus = new StatusLine(SystemConfig.getRootDir() + "/status.txt");

        String pid = System.getProperty("pidFile");
        if (pid != null) {
            this.pidFile = new File(pid);
        }
     }

    /**
     * Begins Server execution.  This method attempts to fail fast to provide
     * immediate feedback to wrapper scripts or other user interface tools.
     * Only once all the prerequisite components are initialized and in a sane
     * state will the StorageNode begin accepting connections.
     */
    public void start()
    throws Exception {
        Version.printSplash();

        /* First, make sure the port we're binding to is available. */
        nodeStatus.set("Attempting to bind to port");
        if (PortTester.portAvailable(port) == false) {
            nodeStatus.set("Could not bind to port " + port + ".");
            throw new IOException("Could not bind to port " + port);
        }

        /* Read the network configuration; if this is invalid, there is no need
         * to execute the rest of this method. */
        nodeStatus.set("Reading network configuration");
        network = NetworkConfig.readNetworkDescription(
                SystemConfig.getNetworkConfDir());

        /* Set up the FileSystem. */
        nodeStatus.set("Initializing file system");
        try {
            fs = new GeospatialFileSystem(rootDir);
        } catch (FileSystemException e) {
            nodeStatus.set("File system initialization failure");
            logger.log(Level.SEVERE,
                    "Could not initialize the Galileo File System!", e);
            return;
        }

        nodeStatus.set("Initializing communications");

        /* Set up our Shutdown hook */
        Runtime.getRuntime().addShutdownHook(new ShutdownHandler());

        /* Pre-scheduler setup tasks */
        connectionPool = new ClientConnectionPool();
        connectionPool.addListener(eventReactor);
        configurePartitioner();

        /* Start listening for incoming messages. */
        messageRouter = new ServerMessageRouter();
        messageRouter.addListener(eventReactor);
        messageRouter.listen(port);
        nodeStatus.set("Online");

        /* Start processing the message loop */
        while (true) {
            eventReactor.processNextEvent();
        }
    }

    private void configurePartitioner()
    throws HashException, HashTopologyException, PartitionException {
        String[] geohashes = { "8g", "8u", "8v", "8x", "8y", "8z", "94", "95",
                "96", "97", "9d", "9e", "9g", "9h", "9j", "9k", "9m", "9n",
                "9p", "9q", "9r", "9s", "9t", "9u", "9v", "9w", "9x", "9y",
                "9z", "b8", "b9", "bb", "bc", "bf", "c0", "c1", "c2", "c3",
                "c4", "c6", "c8", "c9", "cb", "cc", "cd", "cf", "d4", "d5",
                "d6", "d7", "dd", "de", "dh", "dj", "dk", "dm", "dn", "dp",
                "dq", "dr", "ds", "dt", "dw", "dx", "dz", "f0", "f1", "f2",
                "f3", "f4", "f6", "f8", "f9", "fb", "fc", "fd", "ff" };

        partitioner = new SpatialHierarchyPartitioner(this, network, geohashes);
    }

    private void sendEvent(NodeInfo node, Event event)
    throws IOException {
        connectionPool.sendMessage(node, eventReactor.wrapEvent(event));
    }

    /**
     * Handles a storage request from a client.  This involves determining where
     * the data belongs via a {@link Partitioner} implementation and then
     * forwarding the data on to its destination.
     */
    @EventHandler
    public void handleStorageRequest(
            StorageRequest request, EventContext context)
    throws HashException, IOException, PartitionException {

        /* Determine where this block goes. */
        Block file = request.getBlock();
        Metadata metadata = file.getMetadata();
        NodeInfo node = partitioner.locateData(metadata);

        logger.log(Level.INFO, "Storage destination: {0}", node);
        StorageEvent store = new StorageEvent(file);
        sendEvent(node, store);
    }

    @EventHandler
    public void handleStorage(StorageEvent store, EventContext context)
    throws FileSystemException, IOException {
        logger.log(Level.INFO, "Storing block: {0}", store.getBlock());
        fs.storeBlock(store.getBlock());
    }

    /**
     * Handles a query request from a client.  Query requests result in a number
     * of subqueries being performed across the Galileo network.
     */
    @EventHandler
    public void handleQueryRequest(QueryRequest request, EventContext context)
    throws IOException {
        String queryString = request.getQueryString();
        logger.log(Level.INFO, "Query request: {0}", queryString);

        /* Determine StorageNodes that contain relevant data. */
        //featureGraph.query(
        List<NodeInfo> queryNodes = new ArrayList<>();
        queryNodes.addAll(network.getAllNodes());

        /* Set up QueryTracker for this request */
//        QueryTracker tracker = new QueryTracker(
//                message.getContext().getSelectionKey());
//        String clientId = tracker.getIdString(sessionId);
//        queryTrackers.put(clientId, tracker);
//
//        /* Send a Query Preamble to the client */
//        QueryPreamble preamble = new QueryPreamble(
//                clientId, queryString, queryNodes);
//        publishResponse(preamble);
//
//        /* Optionally write out where this query is going */
//        if (logger.isLoggable(Level.INFO)) {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Forwarding Query to nodes: ");
//            for (NodeInfo node : queryNodes) {
//                sb.append(node.toString() + " ");
//            }
//            logger.info(sb.toString());
//        }
//
//        QueryEvent query = new QueryEvent(tracker.getIdString(sessionId),
//                request.getQuery());
//        for (NodeInfo node : queryNodes) {
//            publishEvent(query, node);
//        }
    }

    /**
     * Handles an internal Query request (from another StorageNode)
     */
    @EventHandler
    public void handleQuery(QueryEvent query, EventContext context)
    throws IOException {
        logger.info(query.getQuery().toString());

        List<Path<Feature, String>> results = fs.query(query.getQuery());
        logger.info("Got " + results.size() + " results");

        QueryResponse response = new QueryResponse(
                query.getQueryId(), results);
        context.sendReply(response);
    }

    @EventHandler
    public void handleQueryResponse(
            QueryResponse response, EventContext context)
    throws IOException {
        QueryTracker tracker = queryTrackers.get(response.getId());
        if (tracker == null) {
            logger.log(Level.WARNING,
                    "Unknown query response received: {0}",
                    response.getId());
            return;
        }
        //sendMessage(tracker.getSelectionKey(), message);
    }

    /**
     * Handles cleaning up the system for a graceful shutdown.
     */
    private class ShutdownHandler extends Thread {
        @Override
        public void run() {
            /* The logging subsystem may have already shut down, so we revert to
             * stdout for our final messages */
            System.out.println("Initiated shutdown.");

            try {
                connectionPool.forceShutdown();
                messageRouter.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }

            nodeStatus.close();

            if (pidFile != null && pidFile.exists()) {
                pidFile.delete();
            }

            fs.shutdown();

            System.out.println("Goodbye!");
        }
    }

    /**
     * Executable entrypoint for a Galileo DHT Storage Node
     */
    public static void main(String[] args) {
        StorageNode node = new StorageNode();
        try {
            node.start();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not start StorageNode.", e);
        }
    }
}
