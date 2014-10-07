package net.sf.opendse.realtime.et.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class TimingGraph extends DirectedSparseMultigraph<TimingElement, TimingDependency> implements Iterable<TimingElement> {

	private static final long serialVersionUID = 1L;

	public Set<TimingDependencyPriority> getPriorityEdges() {
		Set<TimingDependencyPriority> edges = new HashSet<TimingDependencyPriority>();
		for (TimingDependency td : this.getEdges()) {
			if (td instanceof TimingDependencyPriority) {
				edges.add((TimingDependencyPriority) td);
			}
		}
		return edges;
	}

	public TimingElement findNode(TimingElement timingElement) {
		for (TimingElement te : this.getVertices()) {
			if (te.equals(timingElement)) {
				return te;
			}
		}
		return null;
	}

	@Override
	public Iterator<TimingElement> iterator() {
		return this.getVertices().iterator();
	}

	public int getPriorityEdgeCount() {
		return getPriorityEdges().size();
	}

}
