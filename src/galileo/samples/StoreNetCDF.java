package galileo.samples;

import java.io.IOException;

import java.net.UnknownHostException;

import galileo.client.EventPublisher;
import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;

public class StoreNetCDF implements MessageListener {

    private ClientMessageRouter messageRouter;
    private EventPublisher publisher;

    public StoreNetCDF() throws IOException {
        messageRouter = new ClientMessageRouter();
        publisher = new EventPublisher(messageRouter);

        messageRouter.addListener(this);
    }

    public NetworkDestination connect(String hostname, int port)
    throws UnknownHostException, IOException {
        return messageRouter.connectTo(hostname, port);
    }

    public void disconnect() {
        messageRouter.shutdown();
    }

    @Override
    public void onMessage(GalileoMessage message) {
        if (message == null) {
            /* Connection was terminated */
            messageRouter.shutdown();
            return;
        }
    }

    public static void main(String[] args) {

    }

}
