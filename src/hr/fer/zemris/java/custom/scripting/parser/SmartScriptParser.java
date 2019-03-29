package hr.fer.zemris.java.custom.scripting.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hr.fer.zemris.java.custom.collections.*;
import hr.fer.zemris.java.custom.scripting.lexer.*;
import hr.fer.zemris.java.custom.scripting.nodes.*;
import hr.fer.zemris.java.custom.scripting.elems.*;
import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.*;

/**
 * This class is used for parsing documents with a certain set of rules. It
 * parses the document using {@linkplain SSLexer} as a lexical analysis tool by
 * calling the {@linkplain SSLexer#nextToken} method for new tokens until the
 * end of document is reached. If an exception is thrown while calling the
 * nextToken method, it is caught and rethrown as a
 * {@linkplain SmartScriptParserException}. A parsing error may also throw a
 * SmartScriptParserException with an appropriate detail message. Parsing error
 * may occur:
 * <ul>
 * <li>if a tag is invalid,
 * <li>if a variable or a function do not have a valid name,
 * <li>if a number cannot be parsed into an integer or a double or
 * <li>if a string with invalid escape sequences is found.
 * </ul>
 * <p>
 * Rules for plain text parsing:<br>
 * By using SSLexer, the parser is sure that a string of text is validated
 * before getting a token out of it. The text strings are checked if there are
 * any invalid escape sequences. This means that the escape character may not be
 * located at the very end of the input text with nothing to escape. It also
 * means that the escape character may not be any character other than a tag
 * opening bracket or the escape-character itself. If any of these criteria is
 * not met, a {@linkplain SmartScriptParserException} is thrown.
 * <p>
 * Rules for the FOR loop tag parsing:<br>
 * The elements are checked and validated from first to last, and by validating
 * it is considered that:
 * <ul>
 * <li>The first element is a variable with a valid variable name. If this check
 * is not passed, an exception is thrown with invalid name message.
 * <li>The rest of the elements are validated by checking if the element is a
 * variable with a valid variable name, a valid string or a valid number. If
 * none of the these checks are passed, an exception is thrown with an invalid
 * element message.
 * <li>The FOR loop tag must have an END tag in order to function properly. If
 * there are more END tags than FOR loop tags, an exception is thrown.
 * </ul>
 * Note that a valid number element may be an
 * {@linkplain ElementConstantInteger} if the number is a valid integer, or an
 * {@linkplain ElementConstantDouble} if the number cannot be parsed as an
 * integer.
 * <p>
 * Rules for the ECHO tag parsing:<br>
 * The string elements are checked one by one. If either of these elements are
 * invalid, or in other words, if any of the given string elements cannot be
 * represented as an {@linkplain Element}, a
 * {@linkplain SmartScriptParserException} is thrown. The elements are checked
 * and validated from first to last, and by validating it is considered that all
 * of the elements are checked if the element is a variable with a valid
 * variable name, a valid string, a valid number, a valid function or a valid
 * operator. If none of the these checks are passed, an exception is thrown with
 * an invalid element message.
 *
 * @author Mario Bobic
 */
public class SmartScriptParser {

    /**
     * Pattern for compiling variable and function names. Variable and function
     * names must start with a letter and may contain 0 or more alphanumeric
     * characters and/or underscore characters.
     */
    private static final Pattern VALID_VARNAME =
            Pattern.compile("[a-zA-Z]+[_0-9a-zA-Z]*");

    /** Operators supported by this parser. */
    private static final String OPERATORS = "+-*/^";

    /** Minimum number of elements in body of a FOR tag. */
    private static final int FOR_MIN_ELEMENTS = 3;
    /** Maximum number of elements in body of a FOR tag. */
    private static final int FOR_MAX_ELEMENTS = 4;

    /** A reference to a Lexer used for tokenizing document body. */
    private SSLexer lexer;
    /** The document node that is the parent of all other nodes. */
    private DocumentNode document;
    /** Internal stack used for storing nodes and elements. */
    private ObjectStack stack;

    /**
     * Constructs an instance of a SmartScriptParser with the given document
     * body text and parses the document.
     *
     * @param text document body text
     */
    public SmartScriptParser(String text) {
        lexer = new SSLexer(text);
        document = new DocumentNode();
        stack = new ObjectStack();

        parseDoc();
    }

