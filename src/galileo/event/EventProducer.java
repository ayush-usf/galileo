package galileo.event;

import java.io.IOException;

import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.NetworkDestination;

/**
 * This class makes it easy to publish events from a client to a server by
 * linking a {@link ClientMessageRouter} instance to a {@link EventReactor}
 * instance.  This helps avoid message wrapping boilerplate every time an event
 * will be sent.
 *
 * @author malensek
 */
public class EventProducer {

    private ClientMessageRouter router;
    private EventReactor reactor;

    public EventProducer(ClientMessageRouter router, EventReactor reactor) {
        this.router = router;
        this.reactor = reactor;
    }

    /**
     * @param destination The server to publish the event to.
     * @param e Event to be published.
     */
    public void publishEvent(NetworkDestination destination, Event e)
    throws IOException {
        GalileoMessage m = reactor.wrapEvent(e);
        router.sendMessage(destination, m);
    }
}
