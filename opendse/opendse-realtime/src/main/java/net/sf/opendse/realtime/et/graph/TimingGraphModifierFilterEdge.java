package net.sf.opendse.realtime.et.graph;

import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.Specification;

import org.apache.commons.collections15.Predicate;

public class TimingGraphModifierFilterEdge implements TimingGraphModifier {

	protected Predicate<TimingDependency> removePredicate;

	public TimingGraphModifierFilterEdge(Predicate<TimingDependency> removePredicate) {
		this.removePredicate = removePredicate;
	}

	@Override
	public void apply(Specification implementation, TimingGraph timingGraph) {

		Set<TimingDependency> remove = new HashSet<TimingDependency>();

		for (TimingDependency timingDependency : timingGraph.getEdges()) {
			if (!removePredicate.evaluate(timingDependency)) {
				remove.add(timingDependency);
			}
		}

		for (TimingDependency timingDependency : remove) {
			timingGraph.removeEdge(timingDependency);
		}

	}

}
