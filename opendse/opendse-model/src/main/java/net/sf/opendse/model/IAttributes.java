/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.model;

import java.util.Set;

import net.sf.opendse.model.parameter.Parameter;

/**
 * The {@code IAttributes} interface applies for classes that contain attributes. An attribute is pair of an identifier
 * and a value object. The identifier must be a String.
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
	 * @return the value of the value of the attribute or <code>null</code> if this attribute is not set.
	 */
	public <O> O getAttribute(String identifier);

	/**
	 * Return the parameter definition or {@code null} if the attribute is not defined as parameter.
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
