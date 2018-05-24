package net.sf.opendse.encoding.routing;

import com.google.inject.Inject;

public class CommunicationFlowRoutingManagerDefault implements CommunicationFlowRoutingManager {

	protected final CommunicationFlowRoutingEncoderCustom encoder;

	@Inject
	public CommunicationFlowRoutingManagerDefault() {
		// just returns the default stuff for now
		ActivationEncoder activationEncoder = new ActivationEncoderDefault();
		EndNodeEncoder endNodeEncoder = new EndNodeEncoderMapping();
		RoutingResourceEncoder interimNodeEncoder = new RoutingResourceEncoderDefault();
		RoutingEdgeEncoder edgeEncoder = new RoutingEdgeEncoderNonRedundant();
		ProxyEncoder proxyEncoder = new ProxyEncoder();
		this.encoder = new CommunicationFlowRoutingEncoderCustom(activationEncoder, endNodeEncoder, interimNodeEncoder,
				edgeEncoder, proxyEncoder);
	}

	@Override
	public CommunicationFlowRoutingEncoder getEncoder(CommunicationFlow communicationFlow) {
		// interim solution
		return this.encoder;
	}
}