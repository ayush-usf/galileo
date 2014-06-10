
package galileo.samples.net;

import java.io.IOException;

import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.ServerMessageRouter;

/**
 * Very simple demo server that receives and prints String-based messages from
 * clients.
 */
public class SimpleServer implements MessageListener {

    public static final int SERVER_PORT = 7777;

    private ServerMessageRouter messageRouter = new ServerMessageRouter();

    public SimpleServer()
    throws IOException {
        messageRouter.listen(SERVER_PORT);
        messageRouter.addListener(this);
        System.out.println("Listening for incoming messages...");
    }

    @Override
    public void onConnect(NetworkDestination endpoint) { }

    @Override
    public void onDisconnect(NetworkDestination endpoint) { }

    @Override
    public void onMessage(GalileoMessage message) {
        /* Print out the message we received */
        System.out.println(new String(message.getPayload()));
    }

    public static void main(String[] args)
    throws Exception {
        new SimpleServer();
    }
}
