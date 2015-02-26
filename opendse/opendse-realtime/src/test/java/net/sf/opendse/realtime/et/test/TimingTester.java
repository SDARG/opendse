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
package net.sf.opendse.realtime.et.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.solver.gurobi.SolverGurobi;
import net.sf.opendse.generator.ApplicationGenerator;
import net.sf.opendse.generator.ArchitectureGenerator;
import net.sf.opendse.generator.MappingGenerator;
import net.sf.opendse.io.SpecificationWriter;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Function;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.ValidImplementationPredicate;
import net.sf.opendse.model.ValidSpecificationPredicate;
import net.sf.opendse.optimization.encoding.RoutingFilter;
import net.sf.opendse.optimization.encoding.RoutingGenerator;
import net.sf.opendse.optimization.encoding.SingleImplementation;
import net.sf.opendse.realtime.et.PriorityScheduler;
import net.sf.opendse.realtime.et.SolverProvider;
import net.sf.opendse.realtime.et.TimingGraphViewer;
import net.sf.opendse.realtime.et.graph.ApplicationDependencyInterferencePredicate;
import net.sf.opendse.realtime.et.graph.ApplicationPriorityCyclesPredicate;
import net.sf.opendse.realtime.et.graph.DelaySchedulerEdgePredicate;
import net.sf.opendse.realtime.et.graph.RateMonotonicEdgeFilterPredicate;
import net.sf.opendse.realtime.et.graph.SourceTargetCommunicationPredicate;
import net.sf.opendse.realtime.et.graph.TimingElement;
import net.sf.opendse.realtime.et.graph.TimingGraph;
import net.sf.opendse.realtime.et.graph.TimingGraphBuilder;
import net.sf.opendse.realtime.et.graph.TimingGraphModifierFilterEdge;
import net.sf.opendse.realtime.et.graph.TimingGraphModifierFilterVertex;
import net.sf.opendse.realtime.et.qcqp.MyConflictRefinementDeletion;
import net.sf.opendse.realtime.et.qcqp.MyEncoder;
import net.sf.opendse.realtime.et.qcqp.MyEncoder.OptimizationObjective;
import net.sf.opendse.realtime.et.qcqp.MyInterpreter;
import net.sf.opendse.realtime.et.qcqp.MyTimingPropertyAnnotater;
import net.sf.opendse.visualization.SpecificationViewer;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;

public class TimingTester {

	public static void main(String args[]) {

		/*
		 * Specification impl =
		 * SpecificationGenerator.getImplementationScalabilityGateway(1, 3, 2,
		 * 6, 1.0, 0).getImpl();
		 */
		Specification impl = getImplementationToy();
		
		SpecificationWriter writer = new SpecificationWriter();
		writer.write(impl, "testspecs/toy.xml");
		
	}

	public static Specification getImplementationScalability() {
		ArchitectureGenerator architectureGenerator = new ArchitectureGenerator(0);
		ApplicationGenerator applicationGenerator = new ApplicationGenerator(0);
		MappingGenerator mappingGenerator = new MappingGenerator(0);
		RoutingGenerator routingGenerator = new RoutingGenerator();

		Architecture<Resource, Link> architecture = architectureGenerator.getStar(1, 6);
		Set<Resource> ECUs = new HashSet<Resource>();

		for (Resource resource : architecture) {
			if (architecture.getIncidentEdges(resource).size() == 1) {
				resource.setType("ECU");
				resource.setAttribute("scheduler", PriorityScheduler.FIXEDPRIORITY_PREEMPTIVE);
				ECUs.add(resource);
			} else {
				resource.setType("CAN");
				resource.setAttribute("scheduler", PriorityScheduler.FIXEDPRIORITY_NONPREEMPTIVE);
			}
		}

		Application<Task, Dependency> application = new Application<Task, Dependency>();

		for (int i = 0; i < 9; i++) {
			Application<Task, Dependency> appl = applicationGenerator.generate(5, 3, 3);
			applicationGenerator.insertCommunication(appl, 1, 8);
			application = applicationGenerator.merge(appl, application);
		}

		Random random = new Random(0);

		double[] periods = { 10.0, 20.0, 40.0 };

		for (Function<Task, Dependency> function : application.getFunctions()) {
			double h = periods[random.nextInt(periods.length)];

			for (Task task : function) {
				double e = (10.0 + random.nextInt(90)) / 100.0;
				if (Models.isCommunication(task)) {
					e = 0.2;
				}
				task.setAttribute("e", e);
				task.setAttribute("h", h);

				if (application.getOutEdges(task).isEmpty()) {
					task.setAttribute("deadline", h);
				}
			}

		}

		Mappings<Task, Resource> mappings = mappingGenerator.create(application, ECUs, 1, 1);

		Routings<Task, Resource, Link> routings = routingGenerator.fill(application, architecture);

		Specification spec = new Specification(application, architecture, mappings, routings);

		SingleImplementation singleImplementation = new SingleImplementation();
		Specification impl = singleImplementation.get(spec);

		ValidSpecificationPredicate validSpec = new ValidSpecificationPredicate();
		validSpec.evaluate(spec);

		ValidImplementationPredicate validImpl = new ValidImplementationPredicate();
		validImpl.evaluate(impl);

		return impl;

	}

