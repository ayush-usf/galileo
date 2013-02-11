
package galileo.event;

public interface EventListener {

    /**
     * Called when an event is ready to be processed.
     *
     * @param event GalileoEvent that was received; null if the connection has
     * been terminated.
     */
    public void onEvent(GalileoEvent event);
}
