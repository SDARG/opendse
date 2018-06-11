package net.sf.opendse.encoding.routing;

import com.google.inject.Inject;

public class CommunicationFlowRoutingManagerDefault implements CommunicationFlowRoutingManager {

	protected final CommunicationFlowRoutingEncoderCustom encoder;

	@Inject
	public CommunicationFlowRoutingManagerDefault(ActivationEncoder activationEncoder, EndNodeEncoder endNodeEncoder,
			RoutingResourceEncoder routingResourceEncoder, RoutingEdgeEncoder routingEdgeEncoder,
			ProxyEncoder proxyEncoder) {
		// just returns the default stuff for now
		this.encoder = new CommunicationFlowRoutingEncoderCustom(activationEncoder, endNodeEncoder,
				routingResourceEncoder, routingEdgeEncoder, proxyEncoder);
	}

	@Override
	public CommunicationFlowRoutingEncoder getEncoder(CommunicationFlow communicationFlow) {
		// interim solution
		return this.encoder;
	}
}