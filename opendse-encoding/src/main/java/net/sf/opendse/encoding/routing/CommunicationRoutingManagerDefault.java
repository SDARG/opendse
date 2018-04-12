package net.sf.opendse.encoding.routing;

import java.util.Set;

import net.sf.opendse.encoding.variables.T;

public class CommunicationRoutingManagerDefault implements CommunicationRoutingManager {

	@Override
	public CommunicationRoutingEncoder getRoutingEncoder(T communicationTaskVariable,
			Set<CommunicationFlow> communicationFlows) {
		OneDirectionEncoder oneDirectionEncoder = new OneDirectionEncoderDefault();
		CycleBreakEncoder cycleBreakEncoder = new CycleBreakEncoderNonRedundant();
		CommunicationHierarchyEncoder hierarchyEncoder = new CommunicationHierarchyEncoderDefault();
		CommunicationFlowRoutingManager manager = new CommunicationFlowRoutingManagerDefault();
		AdditionalRoutingConstraintsEncoder additionalConstraintsEncoder = new AdditionalRoutingConstraintsEncoderNone();
		return new CommunicationRoutingEncoderCustom(oneDirectionEncoder, cycleBreakEncoder, hierarchyEncoder, manager,
				additionalConstraintsEncoder);
	}
}