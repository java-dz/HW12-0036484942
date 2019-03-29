package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * The {@linkplain Element} subclass. All elements contain some data that is
 * used for interpreting types of elements given to the {@link SmartScriptParser
 * parser}. This element overrides the {@link Element#asText} method and returns
 * the string representation of the operator symbol stored in this element.
 *
 * @author Mario Bobic
 */
public class ElementOperator extends Element {

    /** The operator symbol. */
    private final String symbol;

    /**
     * Constructs an instance of ElementOperator with the given operator symbol.
     *
     * @param symbol symbol to be set to this operator
     */
    public ElementOperator(String symbol) {
        super();
        this.symbol = symbol;
    }

    /**
     * Returns a string representation of the operator symbol stored in this
     * element.
     */
    @Override
    public String asText() {
        return symbol;
    }

}
