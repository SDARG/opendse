package net.sf.opendse.encoding.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.Inject;

import net.sf.opendse.encoding.ApplicationEncoding;
import net.sf.opendse.encoding.RoutingEncoding;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.TaskPropertyService;

/**
 * The {@link RoutingEncodingFlexible} enables picking different
 * {@link CommunicationRoutingEncoder}s based on the properties of the
 * communication tasks.
 * 
 * @author Fedor Smirnov
 *
 */
public class RoutingEncodingFlexible implements RoutingEncoding {

	protected final CommunicationRoutingManager routingEncoderManager;

	/**
	 * Basic constructor
	 * 
	 * @param routingEncoderManager
	 *            the {@link CommunicationRoutingManager} that provides the
	 *            {@link CommunicationRoutingEncoder}s for encoding the routing
	 *            {@link Constraint}s for the communication tasks
	 */
	@Inject
	public RoutingEncodingFlexible(CommunicationRoutingManager routingEncoderManager) {
		this.routingEncoderManager = routingEncoderManager;
	}

	@Override
	public Set<Constraint> toConstraints(Set<ApplicationVariable> applicationVariables,
			Set<MappingVariable> mappingVariables, Routings<Task, Resource, Link> routings) {
		Set<Constraint> routingConstraints = new HashSet<Constraint>();
		Map<T, Set<DTT>> dependencyMap = makeDependencyMap(applicationVariables);
		for (Entry<T, Set<DTT>> entry : dependencyMap.entrySet()) {
			// Gathers the dependencies to communication flows and formulates the routing
			// constraints for the current message.
			T communicationVariable = entry.getKey();
			Set<DTT> dependencyVariables = entry.getValue();
			Set<CommunicationFlow> communicationFlows = findCommunicationFlows(dependencyVariables);
			CommunicationRoutingEncoder encoder = routingEncoderManager.getRoutingEncoder(communicationVariable,
					communicationFlows);
			routingConstraints.addAll(encoder.toConstraints(communicationVariable, communicationFlows,
					routings.get(communicationVariable.getTask()), mappingVariables));
		}
		return routingConstraints;
	}

	/**
	 * Takes the set of the {@link DTT} variables encoding the incident
	 * {@link Dependency}s of the communication that is being routed and sorts them
	 * into a set of {@link CommunicationFlow}s.
	 * 
	 * @param dependendencyVariables
	 *            the set of the {@link DTT} variables encoding the incident
	 *            {@link Dependency}s of the communication that is being routed
	 * @return the set of {@link CommunicationFlow}s of the communication that is
	 *         being routed
	 */
	protected Set<CommunicationFlow> findCommunicationFlows(Set<DTT> dependendencyVariables) {
		Set<DTT> sourceDependencies = new HashSet<DTT>();
		Set<DTT> destDependencies = new HashSet<DTT>();
		for (DTT dependencyVar : dependendencyVariables) {
			Set<DTT> properSet = TaskPropertyService.isCommunication(dependencyVar.getSourceTask()) ? destDependencies
					: sourceDependencies;
			properSet.add(dependencyVar);
		}
		Set<CommunicationFlow> result = new HashSet<CommunicationFlow>();
		for (DTT sourceDependency : sourceDependencies) {
			for (DTT destDependency : destDependencies) {
				result.add(new CommunicationFlow(sourceDependency, destDependency));
			}
		}
		return result;
	}

	/**
	 * Fills the map mapping the communication tasks onto their
	 * dependency-variables.
	 * 
	 * @param applicationVariables
	 *            all {@link ApplicationVariable}s encoded by the
	 *            {@link ApplicationEncoding}
	 * @return a map mapping the {@link T} variables of the communication tasks onto
	 *         the {@link DTT} variables of their {@link Dependency}s (expected to
	 *         be empty)
	 */
	protected Map<T, Set<DTT>> makeDependencyMap(Set<ApplicationVariable> applicationVariables) {
		Map<T, Set<DTT>> dependencyMap = new HashMap<T, Set<DTT>>();
		for (ApplicationVariable applVar : applicationVariables) {
			if (applVar instanceof T) {
				T tVar = (T) applVar;
				if (TaskPropertyService.isCommunication(tVar.getTask()) && !dependencyMap.containsKey(tVar)) {
					dependencyMap.put(tVar, new HashSet<DTT>());
				}
			} else if (applVar instanceof DTT) {
				DTT dttVar = (DTT) applVar;
				Task comm = TaskPropertyService.isCommunication(dttVar.getSourceTask()) ? dttVar.getSourceTask()
						: dttVar.getDestinationTask();
				T tVar = Variables.varT(comm);
				if (!dependencyMap.containsKey(tVar)) {
					dependencyMap.put(tVar, new HashSet<DTT>());
				}
				dependencyMap.get(tVar).add(dttVar);
			} else {
				throw new IllegalArgumentException(
						"Currently, only T and DTT variables are supported as application variables.");
			}
		}
		return dependencyMap;
	}
}
