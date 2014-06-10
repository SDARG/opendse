package net.sf.opendse.model;

import java.util.Set;

import net.sf.opendse.model.parameter.Parameter;

/**
 * The {@code IAttributes} interface applies for classes that contain
 * attributes. An attribute is pair of an identifier and a value object. The
 * identifier must be a String.
 * 
 * 
 * @author Martin Lukasiewycz
 * 
 */
public interface IAttributes {

	/**
	 * Sets the attribute.
	 * 
	 * @param identifier
	 *            the identifier of the attribute
	 * @param object
	 *            the value of the attribute
	 */
	public void setAttribute(String identifier, Object object);

	/**
	 * Returns the attribute
	 * 
	 * @param <O>
	 *            the type of the attribute
	 * @param identifier
	 *            the identifier of the attribute
	 * @return the value of the value of the attribute
	 */
	public <O> O getAttribute(String identifier);

	/**
	 * Return the parameter definition or {@code null} if the attribute is not
	 * defined as parameter.
	 * 
	 * @param identifier
	 *            the identifier of the attribute
	 * @return the parameter
	 */
	public Parameter getAttributeParameter(String identifier);

	/**
	 * Returns the map of all pairs of attributes.
	 * 
	 * @return the attribute map
	 */
	public IAttributes getAttributes();

	/**
	 * Returns the attribute names.
	 * 
	 * @return the attribute names
	 */
	public Set<String> getAttributeNames();

	/**
	 * Tests whether an attribute is defined.
	 * 
	 * @param identifier
	 *            the identifier of the attribute
	 * @return {@code true} if defined
	 */
	public boolean isDefined(String identifier);

}
