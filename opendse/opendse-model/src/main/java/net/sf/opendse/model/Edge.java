package net.sf.opendse.model;

/**
 * The {@code Edge} is the basic edge element in a {@link Graph}.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public abstract class Edge extends Element {

	/**
	 * Constructs a new edge.
	 * 
	 * @param id
	 *            the id
	 */
	public Edge(String id) {
		super(id);
	}

	/**
	 * Constructs a new edge.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Edge(Element parent) {
		super(parent);
	}

}
