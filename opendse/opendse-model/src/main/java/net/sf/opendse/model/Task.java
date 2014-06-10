package net.sf.opendse.model;

/**
 * The {@code Task} is the basic vertex element for {@link Application} graphs.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Task extends Node {

	/**
	 * Type of a {@code Task}.
	 * 
	 * @author Martin Lukasiewycz
	 * 
	 */
	public enum Type {
		/**
		 * Functional task.
		 */
		FUNCTION,
		/**
		 * Communication task.
		 */
		COMMUNICATION;
	}

	/**
	 * Constructs a new task.
	 * 
	 * @param id
	 *            the id
	 */
	public Task(String id) {
		super(id);
	}

	/**
	 * Constructs a new parent.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Task(Element parent) {
		super(parent);
	}

}
