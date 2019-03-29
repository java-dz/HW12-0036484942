package hr.fer.zemris.java.custom.scripting.nodes;

import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.END;
import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.FOR;
import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.TAG_CLOSING;
import static hr.fer.zemris.java.custom.scripting.parser.SmartScriptConstantsAndMethods.TAG_OPENING;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.lexer.SSTokenType;

/**
 * The {@linkplain Node} subclass. As well as its superclass this node can be
 * both and parent child. In addition to providing methods for adding the child
 * node to this node, getting the number of children and getting a specific
 * child at some index, this class also provides a constructor that accepts all
 * elements of a {@link SSTokenType#TAG_FOR FOR} tag, being the loop's iterating
 * variable, a start expression, an end expression and a step expression,
 * respectively. The listed expressions may be a variable again, a constant
 * integer or a double number, or a string.
 *
 * @author Mario Bobic
 * @see Node
 * @see Element
 */
public class ForLoopNode extends Node {

    /** FOR loop's iterating variable. */
    private final ElementVariable variable;
    /** FOR loop's start expression. */
    private final Element startExpression;
    /** FOR loop's end expression. */
    private final Element endExpression;
    /** FOR loop's step expression. */
    private final Element stepExpression;

    /**
     * Constructs an instance of ForLoopNode with the given parameters.
     *
     * @param variable FOR loop's iterating variable
     * @param startExpression FOR loop's start expression
     * @param endExpression FOR loop's end expression
     * @param stepExpression FOR loop's step expression
     */
    public ForLoopNode(ElementVariable variable, Element startExpression,
            Element endExpression, Element stepExpression) {
        super();
        this.variable = variable;
        this.startExpression = startExpression;
        this.endExpression = endExpression;
        this.stepExpression = stepExpression;
    }

    /**
     * Returns the FOR loop's iterating variable.
     *
     * @return the FOR loop's iterating variable
     */
    public ElementVariable getVariable() {
        return variable;
    }

    /**
     * Returns the FOR loop's start expression.
     *
     * @return the FOR loop's start expression
     */
    public Element getStartExpression() {
        return startExpression;
    }

    /**
     * Returns the FOR loop's end expression.
     *
     * @return the FOR loop's end expression
     */
    public Element getEndExpression() {
        return endExpression;
    }

    /**
     * Returns the FOR loop's step expression.
     *
     * @return the FOR loop's step expression
     */
    public Element getStepExpression() {
        return stepExpression;
    }

    /**
     * Returns the string representation of the {@link SSTokenType#TAG_FOR FOR}
     * loop <tt>node</tt> and its children. This method gets all the elements
     * contained in the body of a FOR loop, with the proper formatting.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb        .append(TAG_OPENING)
                .append(FOR)                        .append(" ")
                .append(variable.asText())            .append(" ")
                .append(startExpression.asText())    .append(" ")
                .append(endExpression.asText())        .append(" ");
        if (stepExpression != null) {
            sb    .append(stepExpression.asText())    .append(" ");
        }
        sb        .append(TAG_CLOSING);

        sb        .append(childNodesToString(this));

        sb        .append(TAG_OPENING)
                .append(END)
                .append(TAG_CLOSING);

        return sb.toString();
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitForLoopNode(this);
    }

}
