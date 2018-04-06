package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link NoAdditionalConstraintsEncoder} is used when only the basic
 * routing constraints are needed.
 * 
 * @author Fedor Smirnov
 *
 */
public class NoAdditionalConstraintsEncoder implements AdditionalRoutingConstraintsEncoder {

	@Override
	public Set<Constraint> toConstraints(T communicationVariable, Set<CommunicationFlow> communicationFlows,
			Architecture<Resource, Link> routing) {
		// Does not create any additional constraints. Returns an empty set.
		return new HashSet<Constraint>();
	}

}
