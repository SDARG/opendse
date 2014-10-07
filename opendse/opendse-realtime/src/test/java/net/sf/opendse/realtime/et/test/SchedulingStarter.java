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
import net.sf.opendse.realtime.et.PriorityScheduler;
import net.sf.opendse.realtime.et.SolverProvider;
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
import net.sf.opendse.realtime.et.qcqp.MyConflictRefinement.ConflictRefinementMethod;
import net.sf.opendse.realtime.et.qcqp.MyEncoder.OptimizationObjective;
import net.sf.opendse.visualization.SpecificationViewer;

public class SchedulingStarter {

	//public static Set<TimingElement> iis = null;

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

		for(Task task: implementation.getApplication()){
			task.setAttribute("prio", null);
		}
		startTimer();

		// "-qcqp", "-qcqpD", "-qcqpE"
		if (method.startsWith("-qcqp")) {
			PriorityScheduler scheduler = new PriorityScheduler(implementation, solverProvider);

			if (!scheduler.solve(OptimizationObjective.DELAY)) {
				System.out.println("Problem is infeasible.");
				if (!method.equalsIgnoreCase("-qcqp")) {

					if (method.endsWith("D")) {
						scheduler.determineIIS(ConflictRefinementMethod.DELETION);
					} else if (method.endsWith("H")) {
						scheduler.determineIIS(ConflictRefinementMethod.HIERARCHICAL);
					} else {
						throw new IllegalArgumentException("unknown refinement method " + method);
					}
				}
			}
		}

		System.out.println("runtime " + getTime());

		implementation.setAttribute("runtime", getTime());

		writer.write(implementation, outputFile);

		SpecificationViewer.view(implementation);

	}

	protected static long start = 0;

	protected static void startTimer() {
		start = System.currentTimeMillis();
	}

	protected static double getTime() {
		return ((double) System.currentTimeMillis() - start) / 1000.0;
	}

}