	protected static void add(Architecture<Resource, Link> to, Architecture<Resource, Link> from) {
		for (Resource resource : from) {
			to.addVertex(resource);
		}
		for (Link link : from.getEdges()) {
			to.addEdge(link, from.getEndpoints(link));
		}
	}

	public static Specification getImplementationScalabilityGateway() {
		ArchitectureGenerator architectureGenerator = new ArchitectureGenerator(0);
		ApplicationGenerator applicationGenerator = new ApplicationGenerator(0);
		MappingGenerator mappingGenerator = new MappingGenerator(0);
		RoutingGenerator routingGenerator = new RoutingGenerator();

		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();
		for (int i = 0; i < 3; i++) {
			add(architecture, architectureGenerator.getStar(1, 6));
		}

		Set<Resource> ECUs = new HashSet<Resource>();
		List<Resource> CANs = new ArrayList<Resource>();

		for (Resource resource : architecture) {
			if (architecture.getIncidentEdges(resource).size() == 1) {
				resource.setType("ECU");
				resource.setAttribute("scheduler", PriorityScheduler.FIXEDPRIORITY_PREEMPTIVE);
				ECUs.add(resource);
			} else {
				resource.setType("CAN");
				resource.setAttribute("scheduler", PriorityScheduler.FIXEDPRIORITY_NONPREEMPTIVE);
				CANs.add(resource);
			}
		}

		Resource gateway = new Resource("gateway");
		gateway.setType("gateway");
		gateway.setAttribute("scheduler", PriorityScheduler.FIXEDDELAY);
		architecture.addVertex(gateway);

		for (Resource CAN : CANs) {
			Link link = new Link("link_" + CAN.getId());
			architecture.addEdge(link, CAN, gateway);
		}

		Application<Task, Dependency> application = new Application<Task, Dependency>();
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();

		for (int i = 0; i < 13; i++) {
			Application<Task, Dependency> appl = applicationGenerator.generate(5, 3, 3);
			applicationGenerator.insertCommunication(appl, 1, 8);
			application = applicationGenerator.merge(appl, application);
		}

		Random random = new Random(0);

		double[] periods = { 10.0, 20.0, 40.0, 80.0 };

		for (Function<Task, Dependency> function : application.getFunctions()) {
			double h = periods[random.nextInt(periods.length)];

			for (Task task : function) {
				double e = (10.0 + random.nextInt(90)) / 100.0;
				if (Models.isCommunication(task)) {
					e = 0.2;
				}
				task.setAttribute("e", e);
				task.setAttribute("h", h);

				if (application.getOutEdges(task).isEmpty()) {
					task.setAttribute("deadline", h);
				}
			}

			Resource base = CANs.get(random.nextInt(CANs.size()));

			DijkstraDistance<Resource, Link> dijkstraDistance = new DijkstraDistance<Resource, Link>(architecture, true);
			Map<Resource, Double> weights = new HashMap<Resource, Double>();
			for (Resource ecu : ECUs) {
				weights.put(ecu, 1.0 / Math.pow(dijkstraDistance.getDistance(base, ecu).doubleValue(), 2));
			}

			for (Task task : function) {
				if (Models.isProcess(task)) {
					Resource target = getWeightedRandom(weights, random);
					Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m-" + task.getId() + "-" + target.getId(), task, target);
					mappings.add(mapping);
				}
			}

		}

		// Mappings<Task, Resource> mappings =
		// mappingGenerator.create(application, ECUs, 1, 1);

		Routings<Task, Resource, Link> routings = routingGenerator.fill(application, architecture);

		Specification spec = new Specification(application, architecture, mappings, routings);

		RoutingFilter.filter(spec);

		SpecificationViewer.view(spec);

		SingleImplementation singleImplementation = new SingleImplementation();
		Specification impl = singleImplementation.get(spec);

		ValidSpecificationPredicate validSpec = new ValidSpecificationPredicate();
		validSpec.evaluate(spec);

		ValidImplementationPredicate validImpl = new ValidImplementationPredicate();
		validImpl.evaluate(impl);

		return impl;

	}

