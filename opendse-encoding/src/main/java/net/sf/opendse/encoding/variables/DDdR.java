package net.sf.opendse.encoding.variables;

import net.sf.opendse.encoding.routing.CommunicationFlow;
import net.sf.opendse.model.Resource;

/**
 * The {@link DDdR} variable encodes whether a resource is a destination (a leaf
 * of the routing graph) of a {@link CommunicationFlow}.
 * 
 * @author Fedor Smirnov
 *
 */
public class DDdR extends Variable {

	protected DDdR(CommunicationFlow communicationFlow, Resource resource) {
		super(communicationFlow, resource);
	}
	
	public CommunicationFlow getCommunicationFlow() {
		return get(0);
	}
	
	public Resource getResource() {
		return get(1);
	}
}
