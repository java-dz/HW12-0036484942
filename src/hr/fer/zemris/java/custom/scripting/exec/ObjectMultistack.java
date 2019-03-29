package hr.fer.zemris.java.custom.scripting.exec;

import java.util.HashMap;
import java.util.Map;

/**
 * A stack collection. This class provides fundamental functions and methods of
 * a stack.
 *
 * @author Mario Bobic
 */
public class ObjectMultistack {

    /** Map of multistack entries mapped to stack names. */
    private Map<String, MultistackEntry> map = new HashMap<>();

    /**
     * Pushes the given value onto the stack with the specified <tt>name</tt>.
     *
     * @param name name of the stack where to push the value
     * @param valueWrapper value to be pushed onto the stack
     */
    public void push(String name, ValueWrapper valueWrapper) {
        MultistackEntry entry = map.get(name);

        if (entry == null) {
            map.put(name, new MultistackEntry(valueWrapper, null));
        } else {
            map.put(name, new MultistackEntry(valueWrapper, entry));
        }
    }

    /**
     * Returns a value that was last pushed onto the stack with the specified
     * <tt>name</tt> and removes it from the stack. If the stack is empty, a
     * {@linkplain EmptyStackException} is thrown.
     *
     * @param name name of the stack to be popped
     * @return a value that was last pushed onto the specified stack
     * @throws EmptyStackException if the stack is empty
     */
    public ValueWrapper pop(String name) {
        MultistackEntry entry = getEntry(name);
        ValueWrapper value = entry.value;

        if (entry.prev == null) {
            map.remove(name);
        } else {
            entry.value = entry.prev.value;
            entry.prev = entry.prev.prev;
        }

        return value;
    }

    /**
     * Returns a value that was last pushed onto the stack with the specified
     * <tt>name</tt>. This method leaves the current mapping unchanged. If the
     * stack is empty, a {@linkplain EmptyStackException} is thrown.
     *
     * @param name name of the stack to be peaked
     * @return a value that was last pushed onto the specified stack
     * @throws EmptyStackException if the stack is empty
     */
    public ValueWrapper peek(String name) {
        return getEntry(name).value;
    }

    /**
     * Checks if the stack specified by its <tt>name</tt> is empty. If not, this
     * method returns a {@linkplain MultistackEntry} associated with the
     * specified <tt>name</tt>. If the stack is empty, a
     * {@linkplain EmptyStackException} is thrown.
     *
     * @param name name of the stack
     * @return a {@linkplain MultistackEntry} associated with the specified name
     * @throws EmptyStackException if the stack is empty
     */
    private MultistackEntry getEntry(String name) {
        if (isEmpty(name)) {
            throw new EmptyStackException("Stack " + name + " is empty.");
        }
        return map.get(name);
    }

    /**
     * Returns true if the stack with the specified <tt>name</tt> contains no
     * entries. False otherwise.
     *
     * @param name name of the stack to be checked
     * @return true if the stack with the specified name is empty
     */
    public boolean isEmpty(String name) {
        return !map.containsKey(name);
    }

    /**
     * A class that represents a multistack entry for the
     * <tt>ObjectMultistack</tt> class. Used to store entries with their value
     * and a reference to the next entry. If the previous multistack entry is a
     * <tt>null</tt> reference, this means that the entry is the first one in
     * the slot of stack names.
     *
     * @author Mario Bobic
     */
    private static class MultistackEntry {
        /** Value of the entry. */
        private ValueWrapper value;
        /** Previous entry. */
        private MultistackEntry prev;

        /**
         * Constructs an instance of <tt>MultistackEntry</tt> with the specified
         * value and previous multistack entry.
         *
         * @param value value of the entry
         * @param prev previous entry
         */
        public MultistackEntry(ValueWrapper value, MultistackEntry prev) {
            this.value = value;
            this.prev = prev;
        }
    }

}
