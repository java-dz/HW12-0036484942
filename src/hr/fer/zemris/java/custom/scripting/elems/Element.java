package hr.fer.zemris.java.custom.scripting.elems;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

/**
 * The base class for all elements. All elements contain some data that is used
 * for interpreting types of elements given to the {@link SmartScriptParser
 * parser}. All elements provide an {@link Element#asText} method for returning
 * the string representation of the element.
 *
 * @author Mario Bobic
 */
public abstract class Element {

    /**
     * Returns a string representation of the element.
     *
     * @return a string representation of the element
     */
    public abstract String asText();

}
