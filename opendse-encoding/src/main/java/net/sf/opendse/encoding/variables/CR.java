package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.TaskPropertyService;

/**
 * The {@link CR} indicates whether the communication C is routed over the
 * resource R.
 * 
 * @author Fedor Smirnov
 *
 */
public class CR extends Variable implements RoutingVariable {

	protected CR(Task communication, Resource resource) {
		super(communication, resource);
		if (!TaskPropertyService.isCommunication(communication)) {
			throw new IllegalArgumentException("The task " + communication.getId() + " is not a communication.");
		}
	}

	public Task getCommunication() {
		return get(0);
	}

	public Resource getResource() {
		return get(1);
	}
}
