package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Task;

/**
 * encodes the activation of a task in the application graph
 * 
 * @author Fedor Smirnov
 *
 */
public class T extends Variable implements ApplicationVariable {

	protected T(Task task) {
		super(task);
	}
	
	public Task getTask() {
		return get(0);
	}
}
