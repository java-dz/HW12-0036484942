package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * A token is a lexical unit that groups one or more consecutive characters.
 * One token has its token type and holds its value. This class offers one
 * constructor that accepts these parameters and two getters, one that returns
 * the type of the token and one that returns its value.
 *
 * @author Mario Bobic
 */
public class SSToken {
	
	/** Type of the token. */
	private final SSTokenType type;
	/** Value that this token holds. */
	private final Object value;
	
	/**
	 * Constructs an instance of SSToken with the given token type and value.
	 * 
	 * @param type type of the token
	 * @param value value of the token
	 */
	public SSToken(SSTokenType type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	/**
	 * Returns the type of this token.
	 * 
	 * @return the type of this token
	 */
	public SSTokenType getType() {
		return type;
	}
	
	/**
	 * Returns the value of this token.
	 * 
	 * @return the value of this token
	 */
	public Object getValue() {
		return value;
	}
	
}
