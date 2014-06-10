package net.sf.opendse.model;

/**
 * The {@code Communication} is the default implementation of the
 * {@link ICommunication} interface.
 * 
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Communication extends Task implements ICommunication {

	/**
	 * Constructs a new communication.
	 * 
	 * @param id
	 *            the id
	 */
	public Communication(String id) {
		super(id);
	}

	/**
	 * Constructs a new communication.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Communication(Element parent) {
		super(parent);
	}

}
