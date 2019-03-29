package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * The {@linkplain Node} subclass. As well as its superclass this node can be
 * both parent and child. But since this class is the main node in charge of a
 * document it should have no parent nodes, therefore it should not be a child.
 * This class provides methods for adding the child node to this node, getting
 * the number of children and getting a specific child at some index.
 *
 * @author Mario Bobic
 * @see Node
 */
public class DocumentNode extends Node {

    @Override
    public String toString() {
        return childNodesToString(this);
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitDocumentNode(this);
    }

}
