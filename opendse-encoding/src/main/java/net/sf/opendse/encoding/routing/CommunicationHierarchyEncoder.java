package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.DDLRR;
import net.sf.opendse.encoding.variables.DDR;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link CommunicationHierarchyEncoder} encoder encodes the hierarchical
 * relation between the {@link CLRR} and the {@link CR} variables on the one and
 * the {@link DDLRR} and the {@link DDR} variables on the other side.
 * 
 * @author Fedor Smirnov
 *
 */
public interface CommunicationHierarchyEncoder {

	/**
	 * Encodes the hierarchical relation between the {@link CLRR} and the {@link CR}
	 * variables on the one and the {@link DDLRR} and the {@link DDR} variables on
	 * the other side.
	 * 
	 * @param communicationVariable
	 *            the {@link T} variable encoding the activation of the
	 *            communication that is being routed
	 * @param communicationFlows
	 *            the set of the {@link CommunicationFlow}s of the communication
	 *            that is being routed
	 * @param routing
	 *            the {@link Architecture} representing the routing possibilities of
	 *            the communication that is being routed
	 * @return the set of {@link Constraint}s encoding the hierarchical relation
	 *         between the {@link CLRR} and the {@link CR} variables on the one and
	 *         the {@link DDLRR} and the {@link DDR} variables on the other side
	 */
	public Set<Constraint> toConstraints(T communicationVariable, Set<CommunicationFlow> communicationFlows,
			Architecture<Resource, Link> routing);

}
