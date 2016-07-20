package edu.colostate.cs.galileo.net;

public class GalileoMessage {

    private byte[] payload;
    private MessageContext context;

    /**
     * Constructs an GalileoMessage from an array of bytes.
     *
     * @param payload message payload in the form of a byte array.
     */
    public GalileoMessage(byte[] payload) {
        this.payload = payload;
    }

    public GalileoMessage(byte[] payload, MessageContext context) {
        this(payload);
        this.context = context;
    }

    /**
     * Retrieves the payload for this GalileoMessage.
     *
     * @return the GalileoMessage payload
     */
    public byte[] payload() {
        return payload;
    }

    public MessageContext context() {
        return context;
    }
}
