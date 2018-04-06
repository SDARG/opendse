package net.sf.opendse.encoding.routing;

import java.util.Set;

import com.google.inject.ImplementedBy;

import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Task;

/**
 * The {@link CommunicationRoutingManager} provides
 * {@link CommunicationRoutingEncoder}s based on the properties of a
 * communication {@link Task} and its {@link CommunicationFlow}s.
 * 
 * @author Fedor Smirnov
 *
 */
@ImplementedBy(DefaultCommunicationRoutingManager.class)
public interface CommunicationRoutingManager {

	/**
	 * Returns the {@link CommunicationRoutingEncoder} that is used to encode the
	 * routing constraints for the communication encoded by the given {@link T}
	 * variable and its {@link CommunicationFlow}s.
	 * 
	 * @param communicationTaskVariable
	 *            the {@link T} variable encoding the activation of the
	 *            communication task that is being routed
	 * @param communicationFlows
	 *            the {@link CommunicationFlow}s of the communication that is being
	 *            routed
	 * @return the {@link CommunicationRoutingEncoder} that is used to encode the
	 *         routing constraints for the given communication
	 */
	public CommunicationRoutingEncoder getRoutingEncoder(T communicationTaskVariable,
			Set<CommunicationFlow> communicationFlows);
}
