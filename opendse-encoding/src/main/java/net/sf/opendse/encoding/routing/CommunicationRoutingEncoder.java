package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.MappingEncoding;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link CommunicationRoutingEncoder} encodes the constraints for a given
 * communication {@link Task}.
 * 
 * @author Fedor Smirnov
 *
 */
public interface CommunicationRoutingEncoder {

	/**
	 * Encodes the constraints for the given communication.
	 * 
	 * @param communicationVariable
	 *            the {@link T} variable describing the activation of the
	 *            communication task
	 * @param communicationFlows
	 *            the {@link CommunicationFlow}s of the communication that is being
	 *            routed
	 * @param routing
	 *            an {@link Architecture} graph that describes the routing
	 *            possibilities of the communication task that is being routed
	 * @param mappingVariables
	 *            the {@link MappingVariable}s encoded by the
	 *            {@link MappingEncoding}
	 * @return the constraints encoding a valid routing of the communication
	 */
	public Set<Constraint> toConstraints(T communicationVariable, Set<CommunicationFlow> communicationFlows,
			Architecture<Resource, Link> routing, Set<MappingVariable> mappingVariables);

}
