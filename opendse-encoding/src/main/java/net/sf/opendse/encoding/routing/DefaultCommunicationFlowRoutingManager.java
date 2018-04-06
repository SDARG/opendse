package net.sf.opendse.encoding.routing;

public class DefaultCommunicationFlowRoutingManager implements CommunicationFlowRoutingManager {

	protected final CustomCommunicationFlowRoutingEncoder encoder;

	public DefaultCommunicationFlowRoutingManager() {
		// just returns the default stuff for now
		ActivationEncoder activationEncoder = new DefaultActivationEncoder();
		EndNodeEncoder endNodeEncoder = new MappingEndNodeEncoder();
		RoutingResourceEncoder interimNodeEncoder = new DefaultRoutingResourceEncoder();
		RoutingEdgeEncoder edgeEncoder = new NonRedundantRoutingEdgeEncoder();
		this.encoder = new CustomCommunicationFlowRoutingEncoder(activationEncoder, endNodeEncoder, interimNodeEncoder,
				edgeEncoder);
	}

	@Override
	public CommunicationFlowRoutingEncoder getEncoder(CommunicationFlow communicationFlow) {
		// interim solution
		return this.encoder;
	}
}