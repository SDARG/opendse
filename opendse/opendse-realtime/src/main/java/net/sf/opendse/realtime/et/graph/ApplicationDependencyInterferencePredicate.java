package net.sf.opendse.realtime.et.graph;

public class ApplicationDependencyInterferencePredicate extends ApplicationPriorityCyclesPredicate {

	public ApplicationDependencyInterferencePredicate(TimingGraph timingGraph) {
		super(timingGraph);
	}

	@Override
	public synchronized boolean evaluate(TimingDependency timingDependency) {
		init();

		if (timingDependency instanceof TimingDependencyTrigger) {
			return true;
		} else {
			TimingElement source = timingGraph.getSource(timingDependency);
			TimingElement target = timingGraph.getDest(timingDependency);

			return !predecessors.get(target).contains(source);
		}

	}

}
