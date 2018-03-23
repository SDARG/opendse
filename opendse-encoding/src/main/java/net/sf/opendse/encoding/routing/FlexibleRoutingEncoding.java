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
 * The {@link FlexibleRoutingEncoding} enables picking different
 * {@link CommunicationRoutingEncoder}s based on the properties of the
 * communication tasks.
 * 
 * @author Fedor Smirnov
 *
 */
public class FlexibleRoutingEncoding implements RoutingEncoding {

	protected final CommunicationRoutingEncoderManager routingEncoderManager;

	/**
	 * Basic constructor
	 * 
	 * @param routingEncoderManager
	 *            the {@link CommunicationRoutingEncoderManager} that provides the
	 *            {@link CommunicationRoutingEncoder}s for encoding the routing
	 *            {@link Constraint}s for the communication tasks
	 */
	@Inject
	public FlexibleRoutingEncoding(CommunicationRoutingEncoderManager routingEncoderManager) {
		this.routingEncoderManager = routingEncoderManager;
	}

	@Override
	public Set<Constraint> toConstraints(Set<ApplicationVariable> applicationVariables,
			Set<MappingVariable> mappingVariables, Routings<Task, Resource, Link> routings) {
		Set<Constraint> routingConstraints = new HashSet<Constraint>();
		Map<T, Set<DTT>> dependencyMap = makeDependencyMap(applicationVariables);
		for (Entry<T, Set<DTT>> entry : dependencyMap.entrySet()) {
			T communicationVariable = entry.getKey();
			Set<DTT> dependencyVariables = entry.getValue();
			CommunicationRoutingEncoder encoder = routingEncoderManager.getRoutingEncoder(communicationVariable,
					dependencyVariables);
			routingConstraints.addAll(encoder.toConstraints(communicationVariable, dependencyVariables,
					routings.get(communicationVariable.getTask()), mappingVariables));
		}
		return routingConstraints;
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
