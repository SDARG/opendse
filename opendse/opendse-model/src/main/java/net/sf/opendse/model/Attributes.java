package net.sf.opendse.model;

import java.util.Set;
import java.util.TreeMap;

import net.sf.opendse.model.parameter.Parameter;

/**
 * The {@link Attributes} is the default implementation of the
 * {@link IAttributes} interface using a {@code HashMap}.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Attributes extends TreeMap<String, Object> implements IAttributes {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#getAttribute(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <O> O getAttribute(String identifier) {
		Object value = get(identifier);
		return (O) ((value instanceof Parameter) ? ((Parameter) value).getValue() : value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String identifier, Object object) {
		put(identifier, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#getAttributes()
	 */
	@Override
	public Attributes getAttributes() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.adse.model.IAttributes#getAttributeParameter(java.lang.String)
	 */
	@Override
	public Parameter getAttributeParameter(String identifier) {
		if (isParameter(identifier)) {
			return (Parameter) get(identifier);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#getAttributeNames()
	 */
	@Override
	public Set<String> getAttributeNames() {
		return keySet();
	}

	/**
	 * Returns {@code true} if the attribute value is a parameter.
	 * 
	 * @param identifier
	 *            the identifier
	 * @return {@code true} if the attribute value is a parameter
	 */
	public boolean isParameter(String identifier) {
		Object value = get(identifier);
		return value instanceof Parameter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#isDefined(java.lang.String)
	 */
	@Override
	public boolean isDefined(String identifier) {
		return this.containsKey(identifier);
	}

}
