package hr.fer.zemris.java.custom.scripting.nodes;

import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.ECHO;
import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.TAG_CLOSING;
import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.TAG_OPENING;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.lexer.SSTokenType;

/**
 * The {@linkplain Node} subclass. As well as its superclass this node can be
 * both and parent child. In addition to providing methods for adding the child
 * node to this node, getting the number of children and getting a specific
 * child at some index, this class also provides a constructor to hold an array
 * of {@link Element Elements} and a getter method to get the array of elements.
 *
 * @author Mario Bobic
 * @see Node
 * @see Element
 */
public class EchoNode extends Node {

    /** Elements that this EchoNode contains. */
    private final Element[] elements;

    /**
     * Constructs an instance of EchoNode with the given array of elements.
     *
     * @param elements elements for this EchoNode
     */
    public EchoNode(Element[] elements) {
        super();
        this.elements = elements;
    }

    /**
     * Returns the array of this EchoNode object's elements.
     *
     * @return the array of this EchoNode object's elements
     */
    public Element[] getElements() {
        return elements;
    }

    /**
     * Returns the string representation of the {@link SSTokenType#TAG_ECHO
     * ECHO} <tt>node</tt> and all the elements contained in the body of an ECHO
     * tag, with the proper formatting.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb    .append(TAG_OPENING)
            .append(ECHO).append(" ");

        for (Element el : elements) {
            sb.append(el.asText()).append(" ");
        }

        sb.append(TAG_CLOSING);

        return sb.toString();
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitEchoNode(this);
    }

}
