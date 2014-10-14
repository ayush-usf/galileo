package galileo.comm;

import galileo.event.EventMap;

public class GalileoEventMap extends EventMap {
    public GalileoEventMap() {
        addMapping(10, DebugEvent.class);

        addMapping(100, StorageEvent.class);
        addMapping(101, StorageRequest.class);

        addMapping(200, QueryEvent.class);
        addMapping(201, QueryRequest.class);
        addMapping(202, QueryPreamble.class);
        addMapping(203, QueryResponse.class);
    }
}
