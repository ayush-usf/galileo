package galileo.comm;

import galileo.event.EventMap;

public class GalileoEventMap extends EventMap {
    public GalileoEventMap() {
        addMapping(StorageEvent.class);
        addMapping(StorageRequest.class);

        addMapping(QueryEvent.class);
        addMapping(QueryRequest.class);
        addMapping(QueryPreamble.class);
        addMapping(QueryResponse.class);
    }
}
