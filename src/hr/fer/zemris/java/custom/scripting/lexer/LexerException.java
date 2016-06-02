package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Exception that is thrown if an unexpected problem occurs during the lexical
 * analysis.
 *
 * @author Mario Bobic
 */
public class LexerException extends RuntimeException {
	/** Serialization UID. */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs an {@code LexerException} with no
     * detail message and no cause.
     */
	public LexerException() {
		super();
	}

	/**
     * Constructs an {@code LexerException} with the
     * specified detail message.
     *
     * @param message the detail message
     */
	public LexerException(String message) {
		super(message);
	}

	/**
	 * Constructs an {@code LexerException} with the
     * specified cause.
	 * 
	 * @param cause the cause
	 */
	public LexerException(Throwable cause) {
		super(cause);
	}

	/**
     * Constructs an {@code LexerException} with the
     * specified detail message and cause.
     * 
	 * @param message the detail message
	 * @param cause the cause
	 */
	public LexerException(String message, Throwable cause) {
		super(message, cause);
	}

}
