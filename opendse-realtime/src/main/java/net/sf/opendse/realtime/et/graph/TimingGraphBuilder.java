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
package net.sf.opendse.realtime.et.graph;

import static net.sf.opendse.realtime.et.PriorityScheduler.EXECUTION_TIME;
import static net.sf.opendse.realtime.et.PriorityScheduler.PERIOD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.ValidImplementationPredicate;
import net.sf.opendse.visualization.algorithm.BellmanFord;

public class TimingGraphBuilder {

	protected List<TimingGraphModifier> modifiers = new ArrayList<TimingGraphModifier>();
	protected Specification impl = null;

	public TimingGraphBuilder(TimingGraphModifier... modifiers) {
		addModifiers(modifiers);
	}

	public void addModifiers(TimingGraphModifier... modifiers) {
		for (TimingGraphModifier modifier : modifiers) {
			this.modifiers.add(modifier);
		}
	}

	protected TimingGraph timingGraph = new TimingGraph();

	public TimingGraph getTimingGraph() {
		return timingGraph;
	}

	public TimingGraph build(Specification implementation) {
		impl = implementation;
		
		fillTriggering(implementation, timingGraph);
		fillPriorities(implementation, timingGraph);

		for (TimingGraphModifier modifier : modifiers) {
			modifier.apply(implementation, timingGraph);
		}

		for (TimingElement te : timingGraph.getVertices()) {

			Double h = te.getTask().getAttribute(PERIOD);
			Double e = null;
			if(te.getTask().getAttributeNames().contains(EXECUTION_TIME+":"+te.getResource().getId())){
				e = te.getTask().getAttribute(EXECUTION_TIME+":"+te.getResource().getId());
			} else {
				e = te.getTask().getAttribute(EXECUTION_TIME);
			}
			
			Double deadline = te.getTask().getAttribute("deadline");

			if (h == null || e == null) {
				throw new RuntimeException("Task of timing element " + te + " has not e and h defined: e=" + e + " h=" + h);
			}

			te.setAttribute("h", h);
			te.setAttribute("e", e);

			if (deadline != null) {
				te.setAttribute("deadline", deadline);
			}

		}

		annotateBackwardsDeadlines(timingGraph);
		
		return timingGraph;
	}

	public static void annotateBackwardsDeadlines(TimingGraph timingGraph) {
				
		
		TimingGraph tg = new TimingGraph();
		for (TimingElement te : timingGraph) {
			tg.addVertex(te);
			te.setAttribute("deadline*", null);
		}
		for (TimingDependency td : timingGraph.getEdges()) {
			if (td instanceof TimingDependencyTrigger) {
				tg.addEdge(td, timingGraph.getEndpoints(td));
			}
		}
		
		BellmanFord<TimingElement, TimingDependency> bellmanFord = new BellmanFord<TimingElement, TimingDependency>();
		final Transformer<TimingElement, Double> transformer = bellmanFord.transform(tg);
		List<TimingElement> order = new ArrayList<TimingElement>(tg.getVertices());
		Collections.sort(order, new Comparator<TimingElement>() {
			@Override
			public int compare(TimingElement o1, TimingElement o2) {
				return transformer.transform(o1).compareTo(transformer.transform(o2));
			}
		});
		Collections.reverse(order);

		for (TimingElement te : order) {
			Double deadline = te.getAttribute("deadline");
			if (deadline == null) {
				deadline = Double.MAX_VALUE;
			}

			for (TimingElement te2 : tg.getSuccessors(te)) {
				Double dStar = te2.getAttribute("deadline*");
				Double e = te2.getAttribute("e");
				deadline = Math.min(deadline, dStar - e);
			}
			te.setAttribute("deadline*", adjust(deadline));
		}

	}

