package hr.fer.zemris.java.custom.scripting.lexer;

import java.util.Scanner;
import java.util.regex.Pattern;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods;
import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.*;

/**
 * A Lexer is a program that performs lexical analysis. SSLexer is combined with
 * a parser, which together analyze the syntax and extract tokens from the input
 * text.
 * <p>
 * SSLexer provides one constructor which accepts an input text to be tokenized.
 * The input text is analyzed and tokens are made depending on the token type.
 * It also provides a method for generating the next token and a method that
 * returns the last generated token.
 * <p>
 * SSLexer has two states, {@link SSLexerState#READING_TEXT READING_TEXT} and
 * {@link SSLexerState#READING_TAGS READING_TAGS}. The <tt>READING_TEXT</tt>
 * state reads text starting from where it last ended until the first occurrence
 * of the tag opening bracket sequence or the end of document. The
 * <tt>READING_TAGS</tt> state reads tags starting from from where it last ended
 * until the first occurrence of the tag closing bracket sequence. If there is
 * no tag closing bracket sequence or if a non-existent tag occurs, a
 * {@linkplain LexerException} is thrown.
 * <p>
 * SSLexer generates tokens defined in the {@link SSTokenType} enumeration.
 *
 * @author Mario Bobic
 * @see SSTokenType
 * @see SSLexerState
 */
public class SSLexer {

    /**
     * A pattern for delimiting text from tags. This pattern's regular
     * expression uses Negative Look-Behind to see if the tag opening bracket
     * has been escaped. Described regular expression: <tt>(?&lt;!\\)\{\$</tt>
     */
    private static final Pattern TEXT_DELIMITER = Pattern.compile("(?<!\\\\)\\{\\$");

    /**
     * A pattern for delimiting tags from text. This pattern's regular
     * expression is the following: <tt>\$\}</tt>
     */
    private static final Pattern TAG_DELIMITER = Pattern.compile("\\$\\}");

    /** Input text for tokenization. */
    private final char[] data;
    /** Current token. */
    private SSToken token;
    /** Index of the first character to process. */
    private int currentIndex;
    /** Current state of Lexer. */
    private SSLexerState state;

    /**
     * Constructs an instance of SmartScriptLexer with the given input text to be
     * tokenized.
     *
     * @param text text for tokenization
     * @throws IllegalArgumentException if the input text is null
     */
    public SSLexer(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null.");
        }

