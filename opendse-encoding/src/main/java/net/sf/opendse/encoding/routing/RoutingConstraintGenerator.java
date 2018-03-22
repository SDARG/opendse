package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.MappingEncoding;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link RoutingConstraintGenerator} generates the routing constraints for
 * a given {@link CommunicationFlow}.
 * 
 * @author Fedor Smirnov
 *
 */
public interface RoutingConstraintGenerator {

	/**
	 * Encodes the routing constraints for the given {@link CommunicationFlow}.
	 * 
	 * @param communicationFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param mappingVariables
	 *            the {@link MappingVariable}s encoded by the
	 *            {@link MappingEncoding}
	 * @param routing
	 *            the {@link Architecture} describing the routing options of the
	 *            communication of the {@link CommunicationFlow}
	 * @return the set of constraints encoding the routing of the
	 *         {@link CommunicationFlow}
	 */
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Set<MappingVariable> mappingVariables,
			Architecture<Resource, Link> routing);
}