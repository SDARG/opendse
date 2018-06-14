package net.sf.opendse.encoding.routing;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link ActivationEncoder} formulates {@link Constraint}s that make sure
 * that all {@link RoutingVariable} of a {@link CommunicationFlow} are
 * deactivated if the {@link CommunicationFlow} is deactivated.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(ActivationEncoderDefault.class)
public interface ActivationEncoder {

	/**
	 * Formulates {@link Constraint}s that make sure that all
	 * {@link RoutingVariable} of a {@link CommunicationFlow} are deactivated if the
	 * {@link CommunicationFlow} is deactivated.
	 * 
	 * @param communicationFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param routing
	 *            the {@link Architecture} representing the routing possibilities
	 *            for the {@link Communication} that is being routed
	 * @return a set of {@link Constraint}s that make sure that all
	 *         {@link RoutingVariable} of a {@link CommunicationFlow} are
	 *         deactivated if the {@link CommunicationFlow} is deactivated
	 */
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing);

}
