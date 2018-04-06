package net.sf.opendse.encoding.variables;

import net.sf.opendse.encoding.routing.CommunicationFlow;
import net.sf.opendse.model.Resource;

/**
 * The {@link DDsR} variable encodes whether a resource is the source (a root of
 * the routing graph) of a {@link CommunicationFlow}.
 * 
 * @author Fedor Smirnov
 *
 */
public class DDsR extends Variable {

	protected DDsR(CommunicationFlow communicationFlow, Resource resource) {
		super(communicationFlow, resource);
	}

	public CommunicationFlow getCommunicationFlow() {
		return get(0);
	}

	public Resource getResource() {
		return get(1);
	}
}
