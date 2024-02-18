package assignment2;

public class MyStack<E> {

	private MySinglyLinkedList<E> list ;

	/* ADD YOUR CODE HERE */


	public MyStack() {
		list = new MySinglyLinkedList<>();
	}

	public boolean push(E element) {
		list.addFirst(element);
		return true;
	}

	public E pop() {
		if (isEmpty()) {
			throw new java.util.NoSuchElementException("Stack is empty");
		}
		return list.removeFirst();
	}

	public E peek() {
		if (isEmpty()) {
			throw new java.util.NoSuchElementException("Stack is empty");
		}
		return list.peekFirst();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public void clear() {
		list.clear();
	}

	public int getSize() {
		return list.getSize();
	}
	
	
	/* ADD YOUR CODE HERE */

	public String toString() {
		String msg = "" ;
		for ( E e : list) {
			msg = e.toString() + ","  + msg ;
		}
		return msg ;
	}
}
