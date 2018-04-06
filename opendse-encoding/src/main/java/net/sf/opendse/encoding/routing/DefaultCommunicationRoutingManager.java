package net.sf.opendse.encoding.routing;

import java.util.Set;

import net.sf.opendse.encoding.variables.T;

public class DefaultCommunicationRoutingManager implements CommunicationRoutingManager {

	@Override
	public CommunicationRoutingEncoder getRoutingEncoder(T communicationTaskVariable,
			Set<CommunicationFlow> communicationFlows) {
		OneDirectionEncoder oneDirectionEncoder = new DefaultOneDirectionEncoder();
		CycleBreakEncoder cycleBreakEncoder = new NonRedundantCycleBreakEncoder();
		CommunicationHierarchyEncoder hierarchyEncoder = new DefaultCommunicationHierarchyEncoder();
		CommunicationFlowRoutingManager manager = new DefaultCommunicationFlowRoutingManager();
		AdditionalRoutingConstraintsEncoder additionalConstraintsEncoder = new NoAdditionalConstraintsEncoder();
		return new CustomCommunicationRoutingEncoder(oneDirectionEncoder, cycleBreakEncoder, hierarchyEncoder, manager,
				additionalConstraintsEncoder);
	}
}