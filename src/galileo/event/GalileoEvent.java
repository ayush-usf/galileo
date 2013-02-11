
package galileo.event;

import galileo.serialization.ByteSerializable;

public interface GalileoEvent extends ByteSerializable {

    /**
     * Returns the {@link EventType} of this event.
     *
     * @return EventType
     */
    public EventType getType();
}
