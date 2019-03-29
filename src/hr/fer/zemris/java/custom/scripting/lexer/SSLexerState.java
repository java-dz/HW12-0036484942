package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * This enumeration describes the SSLexer's state. The <tt>READING_TEXT</tt>
 * state reads text starting from where it last ended until the first occurrence
 * of the tag opening bracket sequence or the end of document. The
 * <tt>READING_TAGS</tt> state reads tags starting from from where it last ended
 * until the first occurrence of the tag closing bracket sequence.
 *
 * @author Mario Bobic
 */
public enum SSLexerState {

    /**
     * This state reads text starting from where it last ended until the first
     * occurrence of the tag opening bracket sequence or the end of document.
     */
    READING_TEXT,

    /**
     * This state reads tags starting from from where it last ended until the
     * first occurrence of the tag closing bracket sequence.
     */
    READING_TAGS
}
