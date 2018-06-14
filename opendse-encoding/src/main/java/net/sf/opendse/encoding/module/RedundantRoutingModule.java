package net.sf.opendse.encoding.module;

import net.sf.opendse.encoding.routing.CycleBreakEncoder;
import net.sf.opendse.encoding.routing.CycleBreakEncoderOrder;
import net.sf.opendse.encoding.routing.RoutingEdgeEncoder;
import net.sf.opendse.encoding.routing.RoutingEdgeEncoderRedundant;
import net.sf.opendse.optimization.DesignSpaceExplorationModule;

/**
 * The {@link RedundantRoutingModule} binds the classes for the redundant
 * encoding of message routings.
 * 
 * @author Fedor Smirnov
 *
 */
public class RedundantRoutingModule extends DesignSpaceExplorationModule {

	@Override
	protected void config() {
		bind(CycleBreakEncoder.class).to(CycleBreakEncoderOrder.class);
		bind(RoutingEdgeEncoder.class).to(RoutingEdgeEncoderRedundant.class);
	}
}
