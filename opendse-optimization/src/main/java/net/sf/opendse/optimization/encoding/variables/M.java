package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.encoding.variables.Variable;

/**
 * Class that represents the activation variable of a mapping. The variable is
 * set to 1 if the mapping is active in the implementation.
 * 
 * @author Fedor Smirnov
 *
 */
public class M extends Variable{

	protected M(Mapping<Task, Resource> mapping) {
		super(mapping);
	}

	public Mapping<Task, Resource> getMapping() {
		return get(0);
	}

}
