
package galileo.event;

/**
 * Extends the single-threaded reactor implementation defined by
 * {@link EventMapper} to enable multiple worker threads for processing events
 * concurrently.  This assumes that the object with event handlers can deal
 * with being accessed by multiple threads concurrently.
 * <p>
 * In all likelihood, spinning up threads when necessary using the
 * single-threaded EventMapper would be an easier and fairly performant
 * alternative to using this class.
 *
 * @author malensek
 */
public class ConcurrentEventReactor extends EventReactor {

    private int poolSize;

    /**
     * @param handlerObject an Object instance that contains the implementations
     * for event handlers, denoted by the {@link EventHandler} annotation.
     * @param eventMap a EventMap implementation that provides a mapping from
     * integer identification numbers to specific classes that represent an
     * event.
     * @param poolSize the number of worker threads this concurrent event mapper
     * should maintain.
     */
    public ConcurrentEventReactor(
            Object handlerObject, EventMap eventMap, int poolSize) {
        super(handlerObject, eventMap);
        this.poolSize = poolSize;
    }
}
