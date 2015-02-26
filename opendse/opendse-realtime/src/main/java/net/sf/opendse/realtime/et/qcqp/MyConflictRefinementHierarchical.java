/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
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
	protected final boolean rateMonotonic;
	
	public MyConflictRefinementHierarchical(SolverProvider solverProvider, boolean rateMonotonic) {
		super();
		this.solverProvider = solverProvider;
		this.rateMonotonic = rateMonotonic;
	}

	public Set<TimingElement> find(TimingGraph tg, Specification impl) {
		Set<TimingElement> predef = findFunctions(tg, impl);
		
		MyConflictRefinementDeletion deletion = new MyConflictRefinementDeletion(solverProvider, rateMonotonic);
		return deletion.find(tg, impl, predef);
	}
	
	public Set<TimingElement> findFunctions(TimingGraph tg, Specification impl) {

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