	public static Specification getImplementationToy() {

		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();

		Resource can = new Resource("can");
		can.setAttribute("scheduler", PriorityScheduler.FIXEDPRIORITY_NONPREEMPTIVE);
		can.setType("CAN");
		architecture.addVertex(can);

		Resource[] r = new Resource[4];

		for (int i = 1; i <= 3; i++) {
			r[i] = new Resource("r" + i);
			r[i].setAttribute("scheduler", PriorityScheduler.FIXEDPRIORITY_PREEMPTIVE);
			r[i].setType("ECU");
			architecture.addEdge(new Link("l" + i), r[i], can);
		}

		Application<Task, Dependency> application = new Application<Task, Dependency>();

		Set<Integer> communicationTask = new HashSet<Integer>(Arrays.asList(2, 6, 7, 10, 11));

		double[][] e = { { 1, 0.8 }, { 2, 0.26 }, { 3, 2.0 }, { 4, 1.2 }, { 5, 0.5 }, { 6, 0.26 }, { 7, 0.146 }, { 8, 0.6 }, { 9, 0.8 },
				{ 10, 0.26 }, { 11, 0.146 }, { 12, 0.8 } };
		int[][] dependencies = { { 1, 2 }, { 2, 3 }, { 2, 4 }, { 5, 6 }, { 5, 7 }, { 6, 8 }, { 7, 9 }, { 8, 10 }, { 9, 11 }, { 10, 12 },
				{ 11, 12 } };
		double[][] deadlines = { { 3, 4.0 }, { 4, 4.0 }, { 12, 5.0 } };
		// double[][] deadlines = { { 3, 5.0 }, { 4, 5.0 }, { 12, 5.0 } };

		Task[] t = new Task[13];
		for (int i = 1; i <= 12; i++) {
			if (!communicationTask.contains(i)) {
				t[i] = new Task("t" + i);
			} else {
				t[i] = new Communication("t" + i);
			}
			application.addVertex(t[i]);
			t[i].setAttribute("h", 5.0);
		}
		for (double[] ev : e) {
			t[(int) ev[0]].setAttribute("e", ev[1]);
		}
		for (int[] dep : dependencies) {
			Dependency d = new Dependency("d-" + dep[0] + "-" + dep[1]);
			application.addEdge(d, t[dep[0]], t[dep[1]]);
		}
		for (double[] deadline : deadlines) {
			t[(int) deadline[0]].setAttribute("deadline", deadline[1]);
		}

		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();

		int[][] maps = { { 1, 3 }, { 3, 1 }, { 4, 2 }, { 5, 1 }, { 8, 3 }, { 9, 3 }, { 12, 2 } };

		for (int[] map : maps) {
			mappings.add(new Mapping<Task, Resource>("m" + map[0], t[map[0]], r[map[1]]));
		}

		RoutingGenerator routingGenerator = new RoutingGenerator();
		Routings<Task, Resource, Link> routings = routingGenerator.fill(application, architecture);

		Specification spec = new Specification(application, architecture, mappings, routings);

		SingleImplementation singleImplementation = new SingleImplementation();
		Specification impl = singleImplementation.get(spec);

		ValidSpecificationPredicate validSpec = new ValidSpecificationPredicate();
		validSpec.evaluate(spec);

		ValidImplementationPredicate validImpl = new ValidImplementationPredicate();
		validImpl.evaluate(impl);

		return impl;

	}

	public static Specification getImplementationToyInfeasible() {
		Specification spec = getImplementationToy();
		spec.getApplication().getVertex("t3").setAttribute("e", 2.3);
		// spec.getApplication().getVertex("t5").setAttribute("e", 2.6);
		return spec;
	}

	public static <E> E getWeightedRandom(Map<E, Double> weights, Random random) {

		E result = null;
		double bestValue = Double.MAX_VALUE;

		for (E element : weights.keySet()) {
			double value = -Math.log(random.nextDouble()) / weights.get(element);

			if (value < bestValue) {
				bestValue = value;
				result = element;
			}
		}

		return result;
	}

}
