package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * This enumeration describes the type of a token which is used by the
 * {@link SSLexer}. A token may be <tt>TEXT</tt>, <tt>TAG_FOR</tt>,
 * <tt>TAG_END</tt> and <tt>TAG_ECHO</tt> which all contain a value, or
 * <tt>EOF</tt> that indicates the end of data and does not contain a value.
 *
 * @author Mario Bobic
 * @see SSLexer
 */
public enum SSTokenType {

	/**
	 * Represents a token consisted of any characters. Text tokens can have the
	 * '\' characters which may be considered escape-sequences and it is up to
	 * a lexer or parser to decide whether this is a valid text token or not.
	 */
	TEXT,
	
	/**
	 * Represents a token created from a tag that starts with a <tt>FOR</tt>
	 * (case-insensitive) word and contains 0 or more characters. The token is
	 * to be in raw format without the <tt>FOR</tt> keyword.
	 */
	TAG_FOR,
	
	/**
	 * Represents a token created from a tag that contains only the <tt>END</tt>
	 * (case-insensitive) word. The token value should be <tt>null</tt> because
	 * this tag does not contain anything other than the <tt>END</tt> keyword.
	 */
	TAG_END,
	
	/**
	 * Represents a token created from a tag that starts with <tt>=</tt> symbol
	 * and contains 0 or more characters. The token is to be in raw format
	 * without the <tt>=</tt> symbol.
	 */
	TAG_ECHO,
	
//	VARIABLE,
//	
//	FUNCTION,
//	
//	CONSTANT_INTEGER,
//	
//	CONSTANT_DOUBLE,
//	
//	STRING,
//	
//	OPERATOR,
	
	/**
	 * Indicates the end of data and does not contain a value.
	 */
	EOF
}
