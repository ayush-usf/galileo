public class StoreNetCDF {

    private ClientMessageRouter messageRouter;
    private EventPublisher publisher;

    public TextClient() throws IOException {
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

    public static void main(String[] args) {

    }

}
