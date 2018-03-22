package net.sf.opendse.encoding.routing;

/**
 * The {@link RoutingConstraintGeneratorManager} provides the appropriate
 * {@link RoutingConstraintGenerator} for a communication flow.
 * 
 * @author Fedor Smirnov
 *
 */
public interface RoutingConstraintGeneratorManager {

	/**
	 * get the appropriate {@link RoutingConstraintGenerator} for a given
	 * communication flow
	 * 
	 * @param communicationFlow
	 *            the communication flow to encode
	 * @return the appropriate {@link RoutingConstraintGenerator}
	 */
	public RoutingConstraintGenerator getGenerator(CommunicationFlow communicationFlow);

}