        data = text.toCharArray();
        currentIndex = 0;
        state = SSLexerState.READING_TEXT;
        checkCurrentState();
    }

    /**
     * Generates and returns the next token. Tokens are generated from the input
     * text and their type depends on the input text. If the input text is
     * exhausted or reading error occurs, a {@linkplain LexerException} is
     * thrown.
     * <p>
     * A reading error may occur if there is no tag closing bracket sequence or
     * if a non-existent tag occurs.
     *
     * @return the next token generated from the input text
     * @throws LexerException if there is no next token or if reading error occurs
     */
    public SSToken nextToken() {
        if (token != null && token.getType() == SSTokenType.EOF) {
            throw new LexerException("No next token after EOF.");
        }

        if (isDataEnd()) {
            token = new SSToken(SSTokenType.EOF, null);
        } else {
            if (state == SSLexerState.READING_TEXT) {
                token = getText();
            } else {
                token = getTag();
            }
        }

        return token;
    }

    /**
     * Returns the last generated token. This method may be called multiple
     * times because it does not generate the next token.
     *
     * @return the last generated token
     */
    public SSToken getToken() {
        return token;
    }

    /**
     * Sets the current state of Lexer.
     *
     * @param state state to be set
     * @throws IllegalArgumentException if the given state is <tt>null</tt>
     * @see SSLexerState
     */
    public void setState(SSLexerState state) {
        if (state == null) {
            throw new IllegalArgumentException("State must not be null.");
        }
        this.state = state;
    }

    /**
     * Returns text token starting from the <tt>currentIndex</tt> of the
     * <tt>data</tt> array until the first occurrence of the tag opening bracket
     * sequence or the end of the data array.
     * <p>
     * This method does not remove whitespaces from text as plain text could
     * come with some whitespace formatting.
     * <p>
     * This method increases the <tt>currentIndex</tt> variable as it goes and
     * ends on the first occurrence of the tag opening bracket sequence.
     * <p>
     * After the lexical scanning for text is done, if it is not the end of the
     * whole document, this method sets the current state of Lexer to
     * {@link SSLexerState#READING_TAGS READING_TAGS} and skips the brackets.
     * <p>
     * This method validates a string of text before creating a token out of it
     * by checking if there are any invalid escape sequences. This means that
     * the escape character may not be located at the very end of the input text
     * with nothing to escape. It also means that the escape character may not
     * be any character other than a tag opening bracket or the escape-character
     * itself. If any of these criteria is not met, a {@linkplain LexerException}
     * is thrown.
     *
     * @return text token starting from the <tt>currentIndex</tt> of the
     *         <tt>data</tt> array until the first occurrence of the tag opening
     *         bracket sequence or the end of the data array
     * @throws LexerException if the escape sequence is invalid
     */
    private SSToken getText() {
        Scanner sc = new Scanner(new String(data).substring(currentIndex));
        sc.useDelimiter(TEXT_DELIMITER);

        String next = sc.next();
        sc.close();

        currentIndex += next.length();
        next = validateText(next);

        if (!isDataEnd()) {
            currentIndex += TAG_OPENING.length(); // skip tag opening
            setState(SSLexerState.READING_TAGS);
        }

        return new SSToken(SSTokenType.TEXT, next);
    }

    /**
     * Validates a string of text before creating a token out of it. Validating
     * consists of checking if there are any invalid escape sequences. This
     * means that the escape character may not be located at the very end of the
     * input text with nothing to escape. It also means that the escape
     * character may not be any character other than a tag opening bracket or
     * the escape-character itself. If any of these criteria is not met, a
     * {@linkplain LexerException} is thrown.
     * <p>
     * After validating the escape sequences of the given text, this method
     * replaces all occurrences of the double escape character and escape
     * character + tag opening bracket and returns the new legal text.
     *
     * @param text text to be validated and disposed of escape characters
     * @return text disposed of escape characters
     * @throws LexerException if the escape sequence of text is invalid
     */
    private static String validateText(String text) {
        char[] data = text.toCharArray();
        final char TAG_OPEN = TAG_OPENING.charAt(0);

        for (int i = 0; i < data.length; i++) {
            if (data[i] == ESCAPE_CHAR) {
                if (i == data.length-1) {
                    throw new LexerException("Invalid escape ending.");
                } else {
                    char nextChar = data[i+1];
                    if (!(nextChar == ESCAPE_CHAR || nextChar == TAG_OPEN)) {
                        throw new LexerException("Invalid escape sequence: " + nextChar);
                    } else {
                        i++;
                    }
                }
            }
        }

        /* If validation is successful, replace \\ with \ and \{ with { */
        text = text.replace(""+ESCAPE_CHAR+ESCAPE_CHAR, ""+ESCAPE_CHAR);
        text = text.replace(""+ESCAPE_CHAR+TAG_OPEN, ""+TAG_OPEN);
        return text;
    }

    /**
     * Returns tag token starting from the <tt>currentIndex</tt> of the
     * <tt>data</tt> array until the first occurrence of the tag closing bracket
     * sequence. If there is no tag closing bracket sequence or if a
     * non-existent tag occurs, a {@linkplain LexerException} is thrown.
     * <p>
     * This method returns a tag token with surrounding whitespaces trimmed, if
     * any, and all whitespaces in-between converted to a single space
     * character.
     * <p>
     * This method increases the <tt>currentIndex</tt> variable as it goes and
     * ends on the first occurrence of the tag closing bracket sequence.
     * <p>
     * After the lexical scanning for tag is done, this method sets the current
     * state of Lexer to {@link SSLexerState#READING_TEXT READING_TEXT} and
     * skips the brackets closing brackets.
     *
     * @return tag token starting from the <tt>currentIndex</tt> of the
     *         <tt>data</tt> array until the first occurrence of the tag closing
     *         bracket sequence
     * @throws LexerException
     *             if there is no tag closing bracket sequence or if a
     *             non-existent tag occurs
     */
    private SSToken getTag() {
        Scanner sc = new Scanner(new String(data).substring(currentIndex));
        sc.useDelimiter(TAG_DELIMITER);

        if (!sc.hasNext()) {
            sc.close();
            throw new LexerException("Tag is empty.");
        }

        String next = sc.next();
        sc.close();

        currentIndex += next.length();
        if (isDataEnd()) {
            throw new LexerException("Tag is never closed: " + next);
        } else {
            currentIndex += TAG_CLOSING.length(); // skip tag closing
            checkCurrentState();
        }

        next = replaceWhitespacesKeepingQuotationMarks(next.trim());

        if (next.toUpperCase().startsWith(FOR)) {
            // dispose the FOR keyword
            next = disposeOf(next, FOR);
            return new SSToken(SSTokenType.TAG_FOR, next);
        } else if (next.equalsIgnoreCase(END)) {
            // dispose the END keyword
            return new SSToken(SSTokenType.TAG_END, null);
        } else if (next.startsWith(ECHO)) {
            // dispose the ECHO keyword
            next = disposeOf(next, ECHO);
            return new SSToken(SSTokenType.TAG_ECHO, next);
        } else {
            throw new LexerException("Unknown tag: " + next);
        }
    }

    /**
     * Disposes the given <tt>keyword</tt> from the given <tt>tag</tt> by
     * removing it and then trimming left over whitespaces from the new tag.
     *
     * @param tag tag to be cleared of the keyword
     * @param keyword keyword to be removed from the tag
     * @return the tag cleared of the <tt>keyword</tt>
     */
    private static String disposeOf(String tag, String keyword) {
        return tag.substring(keyword.length()).trim();
    }

    /**
     * Checks if the <tt>data</tt> on the <tt>currentIndex</tt> starts with
     * {@link SmartScriptConstantsAndMethods#TAG_OPENING TAG_OPENING}. If the stated
     * condition is true, the state is set to {@link SSLexerState#READING_TAGS
     * READING_TAGS}, else the state is set to {@link SSLexerState#READING_TEXT
     * READING_TEXT}. If there is no data index on the <tt>currentIndex</tt> and
     * beyond, this method simply returns with nothing changed.
     */
    private void checkCurrentState() {
        String startingCharacters;
        try {
            startingCharacters = "" + data[currentIndex]  +  data[currentIndex+1];
        } catch (IndexOutOfBoundsException e) {
            return;
        }

        if (startingCharacters.equals(TAG_OPENING)) {
            currentIndex += TAG_OPENING.length(); // skip tag opening
            setState(SSLexerState.READING_TAGS);
        } else {
            setState(SSLexerState.READING_TEXT);
        }
    }

    /**
     * Returns true if the input text has been exhausted, or more formally if
     * <tt>currentIndex == data.length</tt>. False otherwise.
     *
     * @return true if the input text has been exhausted
     */
    private boolean isDataEnd() {
        return currentIndex == data.length;
    }

}
