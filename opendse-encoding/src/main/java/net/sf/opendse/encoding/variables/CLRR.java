package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.TaskPropertyService;

/**
 * The {@link CLRR} indicates that the communication C is routed over the
 * directed link L.
 * 
 * @author Fedor Smirnov
 *
 */
public class CLRR extends Variable implements RoutingVariable {

	protected CLRR(Task communication, Link link, Resource source, Resource destination) {
		super(communication, link, source, destination);
		if (!TaskPropertyService.isCommunication(communication)) {
			throw new IllegalArgumentException("The task " + communication.getId() + " is not a communication.");
		}
	}

	public Task getCommunication() {
		return get(0);
	}

	public Link getLink() {
		return get(1);
	}

	public Resource getSource() {
		return get(2);
	}

	public Resource getDestination() {
		return get(3);
	}
}
