package net.sf.opendse.encoding.routing;

import java.util.Set;

import com.google.inject.Inject;

import net.sf.opendse.encoding.variables.T;

public class CommunicationRoutingManagerDefault implements CommunicationRoutingManager {

	protected final OneDirectionEncoder oneDirectionEncoder;
	protected final CycleBreakEncoder cycleBreakEncoder;
	protected final CommunicationHierarchyEncoder hierarchyEncoder;
	protected final CommunicationFlowRoutingManager communicationFlowManager;
	protected final AdditionalRoutingConstraintsEncoder additionalConstraintsEncoder;

	@Inject
	public CommunicationRoutingManagerDefault(OneDirectionEncoder oneDirectionEncoder,
			CycleBreakEncoder cycleBreakEncoder, CommunicationHierarchyEncoder hierarchyEncoder,
			CommunicationFlowRoutingManager communicationFlowManager,
			AdditionalRoutingConstraintsEncoder additionalConstraintsEncoder) {
		this.oneDirectionEncoder = oneDirectionEncoder;
		this.cycleBreakEncoder = cycleBreakEncoder;
		this.hierarchyEncoder = hierarchyEncoder;
		this.communicationFlowManager = communicationFlowManager;
		this.additionalConstraintsEncoder = additionalConstraintsEncoder;
	}

	@Override
	public CommunicationRoutingEncoder getRoutingEncoder(T communicationTaskVariable,
			Set<CommunicationFlow> communicationFlows) {
		return new CommunicationRoutingEncoderCustom(oneDirectionEncoder, cycleBreakEncoder, hierarchyEncoder,
				communicationFlowManager, additionalConstraintsEncoder);
	}
}