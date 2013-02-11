
package galileo.dht;

import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.event.EventContainer;
import galileo.event.GalileoEvent;

import galileo.net.GalileoMessage;

public abstract class EventHandler implements ProcessingUnit {

    private static final Logger logger = Logger.getLogger("galileo");

    public GalileoMessage message;
    public EventContainer eventContainer;

    public EventHandler() { }

    public void run() {
        if (eventContainer == null) {
            logger.warning("Received null event");
            return;
        }

        if (message == null) {
            logger.warning("Reference message is null");
            return;
        }

        try {
            handleEvent();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to process exchange packet!", e);
        }
    }

    public void publishResponse(GalileoEvent event) {

    }

    public abstract void handleEvent();
}
