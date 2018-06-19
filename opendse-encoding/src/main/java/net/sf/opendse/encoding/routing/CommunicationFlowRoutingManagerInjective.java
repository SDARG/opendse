package net.sf.opendse.encoding.routing;

import com.google.inject.Inject;

public class CommunicationFlowRoutingManagerInjective implements CommunicationFlowRoutingManager {

	protected final CommunicationFlowRoutingEncoder encoder;

	@Inject
	public CommunicationFlowRoutingManagerInjective(ActivationEncoder activationEncoder, EndNodeEncoder endNodeEncoder,
			RoutingResourceEncoder interimNodeEncoder, RoutingEdgeEncoder edgeEncoder, ProxyEncoder proxyEncoder) {
		this.encoder = new CommunicationFlowRoutingEncoderCustom(activationEncoder, endNodeEncoder, interimNodeEncoder,
				edgeEncoder, proxyEncoder);
	}

	@Override
	public CommunicationFlowRoutingEncoder getEncoder(CommunicationFlow communicationFlow) {
		return this.encoder;
	}

}
