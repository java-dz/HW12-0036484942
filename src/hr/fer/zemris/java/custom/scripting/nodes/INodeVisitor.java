package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * This interface represents the
 * <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor pattern</a>
 * for classes inherited from the {@linkplain Node} class.
 *
 * @author Mario Bobic
 */
public interface INodeVisitor {

    /**
     * Visits the specified {@link TextNode text node}.
     *
     * @param node a text node
     */
    void visitTextNode(TextNode node);

    /**
     * Visits the specified {@link ForLoopNode for-loop node}.
     *
     * @param node a for-loop node
     */
    void visitForLoopNode(ForLoopNode node);

    /**
     * Visits the specified {@link EchoNode echo node}.
     *
     * @param node an echo node
     */
    void visitEchoNode(EchoNode node);

    /**
     * Visits the specified {@link DocumentNode document node}.
     *
     * @param node a document node
     */
    void visitDocumentNode(DocumentNode node);

}
