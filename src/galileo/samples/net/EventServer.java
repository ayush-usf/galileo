package galileo.samples.net;

import java.io.IOException;

import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.ServerMessageRouter;

public class EventServer {

    public static final int LISTEN_PORT = 7777;

    private int port;

    private ServerMessageRouter messageRouter;

    private EventReactor eventReactor;
    private SampleEventMap eventMap;

    public EventServer(int port) {
        this.port = port;
    }

    public void start()
    throws IOException {
        eventMap = new SampleEventMap();
        eventReactor = new EventReactor(this, this.eventMap);
        messageRouter = new ServerMessageRouter(this.port);
        messageRouter.addListener(eventReactor);
        messageRouter.listen();

        while (true) {
            try {
                eventReactor.processNextEvent();
            } catch (Exception e) {
                System.out.println("Error processing event!");
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void processGoodEvent(GoodEvent event, EventContext context) {
        System.out.println("We got a GoodEvent!");
        System.out.println("Let's see what it says.");
        System.out.println("Contents: " + event.getData());
    }

    public static void main(String[] args)
    throws IOException {
        EventServer server = new EventServer(LISTEN_PORT);
        server.start();
    }
}
