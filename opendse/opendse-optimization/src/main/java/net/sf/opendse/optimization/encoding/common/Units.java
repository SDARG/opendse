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
