package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;

/**
 * The {@link L} {@link Variable} encodes whether the corresponding {@link Link}
 * is allocated in the {@link Architecture} of the implementation. If the L
 * variable is set to 1, the corresponding link is allocated.
 * 
 * @author Fedor Smirnov
 *
 */
public class L extends Variable implements AllocationVariable {

	protected L(Link link) {
		super(link);
	}

	public Link getLink() {
		return get(0);
	}
}