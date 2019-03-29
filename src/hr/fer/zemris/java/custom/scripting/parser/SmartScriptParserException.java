package hr.fer.zemris.java.custom.scripting.parser;

/**
 * Exception that is thrown if an unexpected problem occurs during the smart
 * script parsing.
 *
 * @author Mario Bobic
 */
public class SmartScriptParserException extends RuntimeException {
    /** Serialization UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an {@code SmartScriptParserException} with no
     * detail message and no cause.
     */
    public SmartScriptParserException() {
        super();
    }

    /**
     * Constructs an {@code SmartScriptParserException} with the
     * specified detail message.
     *
     * @param message the detail message.
     */
    public SmartScriptParserException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code SmartScriptParserException} with the
     * specified cause.
     *
     * @param cause the cause
     */
    public SmartScriptParserException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an {@code SmartScriptParserException} with the
     * specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public SmartScriptParserException(String message, Throwable cause) {
        super(message, cause);
    }

}
