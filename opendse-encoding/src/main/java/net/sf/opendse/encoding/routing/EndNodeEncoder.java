package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.MappingEncoding;
import net.sf.opendse.encoding.variables.DDdR;
import net.sf.opendse.encoding.variables.DDsR;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link EndNodeEncoder} formulates the constraints that encode the source
 * and the destination nodes of the routed communication flows by encoding the
 * {@link DDsR} and the {@link DDdR} variables.
 * 
 * @author Fedor Smirnov
 *
 */
public interface EndNodeEncoder {

	/**
	 * Formulates the constraints encoding the {@link DDsR} and the {@link DDdR}
	 * variables.
	 * 
	 * @param communicationFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param routing
	 *            the {@link Architecture} representing the routing possibilities of
	 *            the communication
	 * @param mappingVariables
	 *            the set of {@link MappingVariable}s encoded by the
	 *            {@link MappingEncoding}
	 * @return the set of constraints encoding the {@link DDsR} and the {@link DDdR}
	 *         variables.
	 */
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing,
			Set<MappingVariable> mappingVariables);
}
