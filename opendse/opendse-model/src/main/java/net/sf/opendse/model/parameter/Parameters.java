/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
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
	 * @param def
	 *            the default value
	 * @param select
	 *            the list of elements
	 * @return the parameter object
	 */
	public static ParameterSelect selectList(Object def, List<Object> select) {
		ParameterSelect parameter = new ParameterSelect(def, null, select);
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
	 * Constructs a {@link ParameterRangeInt}.
	 * 
	 * @param def
	 *            the default value
	 * @param lb
	 *            the lower bound
	 * @param ub
	 *            the upper bound
	 * @return the parameter object
	 */
	public static ParameterRangeInt range(int def, int lb, int ub) {
		ParameterRangeInt range = new ParameterRangeInt(def, lb, ub);
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
