
package galileo.serialization;

public class SerializationException extends Exception {

    /**
     * Constructs a <code>SerializationException</code> with no detail
     * message.
     */
    public SerializationException() {
        super();
    }

    /**
     * Constructs a <code>SerializationException</code> with the specified
     * detail message.
     *
     * @param s
     *            the detail message.
     */
    public SerializationException(String s) {
        super(s);
    }
}
