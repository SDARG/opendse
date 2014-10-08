package net.sf.opendse.realtime.et.qcqp;

import static net.sf.jmpi.main.expression.MpExpr.prod;
import static net.sf.jmpi.main.expression.MpExpr.sum;
import static net.sf.opendse.realtime.et.PriorityScheduler.DELAY;
import static net.sf.opendse.realtime.et.PriorityScheduler.FIXEDDELAY;
import static net.sf.opendse.realtime.et.PriorityScheduler.FIXEDPRIORITY_NONPREEMPTIVE;
import static net.sf.opendse.realtime.et.PriorityScheduler.FIXEDPRIORITY_PREEMPTIVE;
import static net.sf.opendse.realtime.et.PriorityScheduler.SCHEDULER;
import static net.sf.opendse.realtime.et.qcqp.vars.Vars.a;
import static net.sf.opendse.realtime.et.qcqp.vars.Vars.b;
import static net.sf.opendse.realtime.et.qcqp.vars.Vars.c;
import static net.sf.opendse.realtime.et.qcqp.vars.Vars.d;
import static net.sf.opendse.realtime.et.qcqp.vars.Vars.i;
import static net.sf.opendse.realtime.et.qcqp.vars.Vars.j;
import static net.sf.opendse.realtime.et.qcqp.vars.Vars.r;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jmpi.main.MpConstraint;
import net.sf.jmpi.main.MpDirection;
import net.sf.jmpi.main.MpProblem;
import net.sf.jmpi.main.expression.MpExpr;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.realtime.et.PriorityScheduler;
import net.sf.opendse.realtime.et.graph.TimingDependency;
import net.sf.opendse.realtime.et.graph.TimingDependencyPriority;
import net.sf.opendse.realtime.et.graph.TimingDependencyTrigger;
import net.sf.opendse.realtime.et.graph.TimingElement;
import net.sf.opendse.realtime.et.graph.TimingGraph;

import org.apache.commons.collections15.Transformer;

public class MyEncoder {

	protected CycleCounter cycleCounter = CycleCounter.LOCAL;
	//protected boolean forbidGlobalCycles = false;
	protected final boolean uniquePriorityAssignment;
	protected OptimizationObjective objective = null;

	public enum OptimizationObjective {
		DELAY, DELAY_AND_JITTER_ALL;
	}
	
	public enum CycleCounter {
		NONE, LOCAL, GLOBAL;
	}

	protected final Transformer<TimingDependencyPriority, Boolean> definedPriorities;

	public MyEncoder() {
		this(null);
	}

	public MyEncoder(OptimizationObjective objective) {
		this(objective, new Transformer<TimingDependencyPriority, Boolean>() {
			public Boolean transform(TimingDependencyPriority input) {
				return null;
			}
		});
	}

	public MyEncoder(OptimizationObjective objective, Transformer<TimingDependencyPriority, Boolean> definedPriorities) {
		this(objective, definedPriorities, true);
	}

	public MyEncoder(OptimizationObjective objective, Transformer<TimingDependencyPriority, Boolean> definedPriorities, boolean uniquePriorityAssignment) {
		this.objective = objective;
		this.definedPriorities = definedPriorities;
		this.uniquePriorityAssignment = uniquePriorityAssignment;
	}

