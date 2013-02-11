
package galileo.net;

public interface MessageListener {

    /**
     * Called when a message is ready to be processed.
     *
     * @param message GalileoMessage that was received; null if the connection
     * has been terminated.
     */
    public void onMessage(GalileoMessage message);
}
