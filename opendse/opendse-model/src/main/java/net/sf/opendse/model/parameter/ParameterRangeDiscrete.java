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

/**
 * The {@code ParameterRangeDiscrete} is a {@link Integer}-valued parameter
 * within a lower and an upper bound.
 * 
 * @author Falko Höfte
 * 
 */
public class ParameterRangeDiscrete implements Parameter {

	protected final int value;
	protected final int lb;
	protected final int ub;

	/**
	 * Constructs a {@code ParameterRange}.
	 * 
	 * @param value
	 *            the default value
	 * @param lb
	 *            the lower bound
	 * @param ub
	 *            the upper bound
	 */
	public ParameterRangeDiscrete(int value, int lb, int ub) {
		this.value = value;
		this.lb = lb;
		this.ub = ub;
	}

	/**
	 * Returns the lower bound.
	 * 
	 * @return the lower bound
	 */
	public int getLowerBound() {
		return lb;
	}

	/**
	 * Returns the upper bound.
	 * 
	 * @return the upper bound
	 */
	public int getUpperBound() {
		return ub;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.parameter.Parameter#getValue()
	 */
	@Override
	public Integer getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value + " (" + lb + "," + ub + ")";
	}

}
