package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link ProxyEncoder} generates the constraints that describe the
 * activation conditions of the elements inside the proxy areas.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(ProxyEncoderCompact.class)
public interface ProxyEncoder {

	/**
	 * Generates the constraints that describe the activation conditions of the
	 * elements inside the proxy areas
	 * 
	 * @param flow
	 *            the {@link CommunicationFlow} that is being encoded
	 * @param routing
	 *            the {@link Architecture} representing the routing possibilities
	 * @param mappingVariables
	 *            the set of the variables encoding the mapping of the tasks
	 * @return the constraints that describe the activation conditions of the
	 *         elements inside the proxy areas
	 */
	public Set<Constraint> toConstraints(CommunicationFlow flow, Architecture<Resource, Link> routing,
			Set<MappingVariable> mappingVariables);

}
