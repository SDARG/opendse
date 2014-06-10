package net.sf.opendse.model;

/**
 * The {@code Link} is the basic edge element in {@link Architecture} graphs.
 * 
 * @author lukasiewycz
 * 
 */
public class Link extends Edge {

	/**
	 * Constructs a new link.
	 * 
	 * @param id
	 *            the id
	 */
	public Link(String id) {
		super(id);
	}

	/**
	 * Constructs a new link.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Link(Element parent) {
		super(parent);
	}

}
