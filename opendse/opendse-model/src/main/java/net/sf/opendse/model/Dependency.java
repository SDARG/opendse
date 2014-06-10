package net.sf.opendse.model;

/**
 * The {@code Dependency} is the basic edge element in {@link Applications}
 * graphs.
 * 
 * @author lukasiewycz
 * 
 */
public class Dependency extends Edge {

	/**
	 * Constructs a new dependency.
	 * 
	 * @param id
	 *            the id
	 */
	public Dependency(String id) {
		super(id);
	}

	/**
	 * Constructs a new dependency.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Dependency(Element parent) {
		super(parent);
	}

}
