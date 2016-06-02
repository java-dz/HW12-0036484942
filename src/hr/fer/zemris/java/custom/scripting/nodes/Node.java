package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;

/**
 * The base class for all nodes. All nodes can be parents and can have children.
 * This class provides methods for adding the child node to this node, getting
 * the number of children and getting a specific child at some index. If the
 * {@link Node#getChild} method is called before any child was added, an
 * {@linkplain IndexOutOfBoundsException} is thrown.
 *
 * @author Mario Bobic
 */
public abstract class Node {

	/** The backing collection of this node. */
	private ArrayIndexedCollection col;
	
	/**
	 * Adds the child node to this node. By calling this method for the first
	 * time, an inner collection is created and initialized. Every other call
	 * just adds the child node to the same inner collection that was
	 * constructed during the first call.
	 * <p>If the passed <tt>child</tt> argument is <tt>null</tt>, an
	 * {@linkplain IllegalArgumentException} is thrown.
	 * 
	 * @param child child node to be added to this node
	 * @throws IllegalArgumentException if child is <tt>null</tt>
	 */
	public void addChildNode(Node child) {
		if (col == null) {
			col = new ArrayIndexedCollection();
		}
		
		col.add(child);
	}
	
	/**
	 * Returns the current number of children of this node. If no child has been
	 * added to this node before this method is called, this method simply
	 * returns 0.
	 * 
	 * @return the current number of children of this node
	 */
	public int numberOfChildren() {
		return col == null ? 0 : col.size();
	}
	
	/**
	 * Returns the child stored at the given <tt>index</tt>.
	 * <p>
	 * Valid indexes are in range 0 to numberOfChildren-1. If the given index is
	 * invalid, this method throws an {@linkplain IndexOutOfBoundsException}. If
	 * no child has been added to this node before this method is called, the
	 * same exception is thrown because the inner collection size is 0.
	 * 
	 * @param index index of a child
	 * @return the child stored at the given <tt>index</tt>
	 * @throws IndexOutOfBoundsException if the given index is out of range
	 */
	public Node getChild(int index) {
		if (col == null) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
		} else {
			return (Node) col.get(index);
		}
	}
	
	/**
	 * Returns a string representation of the child nodes of the given
	 * <tt>parent</tt> node by iterating through all the children of the node
	 * and appending them this way relying on the implementation of their
	 * <tt>toString()</tt> method.
	 * 
	 * @param parent parent whose child nodes are to be returned as string
	 * @return child nodes appended as a string representation
	 */
	protected static String childNodesToString(Node parent) {
		StringBuilder sb = new StringBuilder();
		
		int num = parent.numberOfChildren();
		for (int i = 0; i < num; i++) {
			Node child = parent.getChild(i);
			sb.append(child);
		}
		
		return sb.toString();
	}
	
	/**
	 * Accepts a node visitor object for processing this node.
	 * 
	 * @param visitor visitor for node processing
	 */
	public abstract void accept(INodeVisitor visitor);

}
