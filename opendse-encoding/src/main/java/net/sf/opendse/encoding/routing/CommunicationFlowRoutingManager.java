package net.sf.opendse.encoding.routing;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.ImplementedBy;

/**
 * The {@link CommunicationFlowRoutingManager} provides the appropriate
 * {@link CommunicationFlowRoutingEncoder} for the given
 * {@link CommunicationFlow}.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(CommunicationFlowRoutingManagerDefault.class)
public interface CommunicationFlowRoutingManager {

	/**
	 * Provides the appropriate {@link CommunicationFlowRoutingEncoder} for the
	 * given {@link CommunicationFlow}.
	 * 
	 * @param communicationFlow
	 *            the {@link CommunicationFlow} that shall be routed
	 * @return the {@link CommunicationFlowRoutingEncoder} that is to be used for
	 *         the encoding of the routing {@link Constraint}s of the given
	 *         {@link CommunicationFlow}
	 */
	public CommunicationFlowRoutingEncoder getEncoder(CommunicationFlow communicationFlow);

}
