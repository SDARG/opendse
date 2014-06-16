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
 * The {@code ParameterRange} is a double-valued parameter wihtin a lower and an
 * upper bound.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class ParameterRange implements Parameter {

	protected final double value;
	protected final double lb;
	protected final double ub;
	protected final double granularity;

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
	public ParameterRange(double value, double lb, double ub) {
		this(value, lb, ub, 0);
	}

	/**
	 * Constructs a {@code ParameterRange}.
	 * 
	 * @param value
	 *            the default value
	 * @param lb
	 *            the lower bound
	 * @param ub
	 *            the upper bound
	 * @param granularity
	 *            the granularity (step-size)
	 */
	public ParameterRange(double value, double lb, double ub, double granularity) {
		super();
		this.value = value;
		this.lb = lb;
		this.ub = ub;
		this.granularity = granularity;
	}

	/**
	 * Returns the lower bound.
	 * 
	 * @return the lower bound
	 */
	public double getLowerBound() {
		return lb;
	}

	/**
	 * Returns the upper bound.
	 * 
	 * @return the upper bound
	 */
	public double getUpperBound() {
		return ub;
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
	 * Normalizes the value.
	 * 
	 * @param value
	 *            the value
	 * @return the normalized value
	 */
	public double normalizeValue(double value) {
		if (granularity > 0) {
			double v = value / granularity;
			v = Math.round(v) * granularity;
			return v;
		} else {
			return value;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value + " (" + lb + "," + ub + "," + granularity+")";
	}

}
