package net.sf.opendse.realtime.et.graph;

import net.sf.opendse.model.Task;

import org.apache.commons.collections15.Predicate;

public class RateMonotonicEdgeFilterPredicate implements Predicate<TimingDependency> {

	protected TimingGraph timingGraph;

	public RateMonotonicEdgeFilterPredicate(TimingGraph timingGraph) {
		this.timingGraph = timingGraph;

	}

	@Override
	public boolean evaluate(TimingDependency timingDependency) {
		
		if (timingDependency instanceof TimingDependencyTrigger) {
			return true;
		} else {
			TimingElement source = timingGraph.getSource(timingDependency);
			TimingElement target = timingGraph.getDest(timingDependency);
			
			Task t0 = source.getTask();
			Task t1 = target.getTask();
			
			Double h0 = t0.getAttribute("h");
			Double h1 = t1.getAttribute("h");

			return h0 <= h1;
		}
		
	}


}
