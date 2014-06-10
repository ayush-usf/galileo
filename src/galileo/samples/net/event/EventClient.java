package galileo.samples.net.event;

import java.io.IOException;

import galileo.event.EventReactor;
import galileo.net.ClientMessageRouter;

public class EventClient {

    private ClientMessageRouter messageRouter;
    private EventReactor eventReactor;

    public EventClient()
    throws IOException {
        messageRouter = new ClientMessageRouter();
        eventReactor = new EventReactor(this, SampleEventMap.instance());
    }

    public static void main(String[] args) {


    }
}
