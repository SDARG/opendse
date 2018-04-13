package net.sf.opendse.encoding.routing;

/**
 * The {@link CommunicationRoutingEncoderCustom} does not offer any default
 * behavior. Instead, its behavior is dictated by the classes used during its
 * construction.
 * 
 * @author Fedor Smirnov
 *
 */
public class CommunicationRoutingEncoderCustom extends CommunicationRoutingEncoderAbstract {

	public CommunicationRoutingEncoderCustom(OneDirectionEncoder oneDirectionEncoder,
			CycleBreakEncoder cycleBreakEncoder, CommunicationHierarchyEncoder hierarchyEncoder,
			CommunicationFlowRoutingManager communicationFlowRoutingManager,
			AdditionalRoutingConstraintsEncoder additionalConstraintsEncoder) {
		super(oneDirectionEncoder, cycleBreakEncoder, hierarchyEncoder, communicationFlowRoutingManager,
				additionalConstraintsEncoder);
	}

}
