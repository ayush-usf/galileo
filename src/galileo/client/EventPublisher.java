
package galileo.client;

import java.io.IOException;

import galileo.event.EventContainer;
import galileo.event.GalileoEvent;

import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;

import galileo.serialization.Serializer;

/**
 * Handles publishing events from a client to a server.
 *
 * @author malensek
 */
public class EventPublisher {

    private ClientMessageRouter router;

    /**
     * Creates a new EventPublisher instance using the provided
     * {@link ClientMessageRouter} instance for communications.
     */
    public EventPublisher(ClientMessageRouter router) {
        this.router = router;
    }

    /**
     * Publishes a {@link GalileoEvent} via the client's
     * {@link ClientMessageRouter}.
     *
     * @return identification number of the event.
     */
    public int publish(GalileoEvent event)
    throws IOException {
        EventContainer container = new EventContainer(event);
        byte[] messagePayload = Serializer.serialize(container);
        GalileoMessage message = new GalileoMessage(messagePayload);
        router.sendMessage(message);

        return container.getEventId();
    }
}
