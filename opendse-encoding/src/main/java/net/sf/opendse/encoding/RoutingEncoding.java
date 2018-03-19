package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;

/**
 * formulates the constraints encoding a valid routing and returns the variables
 * containing the routing information
 * 
 * @author Fedor Smirnov
 *
 */
public interface RoutingEncoding {

	/**
	 * Formulates the routing constraints and add them to the input constraint set.
	 * Returns the set of the variables containing the routing information.
	 * 
	 * @param applicationVariables
	 * @param mappingVariables
	 * @param routings
	 * @param constraints
	 * @return
	 */
	public Set<RoutingVariable> toConstraints(Set<ApplicationVariable> applicationVariables,
			Set<MappingVariable> mappingVariables, Routings<Task, Resource, Link> routings,
			Set<Constraint> constraints);
}
