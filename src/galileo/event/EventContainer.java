
package galileo.event;

import java.io.IOException;

import galileo.serialization.ByteSerializable;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;
import galileo.serialization.Serializer;

/**
 * General container that wraps various communication events in Galileo.
 * An EventContainer includes the type of event ({@link EventType}), an
 * identification number for the event, and a byte array representing the event
 * contents.
 *
 * Event identification numbers are used for non-blocking transactional
 * communications between a client and server; the ID number can be used to
 * track a specific transaction.
 *
 * Given a {@link GalileoEvent}, the EventContainer will serialize the event
 * contents to use as the payload for the container.
 */
public class EventContainer implements ByteSerializable {

    private static int idCounter;

    private EventType type;
    private int id;
    private byte[] payload;

    /**
     * Create a new EventContainer for the given event, and generate an ID
     * number for the event.
     *
     * <em>WARNING:</em> using a combination of user-specified IDs and
     * program-generated IDs will likely result in erratic behavior.
     */
    public EventContainer(GalileoEvent event)
    throws IOException {
        this(event, incrementIDCounter());
    }

    /**
     * Create a new EventContainer for the given event, with a user-specified
     * identification number for the event.
     *
     * <em>WARNING:</em> using a combination of user-specified IDs and
     * program-generated IDs will likely result in erratic behavior.
     */
    public EventContainer(GalileoEvent event, int eventId)
    throws IOException {
        this.type = event.getType();
        this.id = eventId;
        this.payload = Serializer.serialize(event);
    }

    /**
     * Increment the automatic ID generator and return the result.
     */
    private static synchronized int incrementIDCounter() {
        if (idCounter < 0) {
            idCounter = 0;
        }
        idCounter++;
        return idCounter;
    }

    public EventType getEventType() {
        return type;
    }

    public int getEventId() {
        return id;
    }

    public byte[] getEventPayload() {
        return payload;
    }

    public EventContainer(SerializationInputStream in)
    throws IOException {
        type = EventType.fromInt(in.readInt());
        id = in.readInt();
        payload = in.readField();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeInt(type.toInt());
        out.writeInt(id);
        out.writeField(payload);
    }
}
