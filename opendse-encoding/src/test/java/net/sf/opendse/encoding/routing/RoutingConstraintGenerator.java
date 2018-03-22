package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link RoutingConstraintGenerator} generates the routing constraints for
 * a given communication flow and returns the generated {@link RoutingVariable}s.
 * 
 * @author Fedor Smirnov
 *
 */
public interface RoutingConstraintGenerator {

	/**
	 * formulates the constraints and returns the used {@link RoutingVariable}s
	 * 
	 * @param communicationVariable
	 *            the variable for the encoding of the routed communication
	 * @param communicationFlows
	 *            the flow of the routed communication
	 * @param routing
	 *            the specification routing graph of the communication
	 * @param constraints
	 *            the hitherto formulated constraints
	 * @return the encoded {@link RoutingVariable}s
	 */
	public Set<RoutingVariable> toConstraints(T communicationVariable, CommunicationFlow communicationFlows,
			Architecture<Resource, Link> routing, Set<Constraint> constraints);
}
