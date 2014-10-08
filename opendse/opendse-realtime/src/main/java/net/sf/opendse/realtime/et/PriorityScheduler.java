package net.sf.opendse.realtime.et;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.MpResult;
import net.sf.jmpi.main.MpSolver;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Function;
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
import net.sf.opendse.realtime.et.qcqp.MyEncoder;
import net.sf.opendse.realtime.et.qcqp.MyConflictRefinement.ConflictRefinementMethod;
import net.sf.opendse.realtime.et.qcqp.MyEncoder.OptimizationObjective;
import net.sf.opendse.realtime.et.qcqp.MyConflictRefinement;
import net.sf.opendse.realtime.et.qcqp.MyConflictRefinementDeletion;
import net.sf.opendse.realtime.et.qcqp.MyConflictRefinementHierarchical;
import net.sf.opendse.realtime.et.qcqp.MyInterpreter;
import net.sf.opendse.realtime.et.qcqp.MyTimingPropertyAnnotater;

public class PriorityScheduler {
	
	public static String PRIORITY = "prio";
	public static String DELAY = "delay";
	public static String JITTER = "jitter";
	public static String RESPONSE = "response";
	public static String PERIOD = "h";
	public static String EXECUTION_TIME = "e";
	public static String DEADLINE = "deadline";

	public static String FIXEDPRIORITY_PREEMPTIVE = "FIXEDPRIORITY_PREEMPTIVE";
	public static String FIXEDPRIORITY_NONPREEMPTIVE = "FIXEDPRIORITY_NONPREEMPTIVE";
	public static String FIXEDDELAY = "FIXEDDELAY";
	public static String FIXEDDELAY_RESPONSE = "FIXEDDELAY_"+RESPONSE;
	public static String FIXEDDELAY_JITTER = "FIXEDDELAY_"+JITTER;
	
	public static String SCHEDULER = "scheduler";
	// public static String DELAY = "DELAY";

	

	protected final Specification specification;
	protected final SolverProvider solverProvider;
	protected TimingGraph originalTimingGraph = null;
	protected TimingGraph resultingTimingGraph = null;

	protected Boolean solved = false;
	protected Boolean isInfeasible = null;

	public PriorityScheduler(Specification specification, SolverProvider solverProvider) {
		super();
		this.specification = specification;
		this.solverProvider = solverProvider;
	}

	public boolean solve(OptimizationObjective objective) {
		if (solved) {
			System.err.println("Problem was already solved.");
		}

		originalTimingGraph = toTimingGraph(specification);
		MyEncoder encoder = new MyEncoder(objective);
		MpProblem problem = encoder.encode(originalTimingGraph);

		MpSolver solver = solverProvider.get();
		solver.add(problem);
		MpResult result = solver.solve();

		solved = true;

		if (result == null) {
			isInfeasible = true;
			return false;
		} else {
			isInfeasible = false;
			MyInterpreter interpreter = new MyInterpreter(solverProvider);
			MyTimingPropertyAnnotater annotator = new MyTimingPropertyAnnotater();

			//System.out.println("Interprete.");
			resultingTimingGraph = interpreter.interprete(originalTimingGraph, specification, result);
			//System.out.println("Annotate.");
			annotator.annotate(resultingTimingGraph, specification); 
			return true;
		}
	}

	public TimingGraph getFullTimingGraph() {
		return originalTimingGraph;
	}
	
	public TimingGraph getReducedTimingGraph() {
		return resultingTimingGraph;
	}

	public Set<TimingElement> determineIIS(ConflictRefinementMethod method) {
		if (isInfeasible == null || isInfeasible == false) {
			System.err.println("Problem is either not solved or feasible.");
		}

		MyConflictRefinement conflictRefinement = null;
		if (method == ConflictRefinementMethod.DELETION) {
			conflictRefinement = new MyConflictRefinementDeletion(solverProvider);
		} else if (method == ConflictRefinementMethod.HIERARCHICAL) {
			conflictRefinement = new MyConflictRefinementHierarchical(solverProvider);
		} else {
			throw new IllegalArgumentException("unknown refinement method " + method);
		}

		Set<TimingElement> iis = conflictRefinement.find(originalTimingGraph, specification);

		System.out.println("IIS (size=" + iis.size() + "): " + iis);

		Map<Task, Function<Task, Dependency>> taskToFunction = new HashMap<Task, Function<Task, Dependency>>();
		for (Function<Task, Dependency> function : specification.getApplication().getFunctions()) {
			for (Task task : function) {
				taskToFunction.put(task, function);
			}
		}

		Set<String> iisFunctions = new HashSet<String>();
		for (TimingElement te : iis) {
			iisFunctions.add(taskToFunction.get(te.getTask()).getId());
		}

		System.out.println("IIS functions: " + iisFunctions);
		
		return iis;
	}

	protected TimingGraph toTimingGraph(Specification implementation) {
		TimingGraphBuilder builder = new TimingGraphBuilder();
		builder.addModifiers(new TimingGraphModifierFilterVertex(new SourceTargetCommunicationPredicate(implementation, builder
				.getTimingGraph())));
		builder.addModifiers(new TimingGraphModifierFilterEdge(new ApplicationPriorityCyclesPredicate(builder.getTimingGraph())));
		builder.addModifiers(new TimingGraphModifierFilterEdge(new ApplicationDependencyInterferencePredicate(builder.getTimingGraph())));
		//builder.addModifiers(new TimingGraphModifierFilterEdge(new RateMonotonicEdgeFilterPredicate(builder.getTimingGraph())));
		builder.addModifiers(new TimingGraphModifierFilterEdge(new DelaySchedulerEdgePredicate(builder.getTimingGraph())));
		TimingGraph tg = builder.build(implementation);

		return tg;
	}

}
