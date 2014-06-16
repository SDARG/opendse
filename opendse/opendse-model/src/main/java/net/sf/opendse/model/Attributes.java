/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
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
