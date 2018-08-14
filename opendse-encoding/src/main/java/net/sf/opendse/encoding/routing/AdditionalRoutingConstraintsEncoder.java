package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link AdditionalRoutingConstraintsEncoder} enables an introduction of
 * additional routing constraints.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(AdditionalRoutingConstraintsEncoderMulti.class)
public interface AdditionalRoutingConstraintsEncoder {

	/**
	 * Formulates additional {@link Constraint}s concerning the routing.
	 * 
	 * @param communicationVariable
	 *            the {@link T} variable encoding the activation of the
	 *            communication task that is being routed
	 * @param communicationFlows
	 *            the set of {@link CommunicationFlow}s of the communication that is
	 *            being routed
	 * @param routing
	 *            the {@link Architecture} representing the routing possibilities of
	 *            the communication that is being routed
	 * @return a set of additional {@link Constraint}s concerning the routing
	 */
	public Set<Constraint> toConstraints(T communicationVariable, Set<CommunicationFlow> communicationFlows,
			Architecture<Resource, Link> routing);

}
