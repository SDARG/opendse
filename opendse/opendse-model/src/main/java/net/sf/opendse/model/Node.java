package net.sf.opendse.model;

/**
 * The {@code Node} is the basic vertex element in a {@link Graph}.
 * 
 * 
 * @author Martin Lukasiewycz
 * 
 */
public abstract class Node extends Element {

	/**
	 * Constructs a new node.
	 * 
	 * @param id
	 *            the id
	 */
	public Node(String id) {
		super(id);
	}

	/**
	 * Constructs a new node.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Node(Element parent) {
		super(parent);
	}

}
