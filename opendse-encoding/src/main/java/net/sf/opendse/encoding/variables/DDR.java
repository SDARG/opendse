package net.sf.opendse.encoding.variables;

import net.sf.opendse.encoding.routing.CommunicationFlow;
import net.sf.opendse.model.Resource;

/**
 * The {@link DDR} is a {@link Variable} used to encode whether the
 * {@link CommunicationFlow} formed by two dependencies is routed over a
 * {@link Resource}.
 * 
 * @author Fedor Smirnov
 *
 */
public class DDR extends Variable {

	protected DDR (CommunicationFlow communicationFlow, Resource resource) {
		super(communicationFlow, resource);
	}
	
	public CommunicationFlow getCommunicationFlow() {
		return get(0);
	}
	
	public Resource getResource() {
		return get(1);
	}
}
