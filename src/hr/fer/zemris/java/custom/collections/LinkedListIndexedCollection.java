package hr.fer.zemris.java.custom.collections;

/**
 * Doubly-linked list implementation of the {@linkplain Collection} class.
 * Implements all optional list operations. This collection has <i>unlimited</i>
 * capacity where only your memory is the limit.
 * <p>Whenever a new element is added or inserted, this collection creates
 * a new list node to hold it and connect it with the next and previous one.
 * <p>This implementation <b>allows</b> duplicate elements and <b>does not
 * allow</b> <code>null</code> references.
 *
 * @author Mario Bobic
 */
public class LinkedListIndexedCollection extends Collection {

	/** The size of this list (the number of elements it contains). */
	private int size;
	/** First node of this list. */
	private ListNode first;
	/** Last node of this list. */
	private ListNode last;
	
	/**
	 * Constructs an empty instance of LinkedListIndexedCollection.
	 */
	public LinkedListIndexedCollection() {
		first = last = null;
		size = 0;
	}
	
	/**
	 * Constructs an instance of LinkedListIndexedCollection containing the
	 * elements of the specified collection.
	 * 
	 * @param other the collection whose elements are to be placed into this one
	 * @throws NullPointerException if the specified collection is null
	 */
	public LinkedListIndexedCollection(Collection other) {
		addAll(other);
	}
	
	@Override
	public int size() {
		return size;
	}
	
	/**
	 * Adds the given object into this collection (reference is added onto the
	 * last index in this collection). If <code>null</code> reference is passed
	 * as an element, this method throws the {@linkplain IllegalArgumentException}.
	 * 
	 * @param value element to be added to this collection
	 * @throws IllegalArgumentException if the passed value is <code>null</code>
	 */
	@Override
	public void add(Object value) {
		if (value == null) {
			throw new IllegalArgumentException("Element must not be null.");
		}

		if (first == null) {
			ListNode node = new ListNode(null, null, value);
			first = last = node;
		} else {
			ListNode node = new ListNode(last, null, value);
			last.next = node;
			last = node;
		}
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
		
		if (position == size) {
			add(value);
		} else {
			ListNode existing = getNode(position);
			ListNode newNode = new ListNode(existing.prev, existing, value);
			if (position != 0) {
				existing.prev.next = newNode;
			} else {
				first = newNode;
			}
			existing.prev = newNode;
			size++;
		}
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
	 * <code>index</code>.
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
		
		return getNode(index).value;
	}
	
	/**
	 * Searches the collection and returns the index of the first occurrence of
	 * the given value or -1 if the value is not found. If the given value is
	 * not <code>null</code>, the equality of the given value is determined by
	 * {@link Object#equals equals} method. Since this collection does not allow
	 * <code>null</code> references, this method returns -1 immediately in case a
	 * <code>null</code> reference is passed.
	 * 
	 * @param value element to search for
	 * @return the index of the first occurrence of the specified element in
	 *         this collection, or -1 if this it does not contain the element
	 */
	public int indexOf(Object value) {
		if (value != null) {
			ListNode cur = first;
			for (int i = 0; i < size; i++) {
				if (value.equals(cur.value)) {
					return i;
				}
				cur = cur.next;
			}
		}
		return -1;
	}
	
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
	 * subsequent elements to the left (subtracts one from their indices).
	 *
	 * @param index the index of the element to be removed
	 * @throws IndexOutOfBoundsException
	 *             if the given index is not in range 0 to size-1
	 */
	public void remove(int index) {
		checkIndex(index);
		
		ListNode node = getNode(index);
		/* Check if it's the first element. */
		if (index != 0) {
			node.prev.next = node.next;
		} else {
			first = node.next;
		}
		/* Check if it's the last element. */
		if (index != size-1) {
			node.next.prev = node.prev;
		} else {
			last = node.prev;
		}
		
		node.value = null;
		size--;
	}
	
	@Override
	public void clear() {
		first = last = null;
		size = 0;
	}
	
	@Override
	public Object[] toArray() {
		Object[] arr = new Object[size];
		
		ListNode cur = first;
		for (int i = 0; i < size; i++) {
			arr[i] = cur.value;
			cur = cur.next;
		}
		
		return arr;
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
		
		ListNode cur = first;
		for (int i = 0; i < size; i++) {
			processor.process(cur.value);
			cur = cur.next;
		}
	}
	
	/**
	 * Returns the node at the specified element index.
	 * 
	 * @param index index of the node
	 * @return the node at the specified element index
	 */
	private ListNode getNode(int index) {
		/* Check the closer position. */
		if (index < size/2) {
			ListNode cur = first;
			for (int i = 0; i < index; i++) {
				cur = cur.next;
			}
			return cur;
		} else {
			ListNode cur = last;
			for (int i = size - 1; i > index; i--) {
				cur = cur.prev;
			}
			return cur;
		}
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

	/**
	 * A representation of the list node used by this class for storing values
	 * in a list.
	 *
	 * @author Mario Bobic
	 */
	private static class ListNode {
		
		/** A list node that precedes this one. */
		private ListNode prev;
		/** A list node that succeeds this one. */
		private ListNode next;
		/** Value contained in this list node. */
		private Object value;
		
		/**
		 * Constructs a new instance of a ListNode with the given parameters.
		 * 
		 * @param prev list node that precedes this one
		 * @param next list node that succeeds this one
		 * @param value value to be stored to this list node
		 */
		private ListNode(ListNode prev, ListNode next, Object value) {
			super();
			this.prev = prev;
			this.next = next;
			this.value = value;
		}

	}
}
