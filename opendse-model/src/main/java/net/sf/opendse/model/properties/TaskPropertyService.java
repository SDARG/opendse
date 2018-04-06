package net.sf.opendse.model.properties;

import net.sf.opendse.model.ICommunication;
import net.sf.opendse.model.Task;

/**
 * offers convenience methods to access properties of tasks
 * 
 * @author Fedor Smirnov
 *
 */
public class TaskPropertyService extends AbstractPropertyService {
	
	private TaskPropertyService() {
	}

	public static boolean isProcess(Task task) {
		return !isCommunication(task);
	}

	public static boolean isCommunication(Task task) {
		return task instanceof ICommunication;
	}
}
