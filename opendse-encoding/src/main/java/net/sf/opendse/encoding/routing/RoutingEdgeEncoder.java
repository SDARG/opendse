package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.encoding.variables.DDLRR;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link RoutingEdgeEncoder} formulates the constraints encoding the
 * {@link DDLRR} variables.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(RoutingEdgeEncoderNonRedundant.class)
public interface RoutingEdgeEncoder {

	/**
	 * Formulates the constraints encoding the {@link DDLRR} variables.
	 * 
	 * @param communicationFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param routing
	 *            the {@link Architecture} representing the routing possibilities of
	 *            the communication
	 * @return the set of {@link Constraint}s encoding the {@link DDLRR} variables
	 *         for the given {@link CommunicationFlow}
	 */
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing);

}
