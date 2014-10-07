package net.sf.opendse.realtime.et.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.solver.gurobi.SolverGurobi;
import net.sf.opendse.io.SpecificationReader;
import net.sf.opendse.io.SpecificationWriter;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Function;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
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
import net.sf.opendse.realtime.et.qcqp.MyConflictRefinement;
import net.sf.opendse.realtime.et.qcqp.MyConflictRefinementDeletion;
import net.sf.opendse.realtime.et.qcqp.MyConflictRefinementHierarchical;
import net.sf.opendse.realtime.et.qcqp.MyEncoder;
import net.sf.opendse.realtime.et.qcqp.MyInterpreter;
import net.sf.opendse.realtime.et.qcqp.MyTimingPropertyAnnotater;
import net.sf.opendse.realtime.et.qcqp.SolverProvider;
import net.sf.opendse.visualization.SpecificationViewer;

public class SchedulingStarter {

	public static Set<TimingElement> iis = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SolverProvider solverProvider = new SolverProvider() {
			@Override
			public MpSolver get() {
				MpSolver solver = new SolverGurobi();
				solver.setTimeout(3600);
				return solver;
			}
		};

		String method = args[0];
		String inputFile = args[1];
		String outputFile = args[2];

		SpecificationWriter writer = new SpecificationWriter();
		SpecificationReader reader = new SpecificationReader();

		Specification implementation = reader.read(inputFile);
		SpecificationViewer.view(implementation);
		TimingGraph tg = toTimingGraph(implementation);

		startTimer();

		// "-qcqp", "-qcqpD", "-qcqpE"
		if (method.startsWith("-qcqp")) {

			MyEncoder encoder = new MyEncoder();
			MpProblem problem = encoder.encode(tg);

			MpSolver solver = new SolverGurobi();
			solver.add(problem);
			solver.setTimeout(50000);

			MpResult result = solver.solve();

			if (result == null) {
				System.out.println("problem is infeasible");
				if (!method.equalsIgnoreCase("-qcqp")) {

					MyConflictRefinement conflictRefinement = null;
					if (method.endsWith("D")) {
						conflictRefinement = new MyConflictRefinementDeletion(solverProvider);
					} else if (method.endsWith("H")) {
						conflictRefinement = new MyConflictRefinementHierarchical(solverProvider);
					} else {
						throw new IllegalArgumentException("unknown refinement method " + method);
					}

					Set<TimingElement> iis = conflictRefinement.find(tg, implementation);
					SchedulingStarter.iis = iis;

					System.out.println("IIS: " + iis);
					System.out.println("IIS size: " + iis.size());

					Map<Task, Function<Task, Dependency>> taskToFunction = new HashMap<Task, Function<Task, Dependency>>();
					for (Function<Task, Dependency> function : implementation.getApplication().getFunctions()) {
						for (Task task : function) {
							taskToFunction.put(task, function);
						}
					}

					Set<String> iisFunctions = new HashSet<String>();
					Set<String> iisCluster = new HashSet<String>();

					for (TimingElement te : iis) {
						iisFunctions.add(taskToFunction.get(te.getTask()).getId());
						// iisCluster.add(taskToCluster.get(te.getTask()));
					}

					System.out.println("IIS functions: " + iisFunctions);
				}

				// System.out.println("IIS cluster: " + iisCluster);

			} else {

				System.out.println("problem solved");

				MyInterpreter interpreter = new MyInterpreter(solverProvider);
				MyTimingPropertyAnnotater annotator = new MyTimingPropertyAnnotater();

				TimingGraph rtg = interpreter.interprete(tg, implementation, result);
				annotator.annotate(rtg, implementation);

				System.out.println("interpretation done");
			}
		}

		System.out.println("runtime " + getTime());

		implementation.setAttribute("runtime", getTime());

		writer.write(implementation, outputFile);
	
		Routings<Task, Resource, Link> routings = implementation.getRoutings();
		
		for(Task message: routings.getTasks()){
			System.out.println(message);
			System.out.println(routings.get(message));
		}
		
		SpecificationViewer.view(implementation);

		// TODO Auto-generated method stub

	}

	protected static long start = 0;

	protected static void startTimer() {
		start = System.currentTimeMillis();
	}

	protected static double getTime() {
		return ((double) System.currentTimeMillis() - start) / 1000.0;
	}

	public static TimingGraph toTimingGraph(Specification implementation) {
		TimingGraphBuilder builder = new TimingGraphBuilder();
		builder.addModifiers(new TimingGraphModifierFilterVertex(new SourceTargetCommunicationPredicate(implementation, builder
				.getTimingGraph())));
		builder.addModifiers(new TimingGraphModifierFilterEdge(new ApplicationPriorityCyclesPredicate(builder.getTimingGraph())));
		builder.addModifiers(new TimingGraphModifierFilterEdge(new ApplicationDependencyInterferencePredicate(builder.getTimingGraph())));
		builder.addModifiers(new TimingGraphModifierFilterEdge(new RateMonotonicEdgeFilterPredicate(builder.getTimingGraph())));
		builder.addModifiers(new TimingGraphModifierFilterEdge(new DelaySchedulerEdgePredicate(builder.getTimingGraph())));
		TimingGraph tg = builder.build(implementation);

		return tg;
	}

}
