package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.lexer.SSTokenType;

/**
 * The {@linkplain Node} subclass. As well as its superclass this node can be
 * both and parent child. In addition to providing methods for adding the child
 * node to this node, getting the number of children and getting a specific
 * child at some index, this class also provides a constructor that accepts a
 * {@link SSTokenType#TEXT text} string and a getter method for that text.
 *
 * @author Mario Bobic
 * @see Node
 * @see Element
 */
public class TextNode extends Node {

    /** Text of this text node. */
    private final String text;

    /**
     * Constructs an instance of TextNode with the given text.
     *
     * @param text text for this text node
     */
    public TextNode(String text) {
        super();
        this.text = text;
    }

    /**
     * Returns the text of this text node.
     *
     * @return the text of this text node
     */
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitTextNode(this);
    }

}
