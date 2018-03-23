package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.DDR;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link RoutingResourceEncoder} formulates the constraints encoding the
 * {@link DDR} variables.
 * 
 * @author Fedor Smirnov
 *
 */
public interface RoutingResourceEncoder {

	/**
	 * Formulates the constraints encoding the {@link DDR} variables.
	 * 
	 * @param communicationFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param routing
	 *            the {@link Architecture} representing the routing possibilities of
	 *            the communication
	 * @return the set of {@link Constraint}s encoding the {@link DDR} variables for
	 *         the given {@link CommunicationFlow}
	 */
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing);

}
