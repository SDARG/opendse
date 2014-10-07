package net.sf.opendse.realtime.et.graph;

import net.sf.opendse.model.Specification;

public interface TimingGraphModifier {

	public void apply(Specification implementation, TimingGraph timingGraph);

}
