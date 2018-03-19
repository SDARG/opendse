package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;

/**
 * encodes the activation of a dependency
 * 
 * @author Fedor Smirnov
 *
 */
public class DTT extends Variable implements ApplicationVariable {

	protected DTT(Dependency dependency, Task sourceTask, Task destinationTask) {
		super(dependency, sourceTask, destinationTask);
	}

	public Dependency getDependency() {
		return get(0);
	}
	
	public Task getSourceTask() {
		return get(1);
	}
	
	public Task getDestinationTask() {
		return get(2);
	}
}
