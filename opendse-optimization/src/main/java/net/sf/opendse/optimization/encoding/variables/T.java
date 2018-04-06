package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.encoding.variables.Variable;

/**
 * Class that represents the usage of an application task as a variable. Setting
 * the variable to 1 indicates that the task must be included into the
 * application.
 * 
 * @author Fedor Smirnov
 *
 */
public class T extends Variable{

	protected T(Task t) {
		super(t);
	}

	public Task getTask() {
		return get(0);
	}

}