    /**
     * Returns the document node of this parser.
     *
     * @return the document node of this parser
     */
    public DocumentNode getDocumentNode() {
        return document;
    }

    /**
     * Parses the document using {@linkplain SSLexer} as a lexical analysis tool
     * by calling the {@linkplain SSLexer#nextToken} method for new tokens until
     * the end of document is reached. If an exception is thrown while calling
     * the nextToken method, it is caught and rethrown as a
     * {@linkplain SmartScriptParserException}. A parsing error may also throw
     * a SmartScriptParserException with an appropriate detail message. Parsing
     * error may occur:
     * <ul>
     * <li>if a tag is invalid,
     * <li>if a variable or a function do not have a valid name,
     * <li>if a number cannot be parsed into an integer or a double or
     * <li>if a string with invalid escape sequences is found.
     * </ul>
     * This method creates nodes filled with elements and pushes them onto the
     * stack.
     *
     * @throws SmartScriptParserException if a parsing error occurs
     */
    private void parseDoc() {
        stack.push(document);

        while (true) {
            SSToken token;
            try {
                token = lexer.nextToken();
            } catch (LexerException e) {
                throw new SmartScriptParserException(e.getMessage());
            }

            SSTokenType tokenType = token.getType();

            if (tokenType == SSTokenType.TEXT) {
                createTextNode(token);
            } else if (tokenType == SSTokenType.TAG_FOR) {
                createForNode(token);
            } else if (tokenType == SSTokenType.TAG_ECHO) {
                createEchoNode(token);
            } else if (tokenType == SSTokenType.TAG_END) {
                stack.pop();
                if (stack.isEmpty()) {
                    throw new SmartScriptParserException(
                        "Number of END tags is greater than the number of FOR tags.");
                }
            } else {
                break; // else it is EOF
            }
        }

        if (stack.size() > 1) {
            throw new SmartScriptParserException(
                "Number of FOR tags is greater than the number of END tags.");
        }
    }

    /**
     * Creates a text node with the given <tt>token</tt>. The given token is
     * expected to be a valid {@link SSTokenType#TEXT TEXT} token. The inner
     * Lexer of this class provides clean TEXT tokens with no invalid escape
     * sequences.
     * <p>
     * The newly created text node is added as a child node to the last stacked
     * node.
     *
     * @param token text token to be put into a {@linkplain TextNode}
     */
    protected void createTextNode(SSToken token) {
        String text = (String) token.getValue();
        TextNode node = new TextNode(text);

        Node lastStackedNode = (Node) stack.peek();
        lastStackedNode.addChildNode(node);
    }

    /**
     * Creates a for-loop node with the given <tt>token</tt>. Token is expected
     * to have a string value. This string value is split into an array of
     * elements and they are validated one by one. The number of elements of
     * this array must not be under 3 and must not exceed 4. If either of these
     * elements are invalid, a {@linkplain SmartScriptParserException} is
     * thrown. The elements are checked and validated from first to last, and by
     * validating it is considered that:
     * <ul>
     * <li>The first element is a variable with a valid variable name. If this
     * check is not passed, an exception is thrown with invalid name message.
     * <li>The rest of the elements are validated by checking if the element is
     * a variable with a valid variable name, a valid string or a valid number.
     * If none of the these checks are passed, an exception is thrown with an
     * invalid element message.
     * </ul>
     * Note that a valid number element may be an
     * {@linkplain ElementConstantInteger} if the number is a valid integer, or
     * an {@linkplain ElementConstantDouble} if the number cannot be parsed as
     * an integer.
     * <p>
     * The newly created text node is added as a child node to the last stacked
     * node and is pushed onto the stack as a new parent for future nodes.
     *
     * @param token token to be put into a {@linkplain ForLoopNode}
     */
    protected void createForNode(SSToken token) {
        String forBody = (String) token.getValue();
        String[] forElements = splitKeepingQuotationMarks(forBody);

        if (forElements.length < FOR_MIN_ELEMENTS|| forElements.length > FOR_MAX_ELEMENTS) {
            throw new SmartScriptParserException(String.format(
                "Must have %d or %d elements in FOR body: %s",
                FOR_MIN_ELEMENTS, FOR_MAX_ELEMENTS, forBody
            ));
        }

        Element[] elements = getForElements(forElements);
        ForLoopNode node =
            new ForLoopNode((ElementVariable) elements[0], elements[1], elements[2], elements[3]);

        Node lastStackedNode = (Node) stack.peek();
        lastStackedNode.addChildNode(node);

        stack.push(node);
    }