	public MpProblem encode(TimingGraph tg) {
		MpProblem problem = new MpProblem();

		// Map<Resource, Set<Task>> resourceToTask = getResourceToTasks(tg);

		for (TimingElement te : tg.getVertices()) {
			if (cycleCounter != CycleCounter.NONE) {
				problem.addVar(0, c(te), 1000.0, Double.class);
			}
			problem.addVar(0, r(te), 1000.0, Double.class);
			problem.addVar(0, j(te), 1000.0, Double.class);
			problem.addVar(0, d(te), 1000.0, Double.class);
		}

		for (TimingDependency td : tg.getEdges()) {
			if (td instanceof TimingDependencyPriority) {
				TimingDependencyPriority tdp = (TimingDependencyPriority) td;

				problem.addVar(a(td), Boolean.class);

				Boolean p = definedPriorities.transform(tdp);
				if (p != null) {
					problem.add(sum(a(td)), "=", p ? 1 : 0);
				}

				TimingElement t1 = tg.getSource(td);
				TimingElement t2 = tg.getDest(td);

				problem.addVar(0, i(t1, t2), 1000, Integer.class);
			}
		}

		// resource acyclic

		if (uniquePriorityAssignment) {
			Set<TimingDependency> visited = new HashSet<TimingDependency>();

			for (TimingDependency td : tg.getEdges()) {
				if (td instanceof TimingDependencyPriority && !visited.contains(td)) {
					TimingElement source = tg.getSource(td);
					TimingElement dest = tg.getDest(td);

					TimingDependency td2 = null;

					for (TimingDependency tdopposite : tg.findEdgeSet(dest, source)) {
						if (tdopposite instanceof TimingDependencyPriority) {
							td2 = (TimingDependencyPriority) tdopposite;
						}
					}

					visited.add(td);
					visited.add(td2);

					MpExpr e = sum(a(td));
					if (td2 != null) {
						e.add(a(td2));
					}
					problem.add(e, "=", 1);
				}
			}

			Map<Resource, Set<TimingElement>> resourceToTimingElement = getResourceToTimingElement(tg);
			for (Resource resource : resourceToTimingElement.keySet()) {
				List<TimingElement> tasks = new ArrayList<TimingElement>(resourceToTimingElement.get(resource));
				// System.out.println(resource+" "+tasks);
				for (TimingElement t0 : tasks) {
					for (TimingElement t1 : tasks) {
						for (TimingElement t2 : tasks) {
							// System.out.println(t0 + " " + t1 + " " + t2);
							if (!t0.equals(t1) && !t1.equals(t2) && !t2.equals(t0)) {
								TimingDependencyPriority a0 = findDependencyPriority(tg, t0, t1);
								TimingDependencyPriority a1 = findDependencyPriority(tg, t1, t2);
								TimingDependencyPriority a2 = findDependencyPriority(tg, t2, t0);

								if (a0 != null && a1 != null && a2 != null) {
									problem.add(sum(a(a0), a(a1), a(a2)), "<=", 2);
								}
							}
						}
					}
				}
			}
		}

		// global acyclic
		if (cycleCounter != CycleCounter.NONE) {
			for (TimingDependency td : tg.getEdges()) {
				TimingElement source = tg.getSource(td);
				TimingElement dest = tg.getDest(td);

				if (td instanceof TimingDependencyTrigger && cycleCounter == CycleCounter.GLOBAL) {
					problem.add(sum(c(source), 0.1), "<=", sum(c(dest)));
				} else if (td instanceof TimingDependencyPriority) {
					problem.add(sum(c(source), 0.1), "<=", sum(c(dest), prod(-10000.0, a(td)), 10000.0));
				}
			}
		}

		// preemptive and non-preemptive

		for (TimingElement te : tg.getVertices()) {
			Resource resource = te.getResource();

			String scheduler = resource.getAttribute(SCHEDULER);

			if (FIXEDPRIORITY_PREEMPTIVE.equals(scheduler)) {
				MpExpr lhs = sum(r(te));
				MpExpr rhs = sum(e(te));

				for (TimingDependency td : tg.getInEdges(te)) {
					if (td instanceof TimingDependencyPriority) {
						TimingElement te2 = tg.getOpposite(te, td);
						rhs.addTerm(e(te2), a(td), i(te2, te));

						problem.add(sum(i(te2, te)), ">=", sum(prod(1d / h(te2), r(te)), prod(1d / h(te2), j(te2))));
					}
				}

				problem.add(lhs, "=", rhs);
			} else if (FIXEDPRIORITY_NONPREEMPTIVE.equals(scheduler)) {
				// problem.add(e().add(d(te)), "=", e().con(e(task)));

				problem.addVar(0, b(te), 1000.0, Double.class);

				MpExpr lhs = sum(r(te));
				MpExpr rhs = sum(e(te), b(te));

				problem.add(sum(b(te)), ">=", sum(e(te)));

				for (TimingDependency td : tg.getOutEdges(te)) {
					if (td instanceof TimingDependencyPriority) {
						TimingElement te2 = tg.getOpposite(te, td);
						problem.add(sum(b(te)), ">=", sum(prod(e(te2), a(td))));
					}
				}

				for (TimingDependency td : tg.getInEdges(te)) {
					if (td instanceof TimingDependencyPriority) {
						TimingElement te2 = tg.getOpposite(te, td);
						rhs.addTerm(e(te2), a(td), i(te2, te));

						problem.add(sum(i(te2, te)), ">=", sum(prod(1d / h(te2), r(te)), prod(1d / h(te2), j(te2)), -e(te) / h(te2)));
					}
				}

				problem.add(lhs, "=", rhs);
			} else if (FIXEDDELAY.equals(scheduler)) {

				Double delay = resource.getAttribute(PriorityScheduler.FIXEDDELAY_RESPONSE);

				if (delay == null) {
					delay = 0.0;
				}

				problem.add(sum(r(te)), "=", sum(e(te) + delay));
			}

		}

		// jitter, delay, and deadline

		for (TimingElement te : tg.getVertices()) {
			Set<TimingDependencyTrigger> inEdges = new HashSet<TimingDependencyTrigger>();
			for (TimingDependency td : tg.getInEdges(te)) {
				if (td instanceof TimingDependencyTrigger) {
					inEdges.add((TimingDependencyTrigger) td);
				}
			}

			if (inEdges.isEmpty()) {
				problem.add(sum(j(te)), "=", 0);
				problem.add(sum(d(te)), "=", sum(r(te)));
			} else {
				for (TimingDependencyTrigger td : inEdges) {
					TimingElement te2 = tg.getOpposite(te, td);
					problem.add(sum(j(te)), ">=", sum(r(te2), -e(te2), j(te2)));
					problem.add(sum(d(te)), ">=", sum(r(te), d(te2)));
					problem.add(sum(d(te)), ">=", sum(j(te)));
				}
			}

			if (hasDeadline(te)) {
				double deadline = deadline(te);
				problem.add(sum(d(te)), "<=", sum(deadline));
			}

		}

		// equal priority
		List<Task> tasks = new ArrayList<Task>();
		Map<Task, Set<TimingElement>> taskMap = new HashMap<Task, Set<TimingElement>>();
		Map<Task, Set<TimingElement>> taskMapMultiMapping = new HashMap<Task, Set<TimingElement>>();

		for (TimingElement te : tg) {
			final Task task = te.getTask();
			if (!taskMap.containsKey(task)) {
				taskMap.put(task, new HashSet<TimingElement>());
			}
			taskMap.get(task).add(te);
		}
		tasks.addAll(taskMap.keySet());

		for (Task task : taskMap.keySet()) {
			Set<TimingElement> tes = taskMap.get(task);
			if (tes.size() > 1) {
				taskMapMultiMapping.put(task, tes);
			}
		}

		for (Task task : taskMapMultiMapping.keySet()) {
			List<TimingElement> tes = new ArrayList<TimingElement>(taskMapMultiMapping.get(task));

			for (int i = 0; i < tes.size(); i++) {
				for (int j = i + 1; j < tes.size(); j++) {
					TimingElement iTe = tes.get(i);
					TimingElement jTe = tes.get(j);

					for (TimingDependency iTd : tg.getOutEdges(iTe)) {
						for (TimingDependency jTd : tg.getOutEdges(jTe)) {
							if (iTd instanceof TimingDependencyPriority && jTd instanceof TimingDependencyPriority) {
								TimingElement iTeOpp = tg.getOpposite(iTe, iTd);
								TimingElement jTeOpp = tg.getOpposite(jTe, jTd);

								if (iTeOpp.getTask().equals(jTeOpp.getTask())) {
									// System.out.println(iTe + " " + iTeOpp +
									// " " + jTe + " " + jTeOpp);
									problem.add(sum(a(iTd)), "=", sum(a(iTd)));
								}
							}
						}
					}
				}
			}

		}

		if (objective == OptimizationObjective.DELAY) {
			MpExpr objective = sum();
			for (TimingElement te : tg.getVertices()) {
				Double deadline = te.getAttribute(PriorityScheduler.DEADLINE);

				if (deadline != null) {
					objective.add(d(te));
				}

				// objective.add(dd(te));
				// objective.add(j(te));
			}
			problem.setObjective(objective, MpDirection.MIN);
		} else if (objective == OptimizationObjective.DELAY_AND_JITTER_ALL) {
			MpExpr objective = sum();
			for (TimingElement te : tg.getVertices()) {
				objective.add(d(te));
				objective.add(j(te));
			}
			problem.setObjective(objective, MpDirection.MIN);
		}

		// System.out.println(problem);
		
		System.out.println("Problem with "+problem.getVariablesCount()+" variables and "+problem.getConstraintsCount()+" constraints.");

		return problem;
	}

