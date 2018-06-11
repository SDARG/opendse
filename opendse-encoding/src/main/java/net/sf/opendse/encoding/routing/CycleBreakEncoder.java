package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link CycleBreakEncoder} formulates constraints that prevent routing
 * cycles.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(CycleBreakEncoderColor.class)
public interface CycleBreakEncoder {

	/**
	 * Formulates constraint that prevent cycles in the routing of the current message.
	 * 
	 * @param communicationVariable the {@link T} variable encoding the activation of the message task that is to be routed
	 * @param routing the {@link Architecture} describing the potential routing resources for the message that is to be routed
	 * @return the set of {@link Constraint}s that prevent cycles in the routing
	 */
	public Set<Constraint> toConstraints(T communicationVariable, Architecture<Resource, Link> routing);
	
}