	protected void fillTriggering(Specification implementation, TimingGraph timingGraph) {

		ValidImplementationPredicate predicate = new ValidImplementationPredicate();
		if (!predicate.evaluate(implementation)) {
			throw new IllegalArgumentException("Specification has to be valid implementation.");
		}

		for (Mapping<Task, Resource> mapping : implementation.getMappings()) {
			TimingElement te = new TimingElement(mapping.getSource(), mapping.getTarget());
			timingGraph.addVertex(te);
		}

		for (Task communication : Models.filterCommunications(implementation.getApplication())) {
			for (Resource resource : implementation.getRoutings().get(communication)) {
				TimingElement te = new TimingElement(communication, resource);
				timingGraph.addVertex(te);
			}
		}

		for (Dependency dependency : implementation.getApplication().getEdges()) {
			Task t1 = implementation.getApplication().getSource(dependency);
			Task t2 = implementation.getApplication().getDest(dependency);

			Resource resource = null;
			if (Models.isProcess(t1)) {
				resource = implementation.getMappings().getTargets(t1).iterator().next();
			} else {
				resource = implementation.getMappings().getTargets(t2).iterator().next();
			}

			TimingElement te1 = getTimingElement(t1, resource, timingGraph);
			TimingElement te2 = getTimingElement(t2, resource, timingGraph);

			timingGraph.addEdge(new TimingDependencyTrigger(), te1, te2, EdgeType.DIRECTED);
		}

		for (Task communication : Models.filterCommunications(implementation.getApplication())) {
			Architecture<Resource, Link> routing = implementation.getRoutings().get(communication);

			for (Link link : routing.getEdges()) {
				Resource r1 = routing.getSource(link);
				Resource r2 = routing.getDest(link);

				TimingElement te1 = getTimingElement(communication, r1, timingGraph);
				TimingElement te2 = getTimingElement(communication, r2, timingGraph);
				
				timingGraph.addEdge(new TimingDependencyTrigger(), te1, te2, EdgeType.DIRECTED);
			}
		}
	}

	protected void fillPriorities(Specification implementation, TimingGraph timingGraph) {

		for (Resource resource : implementation.getArchitecture()) {
			List<Task> tasks = new ArrayList<Task>(implementation.getMappings().getSources(resource));
			//System.out.println("set "+tasks);
			
			for (Task communication : Models.filterCommunications(implementation.getApplication())) {
				Architecture<Resource, Link> routing = implementation.getRoutings().get(communication);

				if (routing.containsVertex(resource)) {
					//System.out.println("contains vertex "+resource+" "+communication+" "+routing.getVertices());
					tasks.add(communication);
				}
			}
			
			//System.out.println("set "+tasks);

			for (List<Task> taskSet : Arrays.asList(tasks)) {
				//System.out.println("list "+taskSet);
				for (int i = 0; i < taskSet.size(); i++) {
					for (int j = i + 1; j < taskSet.size(); j++) {
						TimingDependencyPriority d1 = new TimingDependencyPriority();
						TimingDependencyPriority d2 = new TimingDependencyPriority();
						timingGraph.addEdge(d1, getTimingElement(taskSet.get(i), resource, timingGraph),
								getTimingElement(taskSet.get(j), resource, timingGraph));
						timingGraph.addEdge(d2, getTimingElement(taskSet.get(j), resource, timingGraph),
								getTimingElement(taskSet.get(i), resource, timingGraph));

					}
				}
			}

		}
	}

	protected TimingElement getTimingElement(Task task, Resource resource, TimingGraph tg) {
		if(Models.isProcess(task)){
			if(!impl.getMappings().getTargets(task).contains(resource)){
				throw new RuntimeException(task+" "+resource+" :this timing element does not exist in the implementation");
			}
		} else {
			if(!impl.getRoutings().get(task).containsVertex(resource)){
				throw new RuntimeException(task+" "+resource+" :this timing element does not exist in the implementation");
			}
		}
		
		
		for (TimingElement te : tg.getVertices()) {
			if (te.getTask().equals(task) && te.getResource().equals(resource)) {
				return te;
			}
		}
		return null;
	}

	public static double adjust(double value) {
		return Math.round(value * 100000.0) / 100000.0;
	}

}