	protected Map<Resource, Set<Task>> getResourceToTasks(TimingGraph tg) {
		Map<Resource, Set<Task>> map = new HashMap<Resource, Set<Task>>();

		for (TimingElement te : tg.getVertices()) {
			Resource resource = te.getResource();
			Task task = te.getTask();

			if (!map.containsKey(resource)) {
				map.put(resource, new HashSet<Task>());
			}

			map.get(resource).add(task);
		}
		return map;
	}

	protected Map<Resource, Set<TimingElement>> getResourceToTimingElement(TimingGraph tg) {
		Map<Resource, Set<TimingElement>> map = new HashMap<Resource, Set<TimingElement>>();

		for (TimingElement te : tg.getVertices()) {
			Resource resource = te.getResource();

			if (!map.containsKey(resource)) {
				map.put(resource, new HashSet<TimingElement>());
			}

			map.get(resource).add(te);
		}
		return map;
	}

	protected double e(TimingElement te) {
		return (Double) te.getAttribute("e");
	}

	protected double h(TimingElement te) {
		return (Double) te.getAttribute("h");
	}

	protected boolean hasDeadline(TimingElement te) {
		return deadline(te) != null;
	}

	protected Double deadline(TimingElement te) {
		return (Double) te.getAttribute("deadline");
	}

	protected TimingDependencyPriority findDependencyPriority(TimingGraph tg, TimingElement source, TimingElement dest) {
		for (TimingDependency dependency : tg.findEdgeSet(source, dest)) {
			if (dependency instanceof TimingDependencyPriority) {
				return (TimingDependencyPriority) dependency;
			}
		}
		return null;
	}

}
