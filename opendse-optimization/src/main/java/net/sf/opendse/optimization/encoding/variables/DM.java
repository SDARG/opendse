package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * Essentially, a DM variable is the result of logical AND operation between a
 * {@link DTT} and a {@link M} variable. It is used to determine whether a
 * mapping is relevant for the routing of a communication that has the
 * dependency as outgoing or incoming edge.
 * 
 * @author Fedor Smirnov
 *
 */
public class DM extends Variable {

	protected DM(Dependency dependency, Mapping<Task, Resource> mapping) {
		super(dependency, mapping);
	}
	
	public Dependency getDependency() {
		return get(0);
	}
	
	public Mapping<Task, Resource> getMapping(){
		return get(1);
	}
	
}
