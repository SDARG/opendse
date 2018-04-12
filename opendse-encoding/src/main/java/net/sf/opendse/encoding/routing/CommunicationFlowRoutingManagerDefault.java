package net.sf.opendse.encoding.routing;

public class CommunicationFlowRoutingManagerDefault implements CommunicationFlowRoutingManager {

	protected final CommunicationFlowRoutingEncoderCustom encoder;

	public CommunicationFlowRoutingManagerDefault() {
		// just returns the default stuff for now
		ActivationEncoder activationEncoder = new ActivationEncoderDefault();
		EndNodeEncoder endNodeEncoder = new EndNodeEncoderMapping();
		RoutingResourceEncoder interimNodeEncoder = new RoutingResourceEncoderDefault();
		RoutingEdgeEncoder edgeEncoder = new RoutingEdgeEncoderNonRedundant();
		this.encoder = new CommunicationFlowRoutingEncoderCustom(activationEncoder, endNodeEncoder, interimNodeEncoder,
				edgeEncoder);
	}

	@Override
	public CommunicationFlowRoutingEncoder getEncoder(CommunicationFlow communicationFlow) {
		// interim solution
		return this.encoder;
	}
}