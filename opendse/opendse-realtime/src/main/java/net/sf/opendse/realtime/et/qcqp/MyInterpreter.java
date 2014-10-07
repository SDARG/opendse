package net.sf.opendse.realtime.et.qcqp;

import static net.sf.opendse.realtime.et.qcqp.vars.Vars.a;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpSolver;
import net.sf.opendse.model.Specification;
import net.sf.opendse.realtime.et.graph.TimingDependency;
import net.sf.opendse.realtime.et.graph.TimingDependencyPriority;
import net.sf.opendse.realtime.et.graph.TimingDependencyTrigger;
import net.sf.opendse.realtime.et.graph.TimingElement;
import net.sf.opendse.realtime.et.graph.TimingGraph;
import net.sf.opendse.realtime.et.qcqp.MyEncoder.Objective;
import net.sf.opendse.realtime.et.qcqp.vars.Vars;
import net.sf.opendse.visualization.algorithm.BellmanFord;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;

public class MyInterpreter {

	protected final SolverProvider solverProvider;
	protected final boolean considerTiming;

	
	
	public MyInterpreter(SolverProvider solverProvider) {
		this(true, solverProvider);
	}

	public MyInterpreter(boolean considerTiming, SolverProvider solverProvider) {
		super();
		this.considerTiming = considerTiming;
		this.solverProvider = solverProvider;
	}
	

	public TimingGraph interprete(TimingGraph tg, Specification implementation, MpResult result) {
		TimingGraph rtg = new TimingGraph();

		System.out.println(result);
		
		
		if (considerTiming){
			final MpResult oResult = result;
			Transformer<TimingDependencyPriority, Boolean> transformer = new Transformer<TimingDependencyPriority, Boolean>() {
				public Boolean transform(TimingDependencyPriority input) {
					return oResult.getBoolean(a(input));
				}
			};
			MyEncoder encoder = new MyEncoder(Objective.DELAY_AND_JITTER_ALL, transformer);
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
				double j = adjust(result.get(Vars.j(timingElement)).doubleValue());
				timingElement.setAttribute("response", r);
				timingElement.setAttribute("delay", d);
				timingElement.setAttribute("jitter[in]", j);
			}
			// System.out.println(timingElement + " " + d + " " + dd + " " + j);
		}
		
		for (TimingDependency timingDependency : tg.getEdges()) {
			if (timingDependency instanceof TimingDependencyTrigger) {
				// rtg.addEdge(timingDependency, tg.getEndpoints(timingDependency));
			} else if (result.getBoolean(a(timingDependency))) {
				rtg.addEdge(timingDependency, tg.getEndpoints(timingDependency));
			}
		}
		
		WeakComponentClusterer<TimingElement, TimingDependency> clusterer = new WeakComponentClusterer<TimingElement, TimingDependency>();
		Set<Set<TimingElement>> clusters = clusterer.transform(rtg);
		
		for(Set<TimingElement> cluster: clusters){
			TimingGraph clusterGraph = new TimingGraph();
			for(TimingElement te: cluster){
				clusterGraph.addVertex(te);
				for(TimingDependency td: rtg.getOutEdges(te)){
					clusterGraph.addEdge(td, te, rtg.getDest(td));
				}
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

			int prio = 1;

			for (TimingElement te : order) {
				te.getTask().setAttribute("prio:"+te.getResource().getId(), prio++);
			}
		}
		
		
		

		

		return rtg;

	}

	public static double adjust(double value) {
		return Math.round(value * 100000.0) / 100000.0;
	}

}
