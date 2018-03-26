package net.sf.opendse.encoding.routing;

/**
 * The {@link CustomCommunicationRoutingEncoder} does not offer any default
 * behavior. Instead, its behavior is dictated by the classes used during its
 * construction.
 * 
 * @author Fedor Smirnov
 *
 */
public class CustomCommunicationRoutingEncoder extends AbstractCommunicationRoutingEncoder {

	public CustomCommunicationRoutingEncoder(CycleBreakEncoder cycleBreakEncoder,
			CommunicationHierarchyEncoder hierarchyEncoder,
			CommunicationFlowRoutingManager communicationFlowRoutingManager,
			AdditionalRoutingConstraintsEncoder additionalConstraintsEncoder) {
		super(cycleBreakEncoder, hierarchyEncoder, communicationFlowRoutingManager, additionalConstraintsEncoder);
	}

}
