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

/**
 * The {@code ParameterUniqueID} is a parameter that assigns a unique id which
 * is an integer value. Each element with the same identifier will have a
 * different id.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class ParameterUniqueID implements Parameter {

	protected final Integer value;
	protected final String identifier;

	/**
	 * Constructs a {@code ParameterUniqueID}.
	 * 
	 * @param value
	 *            the default value
	 * @param identifier
	 *            the identifier
	 */
	public ParameterUniqueID(Integer value, String identifier) {
		super();
		this.value = value;
		this.identifier = identifier;
	}

	/**
	 * Returns the identifier.
	 * 
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value + " [UID:" + identifier + "]";
	}

}
