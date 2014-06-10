package net.sf.opendse.optimization.encoding;

import static edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED;
import static net.sf.opendse.model.Models.filterCommunications;
import static net.sf.opendse.model.Models.filterProcesses;
import static net.sf.opendse.model.Models.getInLinks;
import static net.sf.opendse.model.Models.getLinks;
import static net.sf.opendse.model.Models.getOutLinks;
import static net.sf.opendse.model.Models.isProcess;
import static net.sf.opendse.optimization.encoding.variables.Variables.p;
import static net.sf.opendse.optimization.encoding.variables.Variables.var;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;
import net.sf.opendse.optimization.encoding.variables.CLRR;
import net.sf.opendse.optimization.encoding.variables.CR;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.Inject;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * The {@code Encoding} transforms the exploration problem into a set of
 * constraints.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Encoding {

	public static List<Class<?>> order = Arrays.<Class<?>> asList(Resource.class, Link.class, Mapping.class, CR.class,
			CLRR.class);

	public static class VariableComparator implements Comparator<Object>, Serializable {

		private static final long serialVersionUID = 1L;

		protected Integer order(Object obj) {
			int i = 0;
			for (Class<?> clazz : order) {
				if (clazz.isAssignableFrom(obj.getClass())) {
					return i;
				}
				i++;
			}
			return 100;
		}

		@Override
		public int compare(Object o0, Object o1) {
			return order(o0).compareTo(order(o1));
		}

	}

	protected final SpecificationConstraints specificationConstraints;

	@Inject
	public Encoding(SpecificationConstraints specificationConstraints) {
		super();
		this.specificationConstraints = specificationConstraints;
	}

	public List<Constraint> toConstraints(Specification specification) {
		List<Constraint> constraints = new ArrayList<Constraint>();

		Application<Task, Dependency> application = specification.getApplication();
		Architecture<Resource, Link> architecture = specification.getArchitecture();
		Mappings<Task, Resource> mappings = specification.getMappings();
		Routings<Task, Resource, Link> routings = specification.getRoutings();

		// EQ1
		for (Task task : filterProcesses(application)) {
			Constraint constraint = new Constraint("=", 1);
			for (Mapping<Task, Resource> m : mappings.get(task)) {
				constraint.add(p(m));
			}
			constraints.add(constraint);
		}

		// EQ2
		for (Mapping<Task, Resource> m : mappings) {
			Constraint constraint = new Constraint(">=", 0);
			Resource r = m.getTarget();
			constraint.add(p(r));
			constraint.add(-1, p(m));
			constraints.add(constraint);
		}

		// EQ3 + EQ4
		for (Dependency dependency : application.getEdges()) {
			Task p0 = application.getSource(dependency);
			Task p1 = application.getDest(dependency);

			if (isProcess(p0) && isProcess(p1)) {
				for (Mapping<Task, Resource> m0 : mappings.get(p0)) {
					for (Mapping<Task, Resource> m1 : mappings.get(p1)) {
						Resource r0 = m0.getTarget();
						Resource r1 = m1.getTarget();

						Edge l = architecture.findEdge(r0, r1);

						if (l != null) {
							Constraint constraint = new Constraint(">=", -1); // EQ3
							constraint.add(p(l));
							constraint.add(-1, p(m0));
							constraint.add(-1, p(m1));
							constraints.add(constraint);
						} else if (!r0.equals(r1)) {
							Constraint constraint = new Constraint("<=", 1); // EQ4
							constraint.add(p(m0));
							constraint.add(p(m1));
							constraints.add(constraint);
						}
					}
				}
			}
		}

		// EQ5
		for (Link l : architecture.getEdges()) {
			Pair<Resource> pair = architecture.getEndpoints(l);
			Resource r0 = pair.getFirst();
			Resource r1 = pair.getSecond();

			Constraint constraint = new Constraint(">=", 0);
			constraint.add(-2, p(l));
			constraint.add(p(r0));
			constraint.add(p(r1));
			constraints.add(constraint);
		}

		// EQ6
		for (Task c : filterCommunications(application)) {
			Architecture<Resource, Link> routing = routings.get(c);
			for (Resource r : routing) {
				Constraint constraint = new Constraint(">=", 0);
				constraint.add(p(r));
				constraint.add(-1, p(var(c, r)));
				constraints.add(constraint);
			}
		}

		// EQ7
		for (Task c : filterCommunications(application)) {
			Architecture<Resource, Link> routing = routings.get(c);
			for (DirectedLink lrr : getLinks(routing)) {
				Constraint constraint = new Constraint(">=", 0);
				constraint.add(p(lrr.getLink()));
				constraint.add(-1, p(var(c, lrr)));
				constraints.add(constraint);
			}
		}

		// EQ8
		for (Task c : filterCommunications(application)) {
			Architecture<Resource, Link> routing = routings.get(c);
			for (DirectedLink lrr : getLinks(routing)) {
				Resource r0 = lrr.getSource();
				Resource r1 = lrr.getDest();

				Constraint constraint = new Constraint(">=", 0);
				constraint.add(-2, p(var(c, lrr)));
				constraint.add(p(var(c, r0)));
				constraint.add(p(var(c, r1)));
				constraints.add(constraint);
			}
		}

		// EQ9
		for (Task c : filterCommunications(application)) {
			Architecture<Resource, Link> routing = routings.get(c);

			for (Link l : routing.getEdges()) {
				if (routing.getEdgeType(l) == UNDIRECTED) {
					Pair<Resource> endpoints = routing.getEndpoints(l);
					Resource r0 = endpoints.getFirst();
					Resource r1 = endpoints.getSecond();

					Constraint constraint = new Constraint("<=", 1);
					constraint.add(p(var(c, l, r0, r1)));
					constraint.add(p(var(c, l, r1, r0)));
					constraints.add(constraint);
				}
			}
		}

		// EQ10 + EQ11
		for (Task c : filterCommunications(application)) {
			for (Task p : filterProcesses(application.getNeighbors(c))) {
				for (Mapping<Task, Resource> m : mappings.get(p)) {
					Resource r = m.getTarget();

					if (routings.get(c).containsVertex(r)) {
						Constraint constraint = new Constraint(">=", 0); // EQ10
						constraint.add(p(var(c, r)));
						constraint.add(-1, p(m));
						constraints.add(constraint);
					} else {
						Constraint constraint = new Constraint("=", 0); // EQ11
						constraint.add(p(m));
						constraints.add(constraint);
					}
				}
			}
		}

		// EQ12
		for (Task c : filterCommunications(application)) {
			for (Task p : filterProcesses(application.getPredecessors(c))) {
				for (Mapping<Task, Resource> m : mappings.get(p)) {
					Resource r0 = m.getTarget();
					Architecture<Resource, Link> routing = routings.get(c);

					for (DirectedLink lrr : getInLinks(routing, r0)) {
						Constraint constraint = new Constraint("<=", 1);
						constraint.add(p(m));
						constraint.add(p(var(c, lrr)));
						constraints.add(constraint);
					}

				}
			}
		}

		// EQ13
		for (Task c : filterCommunications(application)) {
			Architecture<Resource, Link> routing = routings.get(c);

			for (Resource r0 : routing) {
				Constraint constraint = new Constraint("<=", 1);
				for (DirectedLink lrr : getInLinks(routing, r0)) {
					constraint.add(p(var(c, lrr)));
				}
				constraints.add(constraint);
			}
		}

		// EQ12
		/*
		 * for (Task p : filterProcesses(application)) { for (Task c :
		 * filterCommunications(application.getNeighbors(p))) {
		 * 
		 * Architecture<Resource, Edge> routing = routings.get(c);
		 * 
		 * Constraint constraint = new Constraint(">=", 1); for (Mapping<Task,
		 * Resource> m : mappings.get(p)) { Resource r = m.getTarget(); if
		 * (routing.containsVertex(r)) { constraint.add(p(var(c, r))); } }
		 * constraints.add(constraint); } }
		 */

		// EQ14
		for (Task c : filterCommunications(application)) {
			Architecture<Resource, Link> routing = routings.get(c);

			for (Resource r0 : routing) {
				Constraint constraint = new Constraint(">=", 0);
				constraint.add(-1, p(var(c, r0)));

				for (Task p : filterProcesses(application.getSuccessors(c))) {
					for (Mapping<Task, Resource> m : mappings.get(p, r0)) {
						constraint.add(p(m));
					}
				}
				for (DirectedLink lrr : getOutLinks(routing, r0)) {
					constraint.add(p(var(c, lrr)));
				}
				constraints.add(constraint);
			}
		}

		// EQ15
		for (Task c : filterCommunications(application)) {
			Architecture<Resource, Link> routing = routings.get(c);

			for (Resource r0 : routing) {
				Constraint constraint = new Constraint(">=", 0);
				constraint.add(-1, p(var(c, r0)));

				for (Task p : filterProcesses(application.getPredecessors(c))) {
					for (Mapping<Task, Resource> m : mappings.get(p, r0)) {
						constraint.add(p(m));
					}
				}
				for (DirectedLink lrr : getInLinks(routing, r0)) {
					constraint.add(p(var(c, lrr)));
				}

				constraints.add(constraint);
			}
		}

		boolean isUnicast = false;
		boolean isMulticast1 = true;
		boolean isMulticast2 = false;

		final int Tmax = 6;

		if (isUnicast) {

			// EQ16 - unicast
			for (Task c : filterCommunications(application)) {
				Architecture<Resource, Link> routing = routings.get(c);

				for (Resource r0 : routing) {
					Constraint constraint = new Constraint("<=", 1);
					for (DirectedLink lrr : getOutLinks(routing, r0)) {
						constraint.add(p(var(c, lrr)));
					}
					constraints.add(constraint);
				}
			}

			// EQ17 - unicast
			for (Task c : filterCommunications(application)) {
				assert (application.getPredecessorCount(c) == 1);
				assert (application.getSuccessorCount(c) == 1);

				Task p0 = application.getPredecessors(c).iterator().next();
				Task p1 = application.getSuccessors(c).iterator().next();

				Architecture<Resource, Link> routing = routings.get(c);

				for (Resource r0 : routing) {
					Constraint constraint = new Constraint("=", 0);

					for (DirectedLink lrr : getOutLinks(routing, r0)) {
						constraint.add(1, p(var(c, lrr)));
					}
					for (DirectedLink lrr : getInLinks(routing, r0)) {
						constraint.add(-1, p(var(c, lrr)));
					}
					for (Mapping<Task, Resource> m : mappings.get(p0, r0)) {
						constraint.add(-1, p(m));
					}
					for (Mapping<Task, Resource> m : mappings.get(p1, r0)) {
						constraint.add(1, p(m));
					}
					constraints.add(constraint);
				}
			}

		}
		if (isMulticast1) {
			// EQ18
			for (Task c : filterCommunications(application)) {
				for (Task p0 : filterProcesses(application.getPredecessors(c))) {
					for (Task p1 : filterProcesses(application.getSuccessors(c))) {
						for (Mapping<Task, Resource> m : mappings.get(p0)) {
							Resource r0 = m.getTarget();
							Architecture<Resource, Link> routing = routings.get(c);

							for (DirectedLink lrr : getInLinks(routing, r0)) {
								Constraint constraint = new Constraint("<=", 1);
								constraint.add(p(m));
								constraint.add(p(var(c, lrr, p1)));
								constraints.add(constraint);
							}
						}
					}
				}
			}

			// EQ19
			for (Task c : filterCommunications(application)) {
				for (Task p : filterProcesses(application.getSuccessors(c))) {
					Architecture<Resource, Link> routing = routings.get(c);

					for (Resource r0 : routing) {
						Constraint constraint = new Constraint("<=", 1);
						for (DirectedLink lrr : getInLinks(routing, r0)) {
							constraint.add(p(var(c, lrr, p)));
						}
						constraints.add(constraint);
					}
				}
			}

			// EQ20
			for (Task c : filterCommunications(application)) {
				for (Task p : filterProcesses(application.getSuccessors(c))) {
					Architecture<Resource, Link> routing = routings.get(c);

					for (Resource r0 : routing) {
						Constraint constraint = new Constraint("<=", 1);
						for (Link l : routing.getOutEdges(r0)) {
							Resource r1 = routing.getOpposite(r0, l);
							constraint.add(p(var(c, l, r0, r1, p)));
						}
						constraints.add(constraint);
					}
				}
			}

			// EQ21
			for (Task c : filterCommunications(application)) {
				assert (application.getPredecessorCount(c) == 1);
				Task p0 = application.getPredecessors(c).iterator().next();
				for (Task p1 : filterProcesses(application.getSuccessors(c))) {
					Architecture<Resource, Link> routing = routings.get(c);

					for (Resource r0 : routing) {
						Constraint constraint = new Constraint("=", 0);

						for (DirectedLink lrr : getOutLinks(routing, r0)) {
							constraint.add(1, p(var(c, lrr, p1)));
						}
						for (DirectedLink lrr : getInLinks(routing, r0)) {
							constraint.add(-1, p(var(c, lrr, p1)));
						}
						for (Mapping<Task, Resource> m : mappings.get(p0, r0)) {
							constraint.add(-1, p(m));
						}
						for (Mapping<Task, Resource> m : mappings.get(p1, r0)) {
							constraint.add(1, p(m));
						}
						constraints.add(constraint);
					}
				}
			}

			// EQ22
			for (Task c : filterCommunications(application)) {
				Architecture<Resource, Link> routing = routings.get(c);
				for (DirectedLink lrr : getLinks(routing)) {
					Constraint constraint = new Constraint(">=", 0);
					constraint.add(-1, p(var(c, lrr)));
					for (Task p : filterProcesses(application.getSuccessors(c))) {
						constraint.add(p(var(c, lrr, p)));
					}
					constraints.add(constraint);
				}
			}

			// EQ23
			for (Task c : filterCommunications(application)) {
				for (Task p : filterProcesses(application.getSuccessors(c))) {
					Architecture<Resource, Link> routing = routings.get(c);
					for (DirectedLink lrr : getLinks(routing)) {
						Constraint constraint = new Constraint(">=", 0);
						constraint.add(-1, p(var(c, lrr, p)));
						constraint.add(p(var(c, lrr)));
						constraints.add(constraint);
					}
				}
			}
		}

		if (isMulticast2) {

			// EQ24
			for (Task c : filterCommunications(application)) {
				Task p = application.getPredecessors(c).iterator().next();
				Architecture<Resource, Link> routing = routings.get(c);

				for (DirectedLink lrr : getLinks(routing)) {
					Constraint constraint = new Constraint(">=", 0);
					constraint.add(-1, p(var(c, lrr, 1)));
					Resource r0 = lrr.getSource();
					for (Mapping<Task, Resource> m : mappings.get(p, r0)) {
						constraint.add(p(m));
					}
					constraints.add(constraint);
				}
			}

			// EQ25 TODO (redundant)
			for (Task c : filterCommunications(application)) {
				Task p = application.getPredecessors(c).iterator().next();
				Architecture<Resource, Link> routing = routings.get(c);

				List<Resource> rs = new ArrayList<Resource>(mappings.getTargets(p));

				for (int i = 0; i < rs.size(); i++) {
					for (int j = i + 1; j < rs.size(); j++) {
						Resource r0 = rs.get(i);
						Resource r1 = rs.get(j);

						for (DirectedLink lrr0 : getOutLinks(routing, r0)) {
							for (DirectedLink lrr1 : getOutLinks(routing, r1)) {
								Constraint constraint = new Constraint("<=", 1);
								constraint.add(p(var(c, lrr0, 1)));
								constraint.add(p(var(c, lrr1, 1)));
								constraints.add(constraint);
							}
						}
					}
				}
			}

			// EQ26
			for (Task c : filterCommunications(application)) {
				Architecture<Resource, Link> routing = routings.get(c);

				for (DirectedLink lrr0 : getLinks(routing)) {
					for (int t = 2; t <= Tmax; t++) {

						Constraint constraint = new Constraint(">=", 0);
						constraint.add(-1, p(var(c, lrr0, t)));
						for (DirectedLink lrr1 : getInLinks(routing, lrr0.getSource())) {
							constraint.add(p(var(c, lrr1, t - 1)));
						}
						constraints.add(constraint);
					}
				}
			}

			// EQ27
			for (Task c : filterCommunications(application)) {
				Architecture<Resource, Link> routing = routings.get(c);

				for (DirectedLink lrr : getLinks(routing)) {
					Constraint constraint = new Constraint("<=", 1);
					for (int t = 1; t <= Tmax; t++) {
						constraint.add(p(var(c, lrr, t)));
					}
					constraints.add(constraint);
				}
			}

			// EQ28
			for (Task c : filterCommunications(application)) {
				Architecture<Resource, Link> routing = routings.get(c);

				for (DirectedLink lrr : getLinks(routing)) {
					Constraint constraint = new Constraint(">=", 0);
					constraint.add(-1, p(var(c, lrr)));

					for (int t = 1; t <= Tmax; t++) {
						constraint.add(p(var(c, lrr, t)));
					}
					constraints.add(constraint);
				}
			}

			// EQ29
			for (Task c : filterCommunications(application)) {
				Architecture<Resource, Link> routing = routings.get(c);

				for (DirectedLink lrr : getLinks(routing)) {
					for (int t = 1; t <= Tmax; t++) {
						Constraint constraint = new Constraint(">=", 0);
						constraint.add(-1, p(var(c, lrr, t)));
						constraint.add(p(var(c, lrr)));
						constraints.add(constraint);
					}
				}
			}
		}

		// EQ30 (redundant - resource constraints)
		for (Resource r : architecture) {
			Constraint constraint = new Constraint(">=", 0);
			constraint.add(-1, p(r));
			for (Mapping<Task, Resource> m : mappings.get(r)) {
				constraint.add(p(m));
			}
			for (Task c : filterCommunications(application)) {
				Architecture<Resource, Link> routing = routings.get(c);
				if (routing.containsVertex(r)) {
					constraint.add(p(var(c, r)));
				}
			}
			constraints.add(constraint);
		}

		specificationConstraints.doEncoding(constraints);

		return constraints;
	}

}
