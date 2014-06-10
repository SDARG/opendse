package net.sf.opendse.encoding.common;

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