    /**
     * Returns an array of {@link Element Elements} in the body of a FOR tag.
     * The <tt>forElements</tt> array is expected to be have at least
     * {@link SmartScriptParser#FOR_MIN_ELEMENTS FOR_MIN_ELEMENTS} and checks
     * all of the given string elements one by one. If either of these elements
     * are invalid, a {@linkplain SmartScriptParserException} is thrown. The
     * elements are checked and validated from first to last, and by validating
     * it is considered that:
     * <ul>
     * <li>The first element is a variable with a valid variable name. If this
     * check is not passed, an exception is thrown with invalid name message.
     * <li>The rest of the elements are validated by checking if the element is
     * a variable with a valid variable name, a valid string or a valid number.
     * If none of the these checks are passed, an exception is thrown with an
     * invalid element message.
     * </ul>
     * Note that a valid number element may be an
     * {@linkplain ElementConstantInteger} if the number is a valid integer, or
     * an {@linkplain ElementConstantDouble} if the number cannot be parsed as
     * an integer.
     *
     * @param forElements an array of strings in the body of a FOR tag
     * @return an array of string elements in the body of a FOR tag
     * @throws SmartScriptParserException if any given element is invalid
     */
    private static Element[] getForElements(String[] forElements) {
        Element[] elements = new Element[FOR_MAX_ELEMENTS];

        String variable = forElements[0];
        if (!isVariableNameValid(variable)) {
            throw new SmartScriptParserException("Invalid name: " + variable);
        }
        elements[0] = new ElementVariable(variable);

        String start = forElements[1];
        elements[1] = getForElementFrom(start);

        String end = forElements[2];
        elements[2] = getForElementFrom(end);

        if (forElements.length == FOR_MAX_ELEMENTS) {
            String step = forElements[3];
            elements[3] = getForElementFrom(step);
        } else {
            elements[3] = null;
        }

        return elements;
    }

    /**
     * Returns an appropriate element from the given <tt>candidate</tt> adjusted
     * for the FOR loop legal body elements. The candidate is tested for various
     * elements in this order:
     * <ol>
     * <li>Is the candidate a valid variable name? If it is, an
     * {@linkplain ElementVariable} is returned with its name set to
     * <tt>candidate</tt>
     * <li>Is the candidate a valid string? If it is, an
     * {@linkplain ElementString} is returned with its value set to
     * <tt>candidate</tt>
     * <li>Is the candidate a valid number? If it is, an {@linkplain Element} is
     * returned which may be an {@linkplain ElementConstantInteger} if the
     * <tt>candidate</tt> is a valid integer, or an
     * {@linkplain ElementConstantDouble} if the <tt>candidate</tt> cannot be
     * parsed as an integer.
     * </ol>
     * If none of these tests pass, a {@linkplain SmartScriptParserException} is
     * thrown.
     *
     * @param candidate candidate to be retrieved as an element
     * @return an appropriate element from the given <tt>candidate</tt>
     * @throws SmartScriptParserException if none of the above tests pass
     */
    private static Element getForElementFrom(String candidate) {
        if (isVariableNameValid(candidate)) {
            return new ElementVariable(candidate);
        } else if (isStringValid(candidate)) {
            return new ElementString(candidate);
        } else if (isNumberValid(candidate)) {
            return parseNumberElement(candidate);
        } else {
            throw new SmartScriptParserException("Invalid element: " + candidate);
        }
    }

