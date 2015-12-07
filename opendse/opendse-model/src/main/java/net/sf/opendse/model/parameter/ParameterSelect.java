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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import java.util.List;

import cern.colt.Arrays;

/**
 * The {@code ParameterSelect} is a parameter that selects from a set of
 * elements.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class ParameterSelect implements Parameter<Object> {

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
		for (int i = 0; i < elements.size(); i++) {
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
