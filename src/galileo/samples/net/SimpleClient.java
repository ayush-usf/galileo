package galileo.samples.net;

import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.NetworkDestination;

/**
 * Very simple demo client that sends a message to a server.
 */
public class SimpleClient {

    public static void main(String[] args)
    throws Exception {

        if (args.length < 2) {
            System.out.println("Usage: galileo.samples.net.SimpleClient "
                    + "<server> <message>");
            System.exit(1);
        }

        String serverHostName = args[0];
        String str = args[1];

        NetworkDestination server = new NetworkDestination(
                serverHostName, SimpleServer.SERVER_PORT);

        ClientMessageRouter messageRouter = new ClientMessageRouter();

        GalileoMessage message = new GalileoMessage(str.getBytes());

        messageRouter.sendMessage(server, message);
    }
}
