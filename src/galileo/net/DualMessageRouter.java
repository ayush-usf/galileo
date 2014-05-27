package galileo.net;

import java.io.IOException;

/**
 * A "dual" MessageRouter instance that can act as both a server and a client.
 * This implementation is made up of a {@link ClientMessageRouter} and a
 * {@link ServerMessageRouter} instance, meaning outgoing and incoming messages
 * are processed by separate threads.
 *
 * @author malensek
 */
public class DualMessageRouter {

    private ServerMessageRouter serverRouter;
    private ClientMessageRouter clientRouter;

    public DualMessageRouter() { }

    public void sendMessage(NetworkDestination destination,
            GalileoMessage message)
    throws IOException {
        clientRouter.sendMessage(destination, message);
    }

    public void shutdown()
    throws IOException {
        serverRouter.shutdown();
        clientRouter.shutdown();
    }

    public void addListener(MessageListener listener) {
        serverRouter.addListener(listener);
        clientRouter.addListener(listener);
    }
}
