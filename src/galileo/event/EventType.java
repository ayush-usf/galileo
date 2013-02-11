
package galileo.event;

import java.util.HashMap;
import java.util.Map;

public enum EventType {
    UNKNOWN        (0),
    GENERAL        (1),
    QUERY          (2),
    QUERY_RESPONSE (3),
    STORAGE        (4),
    SYSTEM         (5),
    DEBUG          (6);

    private final int type;

    private EventType(int type) {
        this.type = type;
    }

    public int toInt() {
        return type;
    }

    static Map<Integer, EventType> typeMap = new HashMap<>();

    static {
        for (EventType t : EventType.values()) {
            typeMap.put(t.toInt(), t);
        }
    }

    public static EventType fromInt(int i) {
        EventType t = typeMap.get(i);
        if (t == null) {
            return EventType.UNKNOWN;
        }

        return t;
    }

}
