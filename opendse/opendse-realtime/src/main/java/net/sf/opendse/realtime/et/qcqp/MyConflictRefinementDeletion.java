package net.sf.opendse.realtime.et.qcqp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpSolver;
import net.sf.opendse.model.Specification;
import net.sf.opendse.realtime.et.graph.TimingDependency;
import net.sf.opendse.realtime.et.graph.TimingDependencyPriority;
import net.sf.opendse.realtime.et.graph.TimingElement;
import net.sf.opendse.realtime.et.graph.TimingGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class MyConflictRefinementDeletion implements MyConflictRefinement {
	
	protected final SolverProvider solverProvider;

	public MyConflictRefinementDeletion(SolverProvider solverProvider) {
		super();
		this.solverProvider = solverProvider;
	}

	public Set<TimingElement> find(TimingGraph tg, Specification impl) {
		Set<TimingElement> iis = new HashSet<TimingElement>();

		for (TimingElement te : tg.getVertices()) {
			iis.add(te);
		}

		return find(tg, impl, iis);
	}

	public Set<TimingElement> find(TimingGraph tg, Specification impl, Set<TimingElement> predef) {

		Set<TimingElement> iis = new HashSet<TimingElement>(predef);

		List<TimingElement> teList = new ArrayList<TimingElement>(predef);

		Map<TimingDependencyPriority, Pair<TimingElement>> removed = new HashMap<TimingDependencyPriority, Pair<TimingElement>>();
		Double lastE = 0.0;

		for (TimingElement te : teList) {
			System.out.print("conflict refinement " + te);
			lastE = te.getAttribute("e");
			te.setAttribute("e", 0.0);

			for (TimingDependency td : tg.getIncidentEdges(te)) {
				if (td instanceof TimingDependencyPriority) {
					removed.put((TimingDependencyPriority) td,
							new Pair<TimingElement>(tg.getSource(td), tg.getDest(td)));
				}
			}
			for (TimingDependency td : removed.keySet()) {
				tg.removeEdge(td);
			}

			MyEncoder encoder = new MyEncoder();
			MpProblem problem = encoder.encode(tg);
			// System.out.println(problem);

			MpSolver solver = solverProvider.get();
			solver.add(problem);
			
			MpResult result = solver.solve();

			System.out.println(" " + ((result == null ? "infeasible" : "feasible")));

			// System.out.println("without "+te+" : "+result);

			if (result != null) {
				// this is part of the IIS, reset timing graph
				te.setAttribute("e", lastE);

				for (TimingDependencyPriority td : removed.keySet()) {
					Pair<TimingElement> endpoints = removed.get(td);
					tg.addEdge(td, endpoints);
				}

			} else {
				iis.remove(te);

			}
			removed.clear();
		}

		return iis;

	}

}
