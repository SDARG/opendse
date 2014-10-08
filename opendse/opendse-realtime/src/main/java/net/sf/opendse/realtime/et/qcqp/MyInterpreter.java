package net.sf.opendse.realtime.et.qcqp;

import static net.sf.opendse.model.Models.isCommunication;
import static net.sf.opendse.model.Models.isProcess;
import static net.sf.opendse.realtime.et.PriorityScheduler.FIXEDPRIORITY_NONPREEMPTIVE;
import static net.sf.opendse.realtime.et.PriorityScheduler.FIXEDPRIORITY_PREEMPTIVE;
import static net.sf.opendse.realtime.et.PriorityScheduler.PRIORITY;
import static net.sf.opendse.realtime.et.PriorityScheduler.SCHEDULER;
import static net.sf.opendse.realtime.et.qcqp.vars.Vars.a;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpSolver;
import net.sf.opendse.model.Node;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.realtime.et.SolverProvider;
import net.sf.opendse.realtime.et.TimingGraphViewer;
import net.sf.opendse.realtime.et.graph.TimingDependency;
import net.sf.opendse.realtime.et.graph.TimingDependencyPriority;
import net.sf.opendse.realtime.et.graph.TimingDependencyTrigger;
import net.sf.opendse.realtime.et.graph.TimingElement;
import net.sf.opendse.realtime.et.graph.TimingGraph;
import net.sf.opendse.realtime.et.qcqp.MyEncoder.OptimizationObjective;
import net.sf.opendse.realtime.et.qcqp.vars.Vars;
import net.sf.opendse.visualization.algorithm.BellmanFord;
import net.sf.opendse.visualization.algorithm.CycleBreakFilter;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;

public class MyInterpreter {

	protected final SolverProvider solverProvider;
	protected final boolean considerTiming;
	protected final boolean considerPriorities;

	public MyInterpreter(SolverProvider solverProvider) {
		this(true, solverProvider);
	}

	public MyInterpreter(boolean considerTiming, SolverProvider solverProvider) {
		this(considerTiming, true, solverProvider);
	}

	public MyInterpreter(boolean considerTiming, boolean considerPriorities, SolverProvider solverProvider) {
		super();
		this.considerTiming = considerTiming;
		this.solverProvider = solverProvider;
		this.considerPriorities = considerPriorities;
	}

	public TimingGraph interprete(TimingGraph tg, Specification implementation, MpResult result) {
		TimingGraph rtg = new TimingGraph();

		final MpResult oResult = result;
		Transformer<TimingDependencyPriority, Boolean> edgeValueTransformer = new Transformer<TimingDependencyPriority, Boolean>() {
			public Boolean transform(TimingDependencyPriority input) {
				Boolean value = oResult.getBoolean(a(input));
				return value;
			}
		};

		if (considerTiming) {
			MyEncoder encoder = new MyEncoder(OptimizationObjective.DELAY_AND_JITTER_ALL, edgeValueTransformer, false);
			MpProblem problem = encoder.encode(tg);

			MpSolver solver = solverProvider.get();
			solver.add(problem);

			result = solver.solve();
			System.out.println(result);
		}

		for (TimingElement timingElement : tg.getVertices()) {
			rtg.addVertex(timingElement);

			if (considerTiming) {
				double r = adjust(result.get(Vars.r(timingElement)).doubleValue());
				double d = adjust(result.get(Vars.d(timingElement)).doubleValue());
				double jIn = adjust(result.get(Vars.jIn(timingElement)).doubleValue());
				double jOut = adjust(result.get(Vars.jOut(timingElement)).doubleValue());
				timingElement.setAttribute("response", r);
				timingElement.setAttribute("delay", d);
				timingElement.setAttribute("jitter[in]", jIn);
				timingElement.setAttribute("jitter[out]", jOut);
			}
			// System.out.println(timingElement + " " + d + " " + dd + " " + j);
		}

		for (TimingDependency timingDependency : tg.getEdges()) {
			if (timingDependency instanceof TimingDependencyPriority && result.getBoolean(a(timingDependency))) {
				rtg.addEdge(timingDependency, tg.getEndpoints(timingDependency));
			}
		}

		if (considerPriorities) {
			WeakComponentClusterer<TimingElement, TimingDependency> clusterer = new WeakComponentClusterer<TimingElement, TimingDependency>();
			Set<Set<TimingElement>> clusters = clusterer.transform(rtg);

			for (Set<TimingElement> cluster : clusters) {
				TimingGraph clusterGraph = new TimingGraph();
				for (TimingElement te : cluster) {
					clusterGraph.addVertex(te);
					for (TimingDependency td : rtg.getOutEdges(te)) {
						clusterGraph.addEdge(td, te, rtg.getDest(td));
					}
				}

				CycleBreakFilter<TimingElement, TimingDependency> cycleBreak = new CycleBreakFilter<TimingElement, TimingDependency>();
				Set<TimingDependency> edges = cycleBreak.transform(clusterGraph);
				if (!edges.isEmpty()) {
					throw new RuntimeException("Found cycle, cannot assign priorities");
				}

				BellmanFord<TimingElement, TimingDependency> bellmanFord = new BellmanFord<TimingElement, TimingDependency>();
				final Transformer<TimingElement, Double> transformer = bellmanFord.transform(clusterGraph);

				List<TimingElement> order = new ArrayList<TimingElement>(clusterGraph.getVertices());
				Collections.sort(order, new Comparator<TimingElement>() {
					@Override
					public int compare(TimingElement o1, TimingElement o2) {
						return transformer.transform(o1).compareTo(transformer.transform(o2));
					}
				});

				Map<Resource, Integer> priorities = new HashMap<Resource, Integer>();

				for (TimingElement te : order) {
					Resource resource = te.getResource();
					String scheduler = resource.getAttribute(SCHEDULER);
					if (FIXEDPRIORITY_NONPREEMPTIVE.equals(scheduler) || FIXEDPRIORITY_PREEMPTIVE.equals(scheduler)) {
						int prio;
						if (priorities.containsKey(resource)) {
							prio = (priorities.get(resource)) + 1;
						} else {
							prio = 1;
						}
						priorities.put(resource, prio);

						Task task = te.getTask();

						Node node = null;
						if (isProcess(task)) {
							node = task;
						} else if (isCommunication(task)) {
							te.getTask().setAttribute(PRIORITY + ":" + te.getResource().getId(), prio);
							node = implementation.getRoutings().get(task).getVertex(te.getResource());
						}
						node.setAttribute(PRIORITY, prio);
					}
				}
			}
		}

		return rtg;

	}

	public static double adjust(double value) {
		return Math.round(value * 100000.0) / 100000.0;
	}

}
