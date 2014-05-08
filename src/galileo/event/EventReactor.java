
package galileo.event;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.serialization.SerializationException;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.Serializer;
import galileo.util.StackTraceToString;

/**
 * Implements the reactor pattern for processing incoming events
 * ({@link GalileoMessage} instances).
 *
 * @author malensek
 */
public class EventReactor implements MessageListener {

    private static final Logger logger = Logger.getLogger("galileo");

    private Class<?> handlerClass;
    private Object handlerObject;

    private EventMap eventMap;

    private Map<Class<?>, Method> classToMethod = new HashMap<>();

    private BlockingQueue<GalileoMessage> messageQueue
        = new LinkedBlockingQueue<>();

    /**
     * @param handlerObject an Object instance that contains the implementations
     * for event handlers, denoted by the {@link EventHandler} annotation.
     * @param eventMap a EventMap implementation that provides a mapping from
     * integer identification numbers to specific classes that represent an
     * event.
     */
    public EventReactor(Object handlerObject, EventMap eventMap) {
        this.handlerClass = handlerObject.getClass();
        this.handlerObject = handlerObject;
        this.eventMap = eventMap;
        System.out.println(eventMap.getClass(1));
    }

    /**
     * This method links incoming event types to their relevant event handlers
     * found in the handlerObject.
     */
    public void linkEventHandlers() {
        classToMethod.clear();

        for (Method m : handlerClass.getMethods()) {
            for (Annotation a : m.getAnnotations()) {
                if (a.annotationType().equals(EventHandler.class)) {
                    /* This method is an event handler */
                    Class<?>[] params = m.getParameterTypes();
                    Class<?> eventClass;
                    try {
                        eventClass = extractEventClass(params);
                    } catch (EventException e) {
                        logger.log(Level.WARNING, "Could not determine type of "
                                + "event handled by method: " + m, e);
                        break;
                    }

                    logger.log(Level.INFO,
                            "Linking handler method [{0}] to class [{1}]",
                            new Object[] { m.getName(), eventClass.getName() });
                    classToMethod.put(eventClass, m);
                    break;
                }
            }
        }
    }

    /**
     * Determines the class responsible for encapsulating an Event.  This is
     * achieved by providing a list of parameter types, where the first
     * parameter will be the the class that represents the event.
     *
     * @param parameters A list of method parameters
     */
    private Class<?> extractEventClass(Class<?>[] parameters)
    throws EventException {
        if (parameters.length <= 0) {
            throw new EventException(
                    "Event handler method does not have any parameters");
        }

        List<Class<?>> interfaces
            = Arrays.asList(parameters[0].getInterfaces());
        if (interfaces.contains(Event.class) == false) {
            throw new EventException("EventHandler parameter does not "
                    + "implement the Event interface");
        }

        return parameters[0];
    }

    /**
     * Retrieves the next message from the queue, and calls the appropriate
     * event handler method to process the message.  If no message is present in
     * the queue, this method will block until one becomes available.
     *
     * @throws EventException when the incoming event is unknown, or errors
     * occur while trying to call the appropriate handler method
     * @throws InterruptedException if the calling thread is interrupted while
     * waiting for a new message to arrive
     */
    public void processNextEvent() throws EventException, IOException,
            InterruptedException, SerializationException {

        GalileoMessage message = messageQueue.take();
        ByteArrayInputStream byteIn
            = new ByteArrayInputStream(message.getPayload());
        BufferedInputStream buffIn = new BufferedInputStream(byteIn);
        SerializationInputStream in = new SerializationInputStream(buffIn);

        try {
            int type = in.readInt();
            Class<? extends Event> clazz = eventMap.getClass(type);
            if (clazz == null) {
                in.close();
                throw new EventException(
                        "Class mapping for event type not found!");
            }

            Event e = Serializer.deserializeFromStream(clazz, in);
            Method m = classToMethod.get(clazz);
            m.invoke(handlerObject, e);
        } catch (IOException | SerializationException e) {
            throw e;
        } catch (Exception e) {
            /* Propagating all the possible reflection-related exceptions up to
             * clients seemed undesirable from a usability perspective here, so
             * we wrap this up in a catch-all exception. */
            throw new EventException("Error processing event!  "
                    + StackTraceToString.convert(e));
        } finally {
            in.close();
        }
    }


    @Override
    public void onConnect(NetworkDestination endpoint) {

    }

    @Override
    public void onDisconnect(NetworkDestination endpoint) {

    }

    @Override
    public void onMessage(GalileoMessage message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            logger.warning("Interrupted during onMessage delivery");
            Thread.currentThread().interrupt();
        }
    }
}
