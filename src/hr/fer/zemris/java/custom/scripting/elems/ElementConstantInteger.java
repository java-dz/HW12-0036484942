package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * The {@linkplain Element} subclass. All elements contain some data that is
 * used for interpreting types of elements given to the {@link SmartScriptParser
 * parser}. This element overrides the {@link Element#asText} method and returns
 * the string representation of a constant integer value stored in this element.
 *
 * @author Mario Bobic
 */
public class ElementConstantInteger extends Element {

    /** The constant integer value of this element. */
    private final int value;

    /**
     * Constructs an instance of ElementConstantInteger with the given constant
     * integer value.
     *
     * @param value the constant integer value
     */
    public ElementConstantInteger(int value) {
        super();
        this.value = value;
    }

    /**
     * Returns a string representation of a constant integer value stored in
     * this element.
     */
    @Override
    public String asText() {
        return Integer.toString(value);
    }

}
