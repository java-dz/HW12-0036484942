package hr.fer.zemris.java.custom.collections;

/**
 * A stack collection. This class provides fundamental functions and methods of
 * a stack.
 * <p>This implementation <b>allows</b> duplicate elements and <b>does not
 * allow</b> <code>null</code> references.
 *
 * @author Mario Bobic
 */
public class ObjectStack {

    /** The backing collection of this stack. */
    private ArrayIndexedCollection col;

    /**
     * Constructs a new empty instance of ObjectStack.
     */
    public ObjectStack() {
        col = new ArrayIndexedCollection();
    }

    /**
     * Returns true if this stack contains no objects. False otherwise.
     *
     * @return true if this stack contains zero objects
     */
    public boolean isEmpty() {
        return col.isEmpty();
    }

    /**
     * Returns the number of currently stored objects in this stack.
     *
     * @return the number of elements in this stack
     */
    public int size() {
        return col.size();
    }

    /**
     * Pushes the given value onto the stack. If <code>null</code> is passed as
     * an element, this method throws an {@linkplain IllegalArgumentException}.
     *
     * @param value element to be added to this stack
     * @throws IllegalArgumentException if the passed value is <code>null</code>
     */
    public void push(Object value) {
        col.add(value);
    }

    /**
     * Removes last value pushed onto the stack and returns it. If the stack is
     * empty when this method is called, an {@linkplain EmptyStackException} is
     * thrown.
     *
     * @return the last value pushed onto the stack
     * @throws EmptyStackException if the stack was empty
     */
    public Object pop() {
        Object retVal = peek();
        col.remove(col.size()-1);
        return retVal;
    }

    /**
     * Returns the last value pushed onto the stack without removing it. If the
     * stack is empty when this method is called, an
     * {@linkplain EmptyStackException} is thrown.
     *
     * @return the last value pushed onto the stack
     * @throws EmptyStackException if the stack was empty
     */
    public Object peek() {
        if (col.isEmpty()) {
            throw new EmptyStackException("Stack is empty.");
        }

        return col.get(col.size()-1);
    }

    /**
     * Removes all of the elements from this stack. The stack will be empty
     * after this method returns.
     */
    public void clear() {
        col.clear();
    }
}
