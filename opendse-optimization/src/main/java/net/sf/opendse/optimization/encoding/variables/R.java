package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Resource;
import net.sf.opendse.optimization.encoding.variables.Variable;

/**
 * Class representing a variable that is used to express that a resource is
 * allocated in an implementation (R = 1 => resource is allocated).
 * 
 * @author Fedor Smirnov
 *
 */
public class R extends Variable{

	protected R(Resource resource) {
		super(resource);
	}

	public Resource getResource() {
		return get(0);
	}
}