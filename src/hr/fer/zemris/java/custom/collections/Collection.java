package hr.fer.zemris.java.custom.collections;

/**
 * A collection represents a general group of objects, known as its
 * <i>elements</i>. Many methods this class provides are defined in terms of the
 * {@link Object#equals equals} method. Some collections allow duplicate
 * elements and others do not. Some collections allow <code>null</code>
 * references and other do not.
 *
 * @author Mario Bobic
 */
public class Collection {

    /**
     * Constructs a new instance of <code>Collection</code>.
     */
    protected Collection() {
    }

    /**
     * Returns true if this collection contains no objects. False otherwise.
     *
     * @return true if this collection contains zero objects
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of currently stored objects in this collection.
     *
     * @return the number of elements in this collection
     */
    public int size() {
        return 0;
    }

    /**
     * Adds the given object into this collection.
     * <p>
     * Collections that support this operation may place limitations on what
     * elements may be added to this collection. In particular, some collections
     * will refuse to add <code>null</code> elements, and others will impose
     * restrictions on duplicate elements that may be added.
     *
     * @param value element to be added to this collection
     */
    public void add(Object value) {
    }

    /**
     * Returns true if the collection contains the given value, as determined by
     * {@link Object#equals equals} method. False otherwise.
     *
     * @param value element whose presence in this collection is to be tested
     * @return true if the collection contains the given value
     */
    public boolean contains(Object value) {
        return false;
    }

    /**
     * Removes a single instance of the specified element from this collection,
     * if it is present (optional operation). Returns <code>true</code> if the
     * collection contains the given value as determined by {@link Object#equals
     * equals} method and removes one occurrence of it. If there are duplicates,
     * this collection does not specify which value will be removed.
     *
     * @param value element to be removed from this collection, if present
     * @return true if an element was removed, false otherwise
     */
    public boolean remove(Object value) {
        return false;
    }

    /**
     * Returns the newly allocated array with size equals to the size of this
     * collection, filled with the collection content.
     * <p>
     * This method acts as bridge between array-based and collection-based APIs.
     *
     * @return an array filled with the collection content
     */
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method calls {@link Processor#process processor.process(...)} for each
     * element of this collection. The order in which elements will be sent is
     * undefined in this class.
     *
     * @param processor the processor which processes each element
     */
    public void forEach(Processor processor) {
    }

    /**
     * Adds all of the elements in the specified collection to this collection.
     * This other collection remains unchanged.
     *
     * @param other collection containing elements to be added to this collection
     */
    public void addAll(Collection other) {
        Processor p = new Processor() {

            @Override
            public void process(Object value) {
                add(value);
            }
        };

        other.forEach(p);
    }

    /**
     * Removes all of the elements from this collection. The collection will be
     * empty after this method returns.
     */
    public void clear() {
    }

}
