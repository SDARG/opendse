package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Link;
import net.sf.opendse.optimization.encoding.variables.Variable;

/**
 * Class representing a variable used to express whether a link is allocated in
 * the implementation or not (L = 1 => link is allocated).
 * 
 * @author Fedor Smirnov
 *
 */
public class L extends Variable implements AllocationVariable {

	protected L(Link l) {
		super(l);
	}

	public Link getLink() {
		return get(0);
	}
}
