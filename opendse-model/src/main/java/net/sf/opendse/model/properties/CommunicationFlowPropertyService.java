package net.sf.opendse.model.properties;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.properties.DependencyPropertyService.RoutingModes;

/**
 * The {@link CommunicationFlowPropertyService} offers convenience methods to
 * access attributes that describe the communication flows. Hereby, a
 * communication flow is the message transmission from one sender to one
 * receiver. By that definition, a multicast communication consists of multiple
 * communication flows.
 * 
 * @author Fedor Smirnov
 *
 */
public class CommunicationFlowPropertyService {

	private CommunicationFlowPropertyService() {
	}

	/**
	 * Returns the {@link RoutingModes} of the communication flow formed by the two
	 * given {@link Dependency}s.
	 * 
	 * @param source
	 *            the {@link Dependency} connecting the source- to the
	 *            communication-task
	 * @param destination
	 *            the {@link Dependency} connection the communication- to the
	 *            destination-task
	 * @return the {@link RoutingModes} that describes how the communication flow
	 *         shall be routed
	 */
	public static RoutingModes getRoutingMode(Dependency source, Dependency destination) {
		RoutingModes srcRoutingMode = DependencyPropertyService.getRoutingMode(source);
		RoutingModes destRoutingMode = DependencyPropertyService.getRoutingMode(destination);
		if (srcRoutingMode.equals(RoutingModes.REDUNDANT) || destRoutingMode.equals(RoutingModes.REDUNDANT)) {
			return RoutingModes.REDUNDANT;
		} else {
			return RoutingModes.DEFAULT;
		}
	}
}