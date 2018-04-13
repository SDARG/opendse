package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link CRR} {@link Variable} encodes whether a {@link Communication} uses
 * the first {@link Resource} before the second. If it is set to 1, the first
 * resource comes before the second one. If it set to 0, the first resource does
 * not come before the second one (it comes after it or one of the resources is
 * not used at all).
 * 
 * @author Fedor Smirnov
 *
 */
public class CRR extends Variable {

	protected CRR (Task comm, Resource first, Resource second) {
		super(comm, first, second);
	}
	
	public Task getComm() {
		return get(0);
	}
	
	public Resource getFirst() {
		return get(1);
	}

	public Resource getSecond() {
		return get(2);
	}
}
