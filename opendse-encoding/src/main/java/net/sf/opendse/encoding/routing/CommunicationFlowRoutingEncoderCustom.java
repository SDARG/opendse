package net.sf.opendse.encoding.routing;

/**
 * The {@link CommunicationFlowRoutingEncoderCustom} does not provide any
 * default behavior. Its functionality is dictated by the objects used for its
 * construction.
 * 
 * @author Fedor Smirnov
 *
 */
public class CommunicationFlowRoutingEncoderCustom extends CommunicationFlowRoutingEncoderAbstract {

	public CommunicationFlowRoutingEncoderCustom(ActivationEncoder activationEncoder, EndNodeEncoder endNodeEncoder,
			RoutingResourceEncoder interimNodeEncoder, RoutingEdgeEncoder edgeEncoder) {
		super(activationEncoder, endNodeEncoder, interimNodeEncoder, edgeEncoder);
	}
}
