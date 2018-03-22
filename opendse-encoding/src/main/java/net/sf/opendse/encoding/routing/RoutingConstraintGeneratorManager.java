package net.sf.opendse.encoding.routing;

/**
 * The {@link RoutingConstraintGeneratorManager} provides the appropriate
 * {@link RoutingConstraintGenerator} for a {@link CommunicationFlow}.
 * 
 * @author Fedor Smirnov
 *
 */
public interface RoutingConstraintGeneratorManager {

	/**
	 * Returns the appropriate {@link RoutingConstraintGenerator} for a given
	 * communication flow.
	 * 
	 * @param communicationFlow
	 *            the communication flow to encode
	 * @return the appropriate {@link RoutingConstraintGenerator} for the given
	 *         {@link CommunicationFlow}
	 */
	public RoutingConstraintGenerator getGenerator(CommunicationFlow communicationFlow);

}
