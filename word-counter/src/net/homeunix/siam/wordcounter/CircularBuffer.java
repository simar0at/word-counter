package net.homeunix.siam.wordcounter;

import java.nio.BufferUnderflowException;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Implementation of a circular buffer with generic parameter. Used as a fifo things
 * can be added indefinitely (of overwrite is true) and the last n - 1 of these can be accessed.
 * 
 * @author Omar Siam
 *
 * @param <T>
 * Type of things stored in this buffer.
 */
public class  CircularBuffer<T> implements Queue<T> {

	/**
	 * Array used to store things.
	 */
	private T[] buffer;

	/**
	 * Tail pointer where things are read.
	 */
	private int tail;

	/**
	 * Head pointer where things are written
	 */
	private int head;

	/**
	 * Allow items to be added indefinitely.
	 * If false an exception is thrown if too many things are added.
	 */
	private boolean overwrite = true;

	/**
	 * Create a circular buffer for n elements. Best values for n are 2^x - 1
	 * @param n
	 * Number of elements that can be stored
	 */
	@SuppressWarnings("unchecked")
	public CircularBuffer(int n) {
		buffer = (T[]) new Object[n + 1];
		tail = 0;
		head = 0;
	}
	
	/**
	 * Add a thing to this buffer. Throws an exception if to many things are added and
	 * overwrite isn't true.
	 */
	@Override
	public boolean add(T toAdd) throws IllegalStateException {
		if (toAdd == null) throw new NullPointerException();
		if (!offer(toAdd))
			throw new IllegalStateException();
		return true;
	}
	
	/**
	 * Add a thing to this buffer. Returns false if to many things are added and
	 * overwrite isn't true.
	 */		
	@Override
	public boolean offer(T toAdd) {
		if (toAdd == null) return false;
		if ((tail == 0 && head == buffer.length - 1) || head == (tail - 1)) {		
			if (!overwrite) return false;
			buffer[tail++] = null;
			tail = tail % buffer.length;
			buffer[head++] = toAdd;			
		} else {
			buffer[head++] = toAdd;
		}
		head = head % buffer.length;
		return true;
	}

	/**
	 * Remove an item from the tail of the circular buffer. Throw an exception if the buffer is empty.
	 */
	@Override
	public T remove() {
		T t = poll();
		if (t == null)
			throw new BufferUnderflowException();
		return t;
	}
	
	/**
	 * Remove an item from the tail of the circular buffer.
	 */
	@Override
	public T poll() {
		T t = null;
		int adjTail = tail > head ? tail - buffer.length : tail;
		if (adjTail < head) {
			t = (T) buffer[tail];
			buffer[tail++] = null;
			tail = tail % buffer.length;
		}
		return t;
	}
	
	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		if (arg0.size() == 0)
			return false;
		for (T t: arg0) {
			add(t);
		}
		return true;
	}

	@Override
	public void clear() {
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = null;
		head = tail = 0;
	}

	@Override
	public boolean contains(Object arg0) {
		for (T t: buffer) {
			if (arg0.equals(t)) return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		for (Object o: arg0) {
			if (!contains(o))
				return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return head == tail;
	}

	/**
	 * Iterator needed for using this class in some language features.
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>(){
			{
				read = tail;
			}
			int read;
			@Override
			public boolean hasNext() {
				int adjTail = read > head ? read - buffer.length : read;
				return adjTail < head;
			}

			@Override
			public T next() {
				T ret = (T) buffer[read++];
				read = read % buffer.length;
				return ret;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();	
			}
		};
	}

	/**
	 * Can't remove an arbitrary object from the circular buffer.
	 */
	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Can't remove an arbitrary object from the circular buffer.
	 */
	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}
	/**
	 * Can't remove an arbitrary object from the circular buffer.
	 */
	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return buffer.length - 1;
	}

	@Override
	public Object[] toArray() {
		return toArray(new Object[buffer.length - 1]);
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <T> T[] toArray(T[] arg0) {
		T[] ret = null;
		if (arg0.length >= buffer.length - 1)
			ret = arg0;
		else
			ret = (T[]) new Object[buffer.length - 1];
		int read = tail;
		for (int i = 0; i < buffer.length - 1; i++) {
			ret[i] = (T) buffer[read++];
			read = read % buffer.length;
		}
		return ret;
	}

	@Override
	public T element() {
		T t = (T) buffer[tail];
	    if (t == null) throw new NoSuchElementException();
		return null;
	}

	@Override
	public T peek() {
		return (T) buffer[tail];
	}
	
	
	/**
	 * Gets the element at index of the circular buffer (counting from tail).
	 * @param index
	 * @return
	 */
	public T get(int index) {
		return buffer[(tail + index) % buffer.length];
	}
	
	public String toString() {
		return "CircularBuffer(size=" + buffer.length + ", head=" + head + ", tail=" + tail + ")";
	}

}

