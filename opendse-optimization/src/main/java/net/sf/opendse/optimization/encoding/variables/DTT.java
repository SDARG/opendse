package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.encoding.variables.Variable;

/**
 * Variable representing the activation of the dependency d from the source task
 * src to the dest task.
 * 
 * @author Fedor Smirnov
 *
 */
public class DTT extends Variable implements ApplicationVariable{

	protected DTT(Dependency d, Task src, Task dest) {
		super(d, src, dest);
	}

	public Dependency getDependency() {
		return get(0);
	}

	public Task getSourceTask() {
		return get(1);
	}

	public Task getDestTask() {
		return get(2);
	}

}
