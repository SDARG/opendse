package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.optimization.encoding.variables.Variable;

/**
 * Encoding variables that describe that the destination dependency linking two
 * tasks is routed over the directed link connecting two resources (1) or not
 * (0).
 * 
 * @author Fedor Smirnov
 *
 */
public class DDLRR extends Variable {

	protected DDLRR(Dependency sourceDependency, Dependency destinationDependency, Link link, Resource srcRes,
			Resource destRes) {
		super(sourceDependency, destinationDependency, link, srcRes, destRes);
	}

	public Dependency getSourceDependency() {
		return get(0);
	}

	public Dependency getDestinationDependency() {
		return get(1);
	}

	public Link getLink() {
		return get(2);
	}

	public Resource getSrcRes() {
		return get(3);
	}

	public Resource getDestRes() {
		return get(4);
	}
}
