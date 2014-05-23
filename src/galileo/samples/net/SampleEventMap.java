package galileo.samples.net;

import galileo.event.EventMap;

public class SampleEventMap extends EventMap {

    public SampleEventMap() {
        /* Here we give our events identification numbers */
        addMapping(1, GoodEvent.class);
        addMapping(2, BadEvent.class);
        addMapping(3, BadReplyEvent.class);
        addMapping(4, UglyEvent.class);
    }
}
