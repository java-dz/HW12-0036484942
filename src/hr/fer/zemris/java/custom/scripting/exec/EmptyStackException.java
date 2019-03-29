package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Exception that is thrown when client tries to <code>pop</code> or
 * <code>peek</code> an empty stack.
 *
 * @author Mario Bobic
 */
public class EmptyStackException extends RuntimeException {
    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an {@code EmptyStackException} with no
     * detail message and no cause.
     */
    public EmptyStackException() {
        super();
    }

    /**
     * Constructs an {@code EmptyStackException} with the
     * specified detail message.
     *
     * @param message the detail message.
     */
    public EmptyStackException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code EmptyStackException} with the
     * specified cause.
     *
     * @param cause the cause
     */
    public EmptyStackException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an {@code EmptyStackException} with the
     * specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public EmptyStackException(String message, Throwable cause) {
        super(message, cause);
    }

}
