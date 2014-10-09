package net.sf.opendse.realtime.et.qcqp;

import java.util.Set;

import net.sf.opendse.model.Specification;
import net.sf.opendse.realtime.et.graph.TimingElement;
import net.sf.opendse.realtime.et.graph.TimingGraph;

public interface MyConflictRefinement {

	public enum ConflictRefinementMethod {
		DELETION, HIERARCHICAL;
	}

	public Set<TimingElement> find(TimingGraph tg, Specification impl);

}
