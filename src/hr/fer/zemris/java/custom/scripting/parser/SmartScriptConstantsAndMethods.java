package hr.fer.zemris.java.custom.scripting.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hr.fer.zemris.java.custom.scripting.lexer.SSTokenType;

/**
 * A collection of constants and methods used by the SmartScript framework.
 *
 * @author Mario Bobic
 */
public class SmartScriptConstantsAndMethods {

    /**
     * Prevents this class instantiation.
     */
    private SmartScriptConstantsAndMethods() {
    }

    /** An escape character for validating text. */
    public static final char ESCAPE_CHAR = '\\';

    /** A function starting character. */
    public static final String FUNCTION_MARK = "@";
    /** A quotation mark used for representing strings. */
    public static final String QUOT_MARK = "\"";

    /** A {@link SSTokenType#TAG_FOR FOR} tag definition. */
    public static final String FOR = "FOR";
    /** A {@link SSTokenType#TAG_END END} tag definition. */
    public static final String END = "END";
    /** An {@link SSTokenType#TAG_ECHO ECHO} tag definition. */
    public static final String ECHO = "=";

    /** A tag opening bracket. */
    public static final String TAG_OPENING = "{$";
    /** A tag closing bracket. */
    public static final String TAG_CLOSING = "$}";

    /**
     * A Regular expression for splitting nested strings and maintaining a valid
     * string structure.
     */
    // REGEX = "[^"\\]*(?:\\.[^"\\]*)*"|\S+
    // http://stackoverflow.com/questions/36292591/splitting-a-nested-string-keeping-quotation-marks
    private static final String REGEX = "\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"|\\S+";
    /**
     * A pattern compiled from the REGEX constant.
     */
    private static final Pattern PATTERN_NESTED_STRINGS = Pattern.compile(REGEX);

    /**
     * Replaces each substring of this string that matches multiple whitespace
     * characters and that is not within a nested string with a single space
     * character and returns the new string.
     * <p>
     * For an input string that in plain text looks like this:
     * <blockquote><pre>
     * This \t is "a    string" and    this is "a \t \"nested\"   string"
     * </pre></blockquote>
     * The result will be the following:
     * <blockquote><pre>
     * This is "a    string" and this is "a \t \"nested\"   string"
     * </pre></blockquote>
     *
     * @param s string whose whitespaces are to be replaces by a single space
     * @return a string with no multiple whitespaces on non-nested strings
     */
    public static String replaceWhitespacesKeepingQuotationMarks(String s) {
        String[] split = splitKeepingQuotationMarks(s);
        StringBuilder sb = new StringBuilder();

        for (String str : split) {
            sb.append(str).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * For an input string that in plain text looks like this:
     * <blockquote><pre>
     * This is "a string" and this is "a \"nested\" string"
     * </pre></blockquote>
     * The result will be the following:
     * <blockquote><pre>
     * [0] This
     * [1] is
     * [2] "a string"
     * [3] and
     * [4] this
     * [5] is
     * [6] "a \"nested\" string"
     * </pre></blockquote>
     *
     * @param s the input string to be split
     * @return an array of strings split on whitespaces
     */
    public static String[] splitKeepingQuotationMarks(String s) {
        Matcher matcher = PATTERN_NESTED_STRINGS.matcher(s);
        int counter = 0;

        while (matcher.find()) {
            counter++;
        }

        String[] array = new String[counter];

        matcher.reset();
        for (int i = 0; i < counter; i++) {
            matcher.find();
            array[i] = matcher.group(0);
        }

        return array;
    }

}
