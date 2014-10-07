package net.sf.opendse.realtime.et.graph;

import static net.sf.opendse.realtime.et.PriorityScheduler.FIXEDPRIORITY_NONPREEMPTIVE;
import static net.sf.opendse.realtime.et.PriorityScheduler.FIXEDPRIORITY_PREEMPTIVE;
import net.sf.opendse.model.Resource;

import org.apache.commons.collections15.Predicate;

public class DelaySchedulerEdgePredicate implements Predicate<TimingDependency> {

	protected TimingGraph timingGraph;

	public DelaySchedulerEdgePredicate(TimingGraph timingGraph) {
		this.timingGraph = timingGraph;

	}

	@Override
	public boolean evaluate(TimingDependency timingDependency) {

		if (timingDependency instanceof TimingDependencyTrigger) {
			return true;
		} else {
			TimingElement source = timingGraph.getSource(timingDependency);

			Resource resource = source.getResource();

			String scheduler = resource.getAttribute("scheduler");

			return scheduler.equals(FIXEDPRIORITY_NONPREEMPTIVE) || scheduler.equals(FIXEDPRIORITY_PREEMPTIVE);
		}

	}

}
