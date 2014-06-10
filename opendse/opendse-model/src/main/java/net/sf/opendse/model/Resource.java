package net.sf.opendse.model;

/**
 * The {@code Resource} is the basic vertex element in {@link Architecture}
 * graphs.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Resource extends Node {

	/**
	 * Constructs a new resource.
	 * 
	 * @param id
	 *            the id
	 */
	public Resource(String id) {
		super(id);
	}

	/**
	 * Constructs a new resource.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Resource(Element parent) {
		super(parent);
	}

}
