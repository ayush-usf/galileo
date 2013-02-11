
package galileo.dataset;

public class DatasetException extends Exception {

    private static final long serialVersionUID = -1478139705050339002L;

    /**
     * Constructs an <code>DatasetException</code> with no detail
     * message.
     */
    public DatasetException() {
        super();
    }


    /**
     * Constructs an <code>DatasetException</code> with the specified
     * detail message.
     *
     * @param s
     *            the detail message.
     */
    public DatasetException(String s) {
        super(s);
    }
}
