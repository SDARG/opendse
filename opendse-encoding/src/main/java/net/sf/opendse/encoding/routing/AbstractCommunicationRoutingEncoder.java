package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

public abstract class AbstractCommunicationRoutingEncoder implements CommunicationRoutingEncoder {

	protected final CycleBreakEncoder cycleBreakEncoder;
	protected final CommunicationHierarchyEncoder hierarchyEncoder;
	protected final CommunicationFlowRoutingManager communicationFlowRoutingManager;
	protected final AdditionalRoutingConstraintsEncoder additionalConstraintsEncoder;

	public AbstractCommunicationRoutingEncoder(CycleBreakEncoder cycleBreakEncoder,
			CommunicationHierarchyEncoder hierarchyEncoder,
			CommunicationFlowRoutingManager communicationFlowRoutingManager,
			AdditionalRoutingConstraintsEncoder additionalConstraintsEncoder) {
		this.cycleBreakEncoder = cycleBreakEncoder;
		this.hierarchyEncoder = hierarchyEncoder;
		this.additionalConstraintsEncoder = additionalConstraintsEncoder;
		this.communicationFlowRoutingManager = communicationFlowRoutingManager;
	}

	@Override
	public Set<Constraint> toConstraints(T communicationVariable, Set<CommunicationFlow> commFlows,
			Architecture<Resource, Link> routing, Set<MappingVariable> mappingVariables) {
		Set<Constraint> routingConstraints = new HashSet<Constraint>();
		// Ensures cycle freedom.
		routingConstraints.addAll(cycleBreakEncoder.toConstraints(communicationVariable, routing));
		// Encodes the variable hierarchy.
		routingConstraints.addAll(hierarchyEncoder.toConstraints(communicationVariable, commFlows, routing));
		// Gets the appropriate Encoder for each communication flow.
		for (CommunicationFlow communicationFlow : commFlows) {
			CommunicationFlowRoutingEncoder commFlowEncoder = communicationFlowRoutingManager.getEncoder(communicationFlow);
			routingConstraints.addAll(commFlowEncoder.toConstraints(communicationFlow, routing, mappingVariables));
		}
		// Encodes additional constraints
		routingConstraints
				.addAll(additionalConstraintsEncoder.toConstraints(communicationVariable, commFlows, routing));
		return routingConstraints;
	}
}