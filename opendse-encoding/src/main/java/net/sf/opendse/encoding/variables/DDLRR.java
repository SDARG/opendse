package net.sf.opendse.encoding.variables;

import net.sf.opendse.encoding.routing.CommunicationFlow;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link DDLRR} variable is used to encode whether the
 * {@link CommunicationFlow} formed by two {@link Dependency}s is routed over a
 * directed {@link Link} between two {@link Resource}s.
 * 
 * @author Fedor Smirnov
 *
 */
public class DDLRR extends Variable {

	protected DDLRR(CommunicationFlow communicationFlow, Link link, Resource sourceResource, Resource destResource) {
		super(communicationFlow, link, sourceResource, destResource);
	}

	public CommunicationFlow getCommunicationFlow() {
		return get(0);
	}

	public Link getLink() {
		return get(1);
	}

	public Resource getSourceResource() {
		return get(2);
	}

	public Resource getDestResource() {
		return get(3);
	}
}
