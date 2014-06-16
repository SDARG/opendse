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
package net.sf.opendse.model.parameter;

import java.util.List;

import cern.colt.Arrays;

/**
 * The {@code ParameterSelect} is a parameter that selects from a set of
 * elements.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class ParameterSelect implements Parameter {

	protected final Object value;
	protected final Object[] elements;
	protected final String reference;

	/**
	 * Constructs a {@code ParameterSelect}.
	 * 
	 * @param value
	 *            the default value
	 * @param reference
	 *            the reference parameter
	 * @param elements
	 *            the set of available elements
	 */
	public ParameterSelect(Object value, String reference, List<Object> elements) {
		super();
		this.value = value;
		this.reference = reference;
		this.elements = new Object[elements.size()];
		for(int i=0; i<elements.size(); i++){
			this.elements[i] = elements.get(i);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.parameter.Parameter#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * Returns the set of available elements.
	 * 
	 * @return the set of elements
	 */
	public Object[] getElements() {
		return elements;
	}

	/**
	 * Returns the reference of the parameter.
	 * 
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * Returns the first index of the object.
	 * 
	 * @param o
	 *            the object
	 * @return the index
	 */
	public int indexOf(Object o) {
		for (int i = 0; i < elements.length; i++) {
			if (o.equals(elements[i])) {
				return i;
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value + " " + Arrays.toString(elements) + ((reference != null) ? " " + reference : "");
	}

}
