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
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Function;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.realtime.et.SolverProvider;
import net.sf.opendse.realtime.et.graph.TimingDependency;
import net.sf.opendse.realtime.et.graph.TimingDependencyPriority;
import net.sf.opendse.realtime.et.graph.TimingElement;
import net.sf.opendse.realtime.et.graph.TimingGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class MyConflictRefinementHierarchical implements MyConflictRefinement {

	protected final SolverProvider solverProvider;
	
	public MyConflictRefinementHierarchical(SolverProvider solverProvider) {
		super();
		this.solverProvider = solverProvider;
	}

	public Set<TimingElement> find(TimingGraph tg, Specification impl, boolean rateMonotonic) {
		Set<TimingElement> predef = findFunctions(tg, impl, rateMonotonic);
		
		MyConflictRefinementDeletion deletion = new MyConflictRefinementDeletion(solverProvider);
		return deletion.find(tg, impl, predef, rateMonotonic);
	}
	
	public Set<TimingElement> findFunctions(TimingGraph tg, Specification impl, boolean rateMonotonic) {

		Set<TimingElement> iis = new HashSet<TimingElement>();
		for (TimingElement te : tg.getVertices()) {
			iis.add(te);
		}

		List<Set<TimingElement>> teList = new ArrayList<Set<TimingElement>>();
		Map<TimingElement,Double> eMap = new HashMap<TimingElement, Double>();
		
		for(Function<Task, Dependency> func: impl.getApplication().getFunctions()){
			Set<TimingElement> fuList = new HashSet<TimingElement>();
			for(TimingElement te: tg){
				if(func.containsVertex(te.getTask())){
					fuList.add(te);
				}
			}
			teList.add(fuList);
		}
		
		Map<TimingDependencyPriority, Pair<TimingElement>> removed = new HashMap<TimingDependencyPriority, Pair<TimingElement>>();
		Double lastE = 0.0;

		for (Set<TimingElement> teSet : teList) {
			System.out.print("conflict refinement " + teSet);
			
			for(TimingElement te: teSet){
				Double e = te.getAttribute("e");
				eMap.put(te, e);
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
			}
			

			

			MyEncoder encoder = new MyEncoder();
			MpProblem problem = encoder.encode(tg, rateMonotonic);
			// System.out.println(problem);

			MpSolver solver = solverProvider.get();
			solver.add(problem);

			MpResult result = solver.solve();

			System.out.println(" " + ((result == null ? "infeasible" : "feasible")));

			// System.out.println("without "+te+" : "+result);

			if (result != null) {
				// this is part of the IIS, reset timing graph
				
				for(TimingElement te: teSet){
					te.setAttribute("e", eMap.get(te));
				}

				for (TimingDependencyPriority td : removed.keySet()) {
					Pair<TimingElement> endpoints = removed.get(td);
					tg.addEdge(td, endpoints);
				}

			} else {
				for(TimingElement te: teSet){
					iis.remove(te);
				}
			}
			removed.clear();
		}

		return iis;

	}

}
