
package galileo.fs;

public class FileSystemException extends Exception {

    private static final long serialVersionUID = -4470713548596451442L;


    /**
     * Constructs a FileSystemException with no detail message.
     */
    public FileSystemException() {
        super();
    }


    /**
     * Constructs an FileSystemException with the specified detail message.
     *
     * @param s
     *            the detail message.
     */
    public FileSystemException(String s) {
        super(s);
    }
}
