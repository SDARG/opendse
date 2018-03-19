package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.encoding.variables.AllocationVariable;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;

/**
 * formulates the constraints for a valid allocation and returns the set of
 * variables containing the allocation information
 * 
 * @author Fedor Smirnov
 *
 */
public interface AllocationEncoding {

	/**
	 * Formulates the allocation constraints and adds them to the input constraint
	 * set. Returns the set of variables describing the allocation.
	 * 
	 * @param mappingVariables
	 * @param routingVariables
	 * @param architecture
	 * @param constraints
	 * @return set of variables describing the encoded allocation
	 */
	public Set<AllocationVariable> toConstraints(Set<MappingVariable> mappingVariables,
			Set<RoutingVariable> routingVariables, Architecture<Resource, Link> architecture,
			Set<Constraint> constraints);

}
