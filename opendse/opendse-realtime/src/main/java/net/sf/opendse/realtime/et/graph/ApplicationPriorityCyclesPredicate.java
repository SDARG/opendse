package net.sf.opendse.realtime.et.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.opendse.visualization.algorithm.BellmanFord;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

public class ApplicationPriorityCyclesPredicate implements Predicate<TimingDependency> {

	protected TimingGraph timingGraph;
	protected Map<TimingElement, Set<TimingElement>> predecessors = new HashMap<TimingElement, Set<TimingElement>>();
	protected boolean isInit = false;

	public ApplicationPriorityCyclesPredicate(TimingGraph timingGraph) {
		this.timingGraph = timingGraph;

	}

	protected synchronized void init() {

		if (!isInit) {

			TimingGraph copy = new TimingGraph();
			for (TimingElement timingElement : timingGraph.getVertices()) {
				copy.addVertex(timingElement);
				predecessors.put(timingElement, new HashSet<TimingElement>());
			}
			for (TimingDependency timingDependency : timingGraph.getEdges()) {
				if (timingDependency instanceof TimingDependencyTrigger) {
					copy.addEdge(timingDependency, timingGraph.getEndpoints(timingDependency));
				}
			}

			BellmanFord<TimingElement, TimingDependency> bellmanFord = new BellmanFord<TimingElement, TimingDependency>();

			final Transformer<TimingElement, Double> order = bellmanFord.transform(copy);

			List<TimingElement> elements = new ArrayList<TimingElement>(copy.getVertices());
			Collections.sort(elements, new Comparator<TimingElement>() {
				@Override
				public int compare(TimingElement o1, TimingElement o2) {
					return order.transform(o1).compareTo(order.transform(o2));
				}
			});

			for (TimingElement timingElement : elements) {
				Set<TimingElement> preds = predecessors.get(timingElement);
				for (TimingElement predTimingElement : copy.getPredecessors(timingElement)) {
					preds.add(predTimingElement);
					preds.addAll(predecessors.get(predTimingElement));
				}
			}
			isInit = true;
		}
	}

	@Override
	public synchronized boolean evaluate(TimingDependency timingDependency) {
		init();
		
		if (timingDependency instanceof TimingDependencyTrigger) {
			return true;
		} else {
			TimingElement source = timingGraph.getSource(timingDependency);
			TimingElement target = timingGraph.getDest(timingDependency);

			return !predecessors.get(source).contains(target);
		}
		
		/*
		 TimingEncoder encoder = new TimingEncoder();
		Problem problem = encoder.encode(tg, impl);

		{

			Set<TimingDependencyPriority> timingDependencyPriorities = new HashSet<TimingDependencyPriority>();

			for (Variable variable : problem.getVariables()) {
				if (variable.getVar() instanceof TimingDependencyPriority) {
					timingDependencyPriorities.add((TimingDependencyPriority) variable.getVar());
				}
			}

			Solver solver = new SolverGurobi();
			solver.add(problem);

			Result result = null;

			System.out.println(timingDependencyPriorities.size());
			while ((result = solver.solve()) != null) {

				for (Iterator<TimingDependencyPriority> it = timingDependencyPriorities.iterator(); it.hasNext();) {
					if (result.getBoolean(it.next())) {
						it.remove();
					}
				}

				System.out.println(timingDependencyPriorities.size());

				Expression linear = new Expression();
				for (TimingDependencyPriority tdp : timingDependencyPriorities) {
					linear.add(tdp);
				}
				solver.add(linear, ">=", 1);

			}

			System.out.println(timingDependencyPriorities.size());

			for (TimingDependencyPriority tdp : timingDependencyPriorities) {
				System.out.println(tg.getEndpoints(tdp));
			}

		}
		
		 */
	}

}
