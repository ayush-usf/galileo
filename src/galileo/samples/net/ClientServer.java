package galileo.samples.net;

import java.io.IOException;

import java.util.Random;

import galileo.net.IOMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.MessageContext;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;

/**
 * Demonstration of a class that acts as both a client and server: it listens
 * for messages on a specific port while also periodically sending messages to
 * another server.
 * <p>
 * Sample usage:<br>
 * galileo.samples.net.ClientServer 7000 lattice-0 7001 hello <br>
 * galileo.samples.net.ClientServer 7001 lattice-1 7000 hola!
 */
public class ClientServer implements MessageListener {

    /** The port we will listen on. */
    private int port;

    /** The remove server we will connect to */
    private NetworkDestination server;

    private Random random = new Random();
    private IOMessageRouter messageRouter;

    public ClientServer(int port, NetworkDestination server) {
        this.port = port;
        this.server = server;
    }

    public void start(String data)
    throws IOException {
        /* Bind to the port we'll listen for messages on. */
        messageRouter = new IOMessageRouter();
        messageRouter.listen(this.port);
        messageRouter.addListener(this);

        /* Start sending messages to the other server */
        while (true) {
            byte[] messageData = data.getBytes();
            GalileoMessage message = new GalileoMessage(messageData);

            messageRouter.sendMessage(server, message);

            try {
                Thread.sleep(random.nextInt(5000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnect(NetworkDestination endpoint) { }

    @Override
    public void onDisconnect(NetworkDestination endpoint) { }

    @Override
    public void onMessage(GalileoMessage message) {
        MessageContext context = message.getContext();
        System.out.println("Got message on port " + context.getServerPort()
                + " from " + context.getSource() + ": "
                + new String(message.getPayload()));
    }

    public static void main(String[] args)
    throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: galileo.samples.net.ClientServer "
                    + "<local port> <server hostname> <server port> <msg>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        NetworkDestination server = new NetworkDestination(
                args[1], Integer.parseInt(args[2]));

        ClientServer cs = new ClientServer(port, server);
        cs.start(args[3]);
    }
}
