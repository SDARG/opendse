package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.properties.TaskPropertyService;

public abstract class AbstractCommunicationRoutingEncoder implements CommunicationRoutingEncoder {

	protected final CycleBreakEncoder cycleBreakEncoder;
	protected final CommunicationHierarchyEncoder hierarchyEncoder;

	protected final EndNodeEncoder endNodeEncoder;
	protected final RoutingResourceEncoder interimNodeEncoder;
	protected final RoutingEdgeEncoder edgeEncoder;

	public AbstractCommunicationRoutingEncoder(CycleBreakEncoder cycleBreakEncoder,
			CommunicationHierarchyEncoder hierarchyEncoder, EndNodeEncoder endNodeEncoder,
			RoutingResourceEncoder interimNodeEncoder, RoutingEdgeEncoder edgeEncoder) {
		this.cycleBreakEncoder = cycleBreakEncoder;
		this.hierarchyEncoder = hierarchyEncoder;
		this.endNodeEncoder = endNodeEncoder;
		this.interimNodeEncoder = interimNodeEncoder;
		this.edgeEncoder = edgeEncoder;
	}

	@Override
	public Set<Constraint> toConstraints(T communicationVariable, Set<DTT> dependencyVariables,
			Architecture<Resource, Link> routing, Set<MappingVariable> mappingVariables) {
		Set<Constraint> routingConstraints = new HashSet<Constraint>();
		// Ensures cycle freedom.
		routingConstraints.addAll(cycleBreakEncoder.toConstraints(communicationVariable, routing));
		Set<CommunicationFlow> commFlows = findCommunicationFlows(dependencyVariables);
		// Encodes the variable hierarchy.
		routingConstraints.addAll(hierarchyEncoder.toConstraints(communicationVariable, commFlows, routing));
		// Encodes the routing of each communication flow
		for (CommunicationFlow communicationFlow : commFlows) {
			// Encodes the end nodes of the routing graph.
			routingConstraints.addAll(endNodeEncoder.toConstraints(communicationFlow, routing, mappingVariables));
			// Encode the interim nodes of the routing graph.
			routingConstraints.addAll(interimNodeEncoder.toConstraints(communicationFlow, routing));
			// Encodes the edges of the routing graph.
			routingConstraints.addAll(edgeEncoder.toConstraints(communicationFlow, routing));
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
}