    /**
     * Returns a number element parsed from <tt>s</tt> parameter. A number
     * element may be an {@linkplain ElementConstantInteger} or an
     * {@linkplain ElementConstantDouble}. This method parses the string
     * argument either as a signed decimal integer or a signed decimal double.
     * <p>
     * In order to parse an integer number, the characters in the string must
     * all be decimal digits, except that the first character may be an ASCII
     * minus sign <tt>'-'</tt> to indicate a negative value or an ASCII plus
     * sign <tt>'+'</tt> to indicate a positive value. Integer parsing is done
     * by the {@link Integer#parseInt} method.
     * <p>
     * If the integer parsing does not succeed, the string is tried to be parsed
     * as a double. Double parsing is done by the {@link Double#parseDouble}
     * method.
     * <p>
     * If none of the parsing methods succeed, a
     * {@linkplain SmartScriptParserException} is thrown.
     *
     * @param s number to be parsed into an integer or a double
     * @return a number element parsed as an integer or a double
     * @throws SmartScriptParserException if all number parsing methods fail
     */
    private static Element parseNumberElement(String s) {
        try {
            int value = Integer.parseInt(s);
            return new ElementConstantInteger(value);
        } catch (NumberFormatException e1) {
            try {
                double value = Double.parseDouble(s);
                return new ElementConstantDouble(value);
            } catch (NumberFormatException e2) {
                throw new SmartScriptParserException("Cannot parse number: " + s);
            }
        }
    }

    /**
     * Creates an echo node with the given <tt>token</tt>. Token is expected to
     * have a string value. This string value is split into an array of elements
     * and they are validated one by one.
     *
     * @param token token to be put into a {@linkplain EchoNode}
     */
    protected void createEchoNode(SSToken token) {
        String echoBody = (String) token.getValue();
        String[] echoElements = splitKeepingQuotationMarks(echoBody);

        Element[] elements = getEchoElements(echoElements);
        EchoNode node = new EchoNode(elements);

        Node lastStackedNode = (Node) stack.peek();
        lastStackedNode.addChildNode(node);
    }

    /**
     * Returns an array of {@link Element Elements} in the body of an ECHO tag.
     * The <tt>echoElements</tt> array may have any number of arguments. This
     * method checks all of the given string elements one by one. If either of
     * these elements are invalid, or in other words, if any of the given string
     * elements cannot be represented as an {@linkplain Element}, a
     * {@linkplain SmartScriptParserException} is thrown. The elements are
     * checked and validated from first to last, and by validating it is
     * considered that all of the elements are validated by checking if the
     * element is a variable with a valid variable name, a valid string, a valid
     * number, a valid function or a valid operator. If none of the these checks
     * are passed, an exception is thrown with an invalid element message.
     * <p>
     * Note that a valid number element may be an
     * {@linkplain ElementConstantInteger} if the number is a valid integer, or
     * an {@linkplain ElementConstantDouble} if the number cannot be parsed as
     * an integer.
     *
     * @param echoElements an array of strings in the body of an ECHO tag
     * @return an array of string elements in the body of a ECHO tag
     * @throws SmartScriptParserException if any given element is invalid
     */
    private static Element[] getEchoElements(String[] echoElements) {
        Element[] elements = new Element[echoElements.length];

        for (int i = 0; i < echoElements.length; i++) {
            elements[i] = getEchoElementFrom(echoElements[i]);
        }

        return elements;
    }

    /**
     * Returns an appropriate element from the given <tt>candidate</tt> adjusted
     * for the ECHO legal body elements. This method calls the
     * {@linkplain SmartScriptParser#getForElementFrom} method to check if
     * candidate is any of its legal elements. The candidate is tested for
     * various elements in this order, with respect to the method that is
     * called:
     * <ol>
     * <li>Is the candidate a valid variable name? If it is, an
     * {@linkplain ElementVariable} is returned with its name set to
     * <tt>candidate</tt>
     * <li>Is the candidate a valid string? If it is, an
     * {@linkplain ElementString} is returned with its value set to
     * <tt>candidate</tt>
     * <li>Is the candidate a valid number? If it is, an {@linkplain Element} is
     * returned which may be an {@linkplain ElementConstantInteger} if the
     * <tt>candidate</tt> is a valid integer, or an
     * {@linkplain ElementConstantDouble} if the <tt>candidate</tt> cannot be
     * parsed as an integer.
     * <li>Is the candidate a valid function name? If it is, an
     * {@linkplain ElementFunction} is returned with its name set to
     * <tt>candidate</tt>
     * <li>Is the candidate a valid operator symbol? If it is, an
     * {@linkplain ElementOperator} is returned with its symbol set to
     * <tt>candidate</tt>
     * </ol>
     * If none of these tests pass, a {@linkplain SmartScriptParserException} is
     * thrown.
     *
     * @param candidate candidate to be retrieved as an element
     * @return an appropriate element from the given <tt>candidate</tt>
     * @throws SmartScriptParserException if none of the above tests pass
     */
    private static Element getEchoElementFrom(String candidate) {
        try {
            return getForElementFrom(candidate);
        } catch (SmartScriptParserException e) {
            if (isFunctionNameValid(candidate)) {
                return new ElementFunction(candidate);
            } else if (isOperatorValid(candidate)) {
                return new ElementOperator(candidate);
            } else {
                throw new SmartScriptParserException("Invalid element: " + candidate);
            }
        }
    }

