package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

public class AbstractCommunicationFlowRoutingEncoder implements CommunicationFlowRoutingEncoder {

	protected final ActivationEncoder activationEncoder;
	protected final EndNodeEncoder endNodeEncoder;
	protected final RoutingResourceEncoder interimNodeEncoder;
	protected final RoutingEdgeEncoder edgeEncoder;

	public AbstractCommunicationFlowRoutingEncoder(ActivationEncoder activationEncoder, EndNodeEncoder endNodeEncoder,
			RoutingResourceEncoder interimNodeEncoder, RoutingEdgeEncoder edgeEncoder) {
		this.activationEncoder = activationEncoder;
		this.endNodeEncoder = endNodeEncoder;
		this.interimNodeEncoder = interimNodeEncoder;
		this.edgeEncoder = edgeEncoder;
	}

	@Override
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing, Set<MappingVariable> mappingVariables) {
		Set<Constraint> communicationFlowRoutingConstraints = new HashSet<Constraint>();
		communicationFlowRoutingConstraints.addAll(activationEncoder.toConstraints(communicationFlow, routing));
		communicationFlowRoutingConstraints.addAll(endNodeEncoder.toConstraints(communicationFlow, routing, mappingVariables));
		communicationFlowRoutingConstraints.addAll(interimNodeEncoder.toConstraints(communicationFlow, routing));
		communicationFlowRoutingConstraints.addAll(edgeEncoder.toConstraints(communicationFlow, routing));
		return communicationFlowRoutingConstraints;
	}
}