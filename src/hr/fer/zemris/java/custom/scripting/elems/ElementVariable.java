package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * The {@linkplain Element} subclass. All elements contain some data that is
 * used for interpreting types of elements given to the {@link SmartScriptParser
 * parser}. This element overrides the {@link Element#asText} method and returns
 * the string representation of the variable name stored in this element.
 *
 * @author Mario Bobic
 */
public class ElementVariable extends Element {

    /** The variable name. */
    private final String name;

    /**
     * Constructs an instance of ElementVariable with the given variable name.
     *
     * @param name variable name for this element
     */
    public ElementVariable(String name) {
        super();
        this.name = name;
    }

    /**
     * Returns the variable name stored in this element.
     */
    @Override
    public String asText() {
        return name;
    }

}
