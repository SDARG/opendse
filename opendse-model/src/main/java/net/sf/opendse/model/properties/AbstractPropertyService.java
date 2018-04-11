package net.sf.opendse.model.properties;

import net.sf.opendse.model.Element;

/**
 * ancestor of all property services
 * 
 * @author Fedor Smirnov
 *
 */
public abstract class AbstractPropertyService {

	/**
	 * Checks whether the attribute with the specified name is set
	 * 
	 * @return {@code true} if attribute is set
	 */
	protected static final boolean isAttributeSet(Element element, String attributeName) {
		return element.getAttribute(attributeName) != null;
	}

	/**
	 * To be used if an attribute is always set. Throws an exception if it
	 * isn't.
	 * 
	 * @param element
	 * @param attributeName
	 */
	protected static final void checkAttribute(Element element, String attributeName) {
		if (!isAttributeSet(element, attributeName)) {
			String message = "Attribute " + attributeName + " not set for the element " + element;
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Gets the attribute in the usual way but throws an exception if attribute
	 * not set.
	 * 
	 * @param element
	 * @param attributeName
	 * @return the objects set with the given attribute name as key
	 */
	protected static final Object getAttribute(Element element, String attributeName) {
		checkAttribute(element, attributeName);
		return element.getAttribute(attributeName);
	}
	
}
