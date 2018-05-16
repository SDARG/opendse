package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link AdditionalCommFlowEncoder} encodes additional {@link Constraint}s for each {@link CommunicationFlow}.
 * 
 * @author Fedor Smirnov
 *
 */
public interface AdditionalCommFlowEncoder {

	/**
	 * Formulates additional {@link Constraint}s based on the information available during the routing.
	 * 
	 * @param flow the {@link CommunicationFlow} that being routed
	 * @param routing the {@link Resource}s and {@link Link}s usable for the routing
	 * @param mappingVariables the {@link Variable}s encoding the task mappings
	 * @return
	 */
	public Set<Constraint> toConstraints(CommunicationFlow flow, Architecture<Resource, Link> routing, Set<MappingVariable> mappingVariables);
	
}
