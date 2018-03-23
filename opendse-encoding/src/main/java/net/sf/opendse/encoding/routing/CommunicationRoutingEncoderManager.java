package net.sf.opendse.encoding.routing;

import java.util.Set;

import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;

/**
 * The {@link CommunicationRoutingEncoderManager} provides
 * {@link CommunicationRoutingEncoder}s based on the properties of a
 * communication {@link Task} and its {@link Dependency}s.
 * 
 * @author Fedor Smirnov
 *
 */
public interface CommunicationRoutingEncoderManager {

	/**
	 * Returns the {@link CommunicationRoutingEncoder} that is used to encode the
	 * routing constraints for the communication encoded by the given {@link T}
	 * variable which fulfills the {@link Dependency}s encoded by the given
	 * {@link DTT} variables.
	 * 
	 * @param communicationTaskVariable
	 *            the {@link T} variable encoding the activation of the
	 *            communication task that is being routed
	 * @param dependencyVariables
	 *            the {@link DTT} variables that encode the activation of the
	 *            dependencies of the communication task that is being routed
	 * @return the {@link CommunicationRoutingEncoder} that is used to encode the
	 *         routing constraints for the given communication
	 */
	public CommunicationRoutingEncoder getRoutingEncoder(T communicationTaskVariable, Set<DTT> dependencyVariables);

}
