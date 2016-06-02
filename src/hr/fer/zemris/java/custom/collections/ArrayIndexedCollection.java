package hr.fer.zemris.java.custom.collections;

/**
 * An array indexed collection stores objects into a fixed-size array with the
 * initial capacity specified by the caller or 16 by default. If the array gets
 * filled, this class ensures that the capacity expands to twice the number of
 * the current capacity of the array.
 * <p>
 * This implementation <b>allows</b> duplicate elements and <b>does not
 * allow</b> <code>null</code> references.
 *
 * @author Mario Bobic
 */
public class ArrayIndexedCollection extends Collection {

	/** Default initial capacity. */
	private static final int DEFAULT_CAPACITY = 16;

	/** The size of this collection (the number of elements it contains). */
	private int size;
	/** Current capacity of this collection's array. */
	private int capacity;
	/** The array into which the elements of this collection are stored. */
	private Object[] elements;

	/**
	 * Constructs an instance of ArrayIndexedCollection with the default
	 * capacity of 16.
	 */
	public ArrayIndexedCollection() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Constructs an instance of ArrayIndexedCollection with the capacity set to
	 * <code>initialCapacit</code>.
	 * 
	 * @param initialCapacity initial capacity of this collection
	 * @throws IllegalArgumentException if the specified initial capacity
     *         is less than 1
	 */
	public ArrayIndexedCollection(int initialCapacity) {
		if (initialCapacity < 1) {
			throw new IllegalArgumentException("Initial capacity must not be less than 1.");
		}
		capacity = initialCapacity;
		elements = new Object[capacity];
	}

	/**
	 * Constructs an instance of ArrayIndexedCollection containing the elements
	 * of the specified collection, with the capacity of
	 * <code>MAX(other.size(), 16)</code>.
	 * 
	 * @param other the collection whose elements are to be placed into this one
	 * @throws NullPointerException if the specified collection is null
	 */
	public ArrayIndexedCollection(Collection other) {
		this(other, DEFAULT_CAPACITY);
	}
	
	/**
	 * Constructs an instance of ArrayIndexedCollection containing the elements
	 * of the specified collection with the capacity of
	 * <code>MAX(other.size(), initialCapacity)</code>. The
	 * <code>initialCapacity</code> parameter is ignored if the specified
	 * collection contains more elements than the specified initial capacity.
	 * 
	 * @param other the collection whose elements are to be placed into this one
	 * @param initialCapacity optional initial capacity of this collection
	 * @throws NullPointerException if the specified collection is null
	 */
	public ArrayIndexedCollection(Collection other, int initialCapacity) {
		this(Math.max(other.size(), initialCapacity));
		addAll(other);
	}

	@Override
	public int size() {
		return size;
	}
	
	/**
	 * Adds the given object into this collection (reference is added into first
	 * empty place in this collection). If <code>null</code> reference is passed
	 * as an element, this method throws the {@linkplain IllegalArgumentException}.
	 * The average complexity of this method is O(1) due to the internal array.
	 * 
	 * @param value element to be added to this collection
	 * @throws IllegalArgumentException if the passed value is <code>null</code>
	 */
	@Override
	public void add(Object value) {
		if (value == null) {
			throw new IllegalArgumentException("Element must not be null.");
		}
		
		ensureCapacity();
		elements[size] = value;
		size++;
	}
	
