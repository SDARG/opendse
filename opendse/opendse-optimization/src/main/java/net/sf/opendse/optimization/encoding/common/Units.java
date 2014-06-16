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
package net.sf.opendse.optimization.encoding.common;

import java.util.HashMap;

import org.opt4j.satdecoding.Literal;

/**
 * The {@code Units} contains unit assignments.
 * 
 * @author Martin Lukasiewycz
 * 
 */
@SuppressWarnings("serial")
public class Units extends HashMap<Object, Boolean> {

	public interface Filter {
		public boolean keep(Object var);
	}

	public class StandardFilter implements Filter {

		public boolean keep(Object var) {
			return true;
		}

	}

	protected final Filter filter;

	public Units() {
		super();
		filter = new StandardFilter();
	}

	public Units(Filter filter) {
		super();
		this.filter = filter;
	}

	public class UnitException extends RuntimeException {
		public UnitException(String message) {
			super(message);
		}
	}

	public Boolean put(Object var, Boolean phase) {
		if (!filter.keep(var)) {
			return null;
		}

		if (containsKey(var)) {
			if (get(var) != phase) {
				throw new UnitException("Object " + var + " to false/true");
			}
		}

		return super.put(var, phase);
	}

	public Boolean addUnit(Object var, Boolean phase) {
		return put(var, phase);
	}

	public Boolean addUnit(Literal literal) {
		Object var = literal.variable();
		boolean phase = literal.phase();
		return put(var, phase);
	}

	public void addUnits(Iterable<Literal> literals) {
		for (Literal literal : literals) {
			addUnit(literal);
		}
	}

}
