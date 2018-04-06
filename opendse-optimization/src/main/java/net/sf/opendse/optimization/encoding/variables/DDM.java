package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.encoding.variables.Variable;

/**
 * Variable linking a communication flow to the mapping of a process. DDM = 1 =>
 * the mapping of the process is relevant for the communication flow between the
 * given dependencies (the mapping is irrelevant, if, e.g. one of the
 * dependencies or the mapping is not active).
 * 
 * @author Fedor Smirnov
 *
 */
public class DDM extends Variable {

	protected DDM(Dependency srcDependency, Dependency destDependency, Mapping<Task, Resource> mapping) {
		super(srcDependency, destDependency, mapping);
	}

	public Dependency getSrcDependency() {
		return get(0);
	}

	public Dependency getDestDependency() {
		return get(1);
	}

	public Mapping<Task, Resource> getMapping() {
		return get(2);
	}

}