	/**
	 * Inserts the given value at the given position in this collection. This
	 * method does not overwrite the previously recorded element at the
	 * specified position, but shifts the element currently at that position (if
	 * any) and any subsequent elements to the right (adds one to their indices).
	 * <p>
	 * The legal positions are <code>0</code> to <code>size</code>. If the index
	 * is outside of those boundaries, an {@linkplain IndexOutOfBoundsException}
	 * is thrown. If <code>null</code> reference is passed as an element, this
	 * method throws the {@linkplain IllegalArgumentException}.
	 * 
	 * @param value element to be inserted to this collection
	 * @param position index at which the specified element is to be inserted
	 * @throws IllegalArgumentException if the passed element value is null
	 * @throws IndexOutOfBoundsException if the position index is out of range
	 */
	public void insert(Object value, int position) {
		if (value == null) {
			throw new IllegalArgumentException("Element must not be null.");
		}
		
		checkIndex(position);
		
		ensureCapacity();
		shiftRight(position);
		elements[position] = value;
		size++;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>It is legal to ask this collection if it contains a <code>null</code>
	 * reference.
	 */
	@Override
	public boolean contains(Object value) {
		return indexOf(value) >= 0;
	}
	
	/**
	 * Returns the object that is stored in this collection at position
	 * <code>index</code>. The average complexity of this method is O(1)
	 * due to the internal array storage.
	 * <p>
	 * Valid indexes are in range 0 to size-1. If the given index is invalid,
	 * this method throws an {@linkplain IndexOutOfBoundsException}.
	 * 
	 * @param index index of the element to return
	 * @return the element at the specified position in this collection
	 * @throws IndexOutOfBoundsException
	 *             if the given index is not in range 0 to size-1
	 */
	public Object get(int index) {
		checkIndex(index);
		
		return elements[index];
	}
	
	/**
	 * Searches the collection and returns the index of the first occurrence of
	 * the given value or -1 if the value is not found. If the given value is
	 * not <code>null</code>, the equality of the given value is determined by
	 * {@link Object#equals equals} method. Since this collection does not allow
	 * <code>null</code> references, this method returns -1 in case a
	 * <code>null</code> reference is passed.
	 * 
	 * @param value element to search for
	 * @return the index of the first occurrence of the specified element in
	 *         this collection, or -1 if this it does not contain the element
	 */
	public int indexOf(Object value) {
		if (value != null) {
			for (int i = 0; i < size; i++) {
				if (value.equals(elements[i]))
					return i;
			}
		}
		return -1;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>The worst-case complexity of this method is O(n), in case the given value
	 * is not found.
	 */
	@Override
	public boolean remove(Object value) {
		int index = indexOf(value);

		if (index != -1) {
			remove(index);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes the element at the specified position in this list. Shifts any
	 * subsequent elements to the left (subtracts one from their indices). The
	 * average complexity of this method is O(1) due to the internal array.
	 *
	 * @param index the index of the element to be removed
	 * @throws IndexOutOfBoundsException
	 *             if the given index is not in range 0 to size-1
	 */
	public void remove(int index) {
		checkIndex(index);
		
		elements[index] = null;
		shiftLeft(index);
		size--;
	}
	
	@Override
	public void clear() {
		for (int i = 0; i < size; i++) {
			elements[i] = null;
		}
		size = 0;
	}
	
	@Override
	public Object[] toArray() {
		return copyOfArray(elements, size);
	}
	
	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if the given processor is null
	 */
	@Override
	public void forEach(Processor processor) {
		if (processor == null) {
			throw new IllegalArgumentException("Processor must not be null.");
		}
		
		for (int i = 0; i < size; i++) {
			processor.process(elements[i]);
		}
	}
	
	/**
	 * Increases the capacity of this collection instance if necessary, to
	 * ensure that it can hold new elements. More formally, the collection's
	 * array is full if <code>size == capacity</code>, and its capacity will be
	 * doubled.
	 */
	private void ensureCapacity() {
		if (size == capacity) {
			capacity *= 2;
			elements = copyOfArray(elements, capacity);
		}
	}
	
	/**
	 * Shifts the backing array to the right due to an insertion. The shifting
	 * starts at the end of the backing array (size-1) and ends at the specified
	 * index. This method expects that there is enough capacity for shifting to
	 * the right because the last element will be shifted to position <tt>size</tt>.
	 * 
	 * @param index index where to end shifting
	 */
	private void shiftRight(int index) {
		for (int i = size-1; i >= index; i--) {
			elements[i+1] = elements[i];
		}
		/* Erase the last shifted element's duplicate. */
		elements[index] = null;
	}
	
	/**
	 * Shifts the backing array to the left due to element removal. The shifting
	 * starts at the specified index and ends at size-1. The element at the
	 * position <code>index</code> is expected to be <code>null</code> as it is
	 * overridden.
	 * 
	 * @param index index where to start shifting
	 */
	private void shiftLeft(int index) {
		for (int i = index; i < size-1; i++) {
			elements[i] = elements[i+1];
		}
		/* Erase the last shifted element's duplicate. */
		elements[size-1] = null;
	}
	
	/**
	 * Copies the specified array, truncating or padding with nulls (if
	 * necessary) so the copy has the specified length. For all indices that are
	 * valid in both the original array and the copy, the two arrays will
	 * contain identical values.
	 * <p>
	 * This method throws <tt>NegativeArraySizeException</tt> if
	 * <tt>newLength</tt> is negative, or <tt>NullPointerException</tt> if
	 * <tt>original</tt> is <tt>null</tt>.
	 * 
	 * @param original the array to be copied
	 * @param newLength the length of the copy to be returned
	 * @return a copy of the original array
	 * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
	 * @throws NullPointerException if <tt>original</tt> is null
	 */
	private Object[] copyOfArray(Object[] original, int newLength) {
		Object[] newArr = new Object[newLength];

		int min = Math.min(original.length, newLength);
		for (int i = 0; i < min; i++) {
			newArr[i] = original[i];
		}
		
		return newArr;
	}
	
	/**
	 * Checks if the <code>index</code> is in range of <code>[0, size-1]</code>.
	 * If the index is out of range, an {@linkplain IndexOutOfBoundsException}
	 * is thrown.
	 * 
	 * @param index index to be checked
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	private void checkIndex(int index) {
		if (index < 0 || index > size-1) {
			throw new IndexOutOfBoundsException("Invalid index: " + index);
		}
	}

}