    /**
     * Returns true if the given <tt>name</tt> is a valid variable name.
     * Variable names must start with a letter and may contain 0 or more
     * alphanumeric characters and/or underscore characters.
     *
     * @param name variable name to be validated
     * @return true if <tt>name</tt> is a valid variable name
     */
    private static boolean isVariableNameValid(String name) {
        Matcher m = VALID_VARNAME.matcher(name);
        return m.matches();
    }

    /**
     * Returns true if the given <tt>name</tt> is a valid function name.
     * Function names must have the <tt>'@'</tt> character at the beginning and
     * must start with a letter and may contain 0 or more alphanumeric
     * characters and/or underscore characters.
     *
     * @param name function name to be validated
     * @return true if <tt>name</tt> is a valid function name
     */
    private static boolean isFunctionNameValid(String name) {
        return name.startsWith(FUNCTION_MARK)
                && isVariableNameValid(name.substring(1));
    }

    /**
     * Returns true if the given <tt>string</tt> is a valid string. The string
     * is first checked if it starts with and ends with quotation mark symbols.
     * The string is also validated that the escape character may not be located
     * at the very end of the string with nothing to escape. It also validates
     * that the escape character may not be any character other than a quotation
     * mark character, a newline character, a tabulator character, a carriage
     * return character or the escape-character itself. If the string is bounded
     * with quotation mark symbols and any of the stated criteria is not met,
     * this method throws a {@linkplain SmartScriptParserException} with the
     * invalid escape sequence detail message.
     *
     * @param string string to be validated
     * @return true if <tt>string</tt> is a valid string
     * @throws SmartScriptParserException if validity checks of a string fail
     */
    private static boolean isStringValid(String string) {
        if (!(string.startsWith(QUOT_MARK) && string.endsWith(QUOT_MARK))) {
            return false;
        }

        String withoutQuots = string.substring(1, string.length()-1);
        char[] data = withoutQuots.toCharArray();

        for (int i = 0; i < data.length; i++) {
            if (data[i] == ESCAPE_CHAR) {
                if (i == data.length-1) {
                    throw new SmartScriptParserException(
                            "Invalid string escape ending.");
                } else {
                    char nextChar = data[i+1];
                    if ( !(nextChar == ESCAPE_CHAR
                        || nextChar == QUOT_MARK.charAt(0)
                        || nextChar == 'n'
                        || nextChar == 'r'
                        || nextChar == 't'
                    )) {
                        throw new SmartScriptParserException(
                                "Invalid escape sequence: " + nextChar);
                    } else {
                        i++;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Returns true if the given <tt>number</tt> can be parsed either first into
     * an integer or a double number. False otherwise.
     * <p>This method literally tries to parse the number and returns the result
     * based on the parsing result - if an exception was thrown and caught,
     * false is returned.
     *
     * @param number number to be validated
     * @return true if <tt>number</tt> is a valid integer or double
     */
    private static boolean isNumberValid(String number) {
        try {
            parseNumberElement(number);
            return true;
        } catch (SmartScriptParserException e) {
            return false;
        }
    }

    /**
     * Returns true if the given <tt>symbol</tt> is a valid operator symbol. An
     * operator symbol must be one of the symbols given from the
     * {@link SmartScriptParser#OPERATORS OPERATORS} character sequence.
     *
     * @param symbol operator symbol to be validated
     * @return true if <tt>symbol</tt> is a valid operator symbol
     */
    private static boolean isOperatorValid(String symbol) {
        return symbol.length() == 1 && OPERATORS.contains(symbol);
    }

}
