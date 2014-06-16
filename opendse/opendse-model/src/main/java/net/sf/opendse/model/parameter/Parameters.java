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

import java.util.Arrays;
import java.util.List;

/**
 * The {@code Parameters} provides methods to create {@link Parameter}
 * attributes.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Parameters {

	/**
	 * Constructs a {@link ParameterSelect}.
	 * 
	 * @param def
	 *            the default value
	 * @param select
	 *            the list of elements
	 * @return the parameter object
	 */
	public static ParameterSelect select(Object def, Object... select) {
		ParameterSelect parameter = new ParameterSelect(def, null, Arrays.asList(select));
		return parameter;
	}

	/**
	 * Constructs a {@link ParameterSelect}.
	 * 
	 * @param reference
	 *            the reference parameter
	 * @param def
	 *            the default value
	 * @param select
	 *            the list of elements
	 * @return the parameter object
	 */
	public static ParameterSelect selectRef(String reference, Object def, Object... select) {
		ParameterSelect parameter = new ParameterSelect(def, reference, Arrays.asList(select));
		return parameter;
	}

	/**
	 * Constructs a {@link ParameterSelect}.
	 * 
	 * @param reference
	 *            the reference parameter
	 * @param def
	 *            the default value
	 * @param select
	 *            the list of elements
	 * @return the parameter object
	 */
	public static ParameterSelect selectRefList(String reference, Object def, List<Object> select) {
		ParameterSelect parameter = new ParameterSelect(def, reference, select);
		return parameter;
	}

	/**
	 * Constructs a {@link ParameterRange}.
	 * 
	 * @param def
	 *            the default value
	 * @param lb
	 *            the lower bound
	 * @param ub
	 *            the upper bound
	 * @return the parameter object
	 */
	public static ParameterRange range(double def, double lb, double ub) {
		return range(def, lb, ub, 0);
	}

	/**
	 * Constructs a {@link ParameterRange}.
	 * 
	 * @param def
	 *            the default value
	 * @param lb
	 *            the lower bound
	 * @param ub
	 *            the upper bound
	 * @param granularity
	 *            the granularity (step-size)
	 * @return the parameter object
	 */
	public static ParameterRange range(double def, double lb, double ub, double granularity) {
		ParameterRange range = new ParameterRange(def, lb, ub, granularity);
		return range;
	}

	/**
	 * Constructs a {@link ParameterUniqueID}.
	 * 
	 * @param def
	 *            the default value
	 * @param identifier
	 *            the identifier
	 * @return the parameter object
	 */
	public static ParameterUniqueID uniqueID(int def, String identifier) {
		return new ParameterUniqueID(def, identifier);
	}

}
