package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Resource;

/**
 * The {@link R} {@link Variable} encodes whether a {@link Resource} shall be
 * allocated into the {@link Architecture} of the implementation. If the R
 * variables is set to 1, the corresponding resource is allocated.
 * 
 * @author Fedor Smirnov
 *
 */
public class R extends Variable implements AllocationVariable {

	protected R(Resource resource) {
		super(resource);
	}

	public Resource getResource() {
		return get(0);
	}
}