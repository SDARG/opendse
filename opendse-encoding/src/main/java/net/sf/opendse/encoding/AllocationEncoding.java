package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;

/**
 * 
 * 
 * 
 * The {@link AllocationEncoding} encodes a valid allocation of the architecture
 * resources. An encoder implementing this interface TODO: complete the comments
 * for this class
 * 
 * @author Fedor Smirnov
 *
 */
public interface AllocationEncoding {

	/**
	 * Formulates and returns the constraints describing a resource allocation which
	 * is valid with respect to the application, the mapping, and the routing
	 * decisions.
	 * 
	 * @param mappingVariables
	 * @param routingVariables
	 * @param architecture
	 * @return
	 */
	public Set<Constraint> toConstraints(Set<MappingVariable> mappingVariables, Set<RoutingVariable> routingVariables,
			Architecture<Resource, Link> architecture);

}
