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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.encoding.old;

import static edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED;
import static net.sf.opendse.model.Models.filterCommunications;
import static net.sf.opendse.model.Models.filterProcesses;
import static net.sf.opendse.model.Models.getInLinks;
import static net.sf.opendse.model.Models.getLinks;
import static net.sf.opendse.model.Models.getOutLinks;
import static net.sf.opendse.model.Models.isProcess;
import static net.sf.opendse.encoding.old.variables.Variables.p;
import static net.sf.opendse.encoding.old.variables.Variables.var;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.opendse.encoding.ImplementationEncoding;
import net.sf.opendse.encoding.constraints.SpecificationConstraints;
import net.sf.opendse.encoding.old.variables.CLRR;
import net.sf.opendse.encoding.old.variables.CR;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

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
public class Encoding implements ImplementationEncoding{

	public static List<Class<?>> order = Arrays.<Class<?>>asList(Resource.class, Link.class, Mapping.class, CR.class,
			CLRR.class);

	public enum RoutingEncoding {
		HOP, FLOW;
	}

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
	protected final RoutingEncoding routingEncoding;

	@Inject
	public Encoding(SpecificationConstraints specificationConstraints, RoutingEncoding routingEncoding) {
		super();
		this.specificationConstraints = specificationConstraints;
		this.routingEncoding = routingEncoding;
	}

	/**
	 * For each process task in the application graph, exactly one mapping edge
	 * has to be activated in the implementation.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ1(List<Constraint> constraints, Specification specification) {
		for (Task task : filterProcesses(specification.getApplication())) {
			Constraint constraint = new Constraint("=", 1);
			for (Mapping<Task, Resource> m : specification.getMappings().get(task)) {
				constraint.add(p(m));
			}
			constraints.add(constraint);
		}
	}

	/**
	 * A resource which is the mapping target of an activated mapping edge also
	 * has to be activated in a valid implementation.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ2(List<Constraint> constraints, Specification specification) {
		for (Mapping<Task, Resource> m : specification.getMappings()) {
			Constraint constraint = new Constraint(">=", 0);
			Resource r = m.getTarget();
			constraint.add(p(r));
			constraint.add(-1, p(m));
			constraints.add(constraint);
		}
	}

	/**
	 * This method addresses the case when there is a dependancy between two
	 * process tasks.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ3EQ4(List<Constraint> constraints, Specification specification) {
		for (Dependency dependency : specification.getApplication().getEdges()) {
			Task p0 = specification.getApplication().getSource(dependency);
			Task p1 = specification.getApplication().getDest(dependency);

			if (isProcess(p0) && isProcess(p1)) {
				// both tasks are processes
				for (Mapping<Task, Resource> m0 : specification.getMappings().get(p0)) {
					for (Mapping<Task, Resource> m1 : specification.getMappings().get(p1)) {
						Resource r0 = m0.getTarget();
						Resource r1 = m1.getTarget();

						Edge l = specification.getArchitecture().findEdge(r0, r1);

						if (l != null) {
							// case where there is a link connecting the mapping
							// targets of both processes
							Constraint constraint = new Constraint(">=", -1); // EQ3
							constraint.add(p(l));
							constraint.add(-1, p(m0));
							constraint.add(-1, p(m1));
							constraints.add(constraint);
						} else if (!r0.equals(r1)) {
							// the processes are mapped to two different
							// resources that are not connected by a link => at
							// most one of the considered mapping edges may be
							// activated at the same time
							Constraint constraint = new Constraint("<=", 1); // EQ4
							constraint.add(p(m0));
							constraint.add(p(m1));
							constraints.add(constraint);
						}
					}
				}
			}
		}
	}

	/**
	 * A link is only activated if both end points of the link are activated as
	 * well.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ5(List<Constraint> constraints, Specification specification) {
		for (Link l : specification.getArchitecture().getEdges()) {
			Pair<Resource> pair = specification.getArchitecture().getEndpoints(l);
			Resource r0 = pair.getFirst();
			Resource r1 = pair.getSecond();

			Constraint constraint = new Constraint(">=", 0);
			constraint.add(-2, p(l));
			constraint.add(p(r0));
			constraint.add(p(r1));
			constraints.add(constraint);
		}
	}

	/**
	 * A resource is only active in the routing of a communication task if the
	 * corresponding CR variable is active.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ6(List<Constraint> constraints, Specification specification) {
		for (Task c : filterCommunications(specification.getApplication())) {
			Architecture<Resource, Link> routing = specification.getRoutings().get(c);
			for (Resource r : routing) {
				Constraint constraint = new Constraint(">=", 0);
				constraint.add(p(r));
				constraint.add(-1, p(var(c, r)));
				constraints.add(constraint);
			}
		}
	}

	/**
	 * If the routing variable of a directed link is activated, this link has to
	 * be activated in the architecture.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ7(List<Constraint> constraints, Specification specification) {
		for (Task c : filterCommunications(specification.getApplication())) {
			Architecture<Resource, Link> routing = specification.getRoutings().get(c);
			for (DirectedLink lrr : getLinks(routing)) {
				Constraint constraint = new Constraint(">=", 0);
				constraint.add(p(lrr.getLink()));
				constraint.add(-1, p(var(c, lrr)));
				constraints.add(constraint);
			}
		}
	}

	/**
	 * A directed link can only be activated in the architecture if both end
	 * point-resources are activated as well.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ8(List<Constraint> constraints, Specification specification) {
		for (Task c : filterCommunications(specification.getApplication())) {
			Architecture<Resource, Link> routing = specification.getRoutings().get(c);
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
	}

	/**
	 * An architecture link may only be part of one directed link in the
	 * routings (based on the link connected resource A to resource B, either
	 * the link from A to B or the link fom B to A may be taken into the
	 * routing, but not both)
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ9(List<Constraint> constraints, Specification specification) {
		for (Task c : filterCommunications(specification.getApplication())) {
			Architecture<Resource, Link> routing = specification.getRoutings().get(c);

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
	}

	/**
	 * 
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ10EQ11(List<Constraint> constraints, Specification specification) {
		// iterate over all communications
		for (Task c : filterCommunications(specification.getApplication())) {
			// iterate over all flows of the current communication
			for (Task p : filterProcesses(specification.getApplication().getNeighbors(c))) {
				// iterate over all possible mapping targets of the current
				// communication flow
				for (Mapping<Task, Resource> m : specification.getMappings().get(p)) {
					Resource r = m.getTarget();
					if (specification.getRoutings().get(c).containsVertex(r)) {
						// case where the resource is part of the routing graph
						// : if the mapping of the comm flow on the resource is
						// activated, the communication has to be routed over
						// the resource
						Constraint constraint = new Constraint(">=", 0); // EQ10
						constraint.add(p(var(c, r)));
						constraint.add(-1, p(m));
						constraints.add(constraint);
					} else {
						// case where the resource is not in the routing graph :
						// the mapping must not be activated
						Constraint constraint = new Constraint("=", 0); // EQ11
						constraint.add(p(m));
						constraints.add(constraint);
					}
				}
			}
		}
	}

	/**
	 * If the predecessor of a communication is mapped to a resource, this
	 * resource must not have any in-links in the routing graph of the
	 * communication.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ12(List<Constraint> constraints, Specification specification) {
		for (Task c : filterCommunications(specification.getApplication())) {
			for (Task p : filterProcesses(specification.getApplication().getPredecessors(c))) {
				for (Mapping<Task, Resource> m : specification.getMappings().get(p)) {
					Resource r0 = m.getTarget();
					Architecture<Resource, Link> routing = specification.getRoutings().get(c);

					for (DirectedLink lrr : getInLinks(routing, r0)) {
						Constraint constraint = new Constraint("<=", 1);
						constraint.add(p(m));
						constraint.add(p(var(c, lrr)));
						constraints.add(constraint);
					}

				}
			}
		}
	}

	/**
	 * Any resource in the routing graph has no more than one in-link.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ13(List<Constraint> constraints, Specification specification) {
		for (Task c : filterCommunications(specification.getApplication())) {
			Architecture<Resource, Link> routing = specification.getRoutings().get(c);

			for (Resource r0 : routing) {
				Constraint constraint = new Constraint("<=", 1);
				for (DirectedLink lrr : getInLinks(routing, r0)) {
					constraint.add(p(var(c, lrr)));
				}
				constraints.add(constraint);
			}
		}
	}

	/**
	 * If a resource is activated in the routing of a communication, it must be
	 * the mapping target of at least one of the successors of the communication
	 * and/or have at least one activated out-link.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ14(List<Constraint> constraints, Specification specification) {
		for (Task c : filterCommunications(specification.getApplication())) {
			Architecture<Resource, Link> routing = specification.getRoutings().get(c);

			for (Resource r0 : routing) {
				Constraint constraint = new Constraint(">=", 0);
				constraint.add(-1, p(var(c, r0)));

				for (Task p : filterProcesses(specification.getApplication().getSuccessors(c))) {
					for (Mapping<Task, Resource> m : specification.getMappings().get(p, r0)) {
						constraint.add(p(m));
					}
				}
				for (DirectedLink lrr : getOutLinks(routing, r0)) {
					constraint.add(p(var(c, lrr)));
				}
				constraints.add(constraint);
			}
		}
	}

	/**
	 * If a resource is activated in the routing of a communication, it must be
	 * the maping target of at least one of the predecessors of the
	 * communication and/or have at least one activated in-link.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ15(List<Constraint> constraints, Specification specification) {
		for (Task c : filterCommunications(specification.getApplication())) {
			Architecture<Resource, Link> routing = specification.getRoutings().get(c);

			for (Resource r0 : routing) {
				Constraint constraint = new Constraint(">=", 0);
				constraint.add(-1, p(var(c, r0)));

				for (Task p : filterProcesses(specification.getApplication().getPredecessors(c))) {
					for (Mapping<Task, Resource> m : specification.getMappings().get(p, r0)) {
						constraint.add(p(m));
					}
				}
				for (DirectedLink lrr : getInLinks(routing, r0)) {
					constraint.add(p(var(c, lrr)));
				}

				constraints.add(constraint);
			}
		}
	}

	/**
	 * ONLY USED FOR ENCODING OF UNICAST MESSAGES
	 * 
	 * Each resource must not have more than one out-link in the routign graph.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ16(List<Constraint> constraints, Specification specification) {
		for (Task c : filterCommunications(specification.getApplication())) {
			Architecture<Resource, Link> routing = specification.getRoutings().get(c);

			for (Resource r0 : routing) {
				Constraint constraint = new Constraint("<=", 1);
				for (DirectedLink lrr : getOutLinks(routing, r0)) {
					constraint.add(p(var(c, lrr)));
				}
				constraints.add(constraint);
			}
		}
	}

	/**
	 * ONLY USED FOR ENCODING OF UNICAST MESSAGES
	 * 
	 * A resource in the unicast routing is either a) the mapping target of the
	 * communication predecessor and has one out-link, b) the mapping target of
	 * the communication successor and has one in-link, c) a resource with one
	 * in- and one out-link or d) the mapping target of both the pre- and the
	 * successor of the communication.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ17(List<Constraint> constraints, Specification specification) {
		final Application<Task, Dependency> application = specification.getApplication();

		for (Task c : filterCommunications(application)) {
			assert (application.getPredecessorCount(c) == 1);
			assert (application.getSuccessorCount(c) == 1);

			Task p0 = application.getPredecessors(c).iterator().next();
			Task p1 = application.getSuccessors(c).iterator().next();

			Architecture<Resource, Link> routing = specification.getRoutings().get(c);

			for (Resource r0 : routing) {
				Constraint constraint = new Constraint("=", 0);

				for (DirectedLink lrr : getOutLinks(routing, r0)) {
					constraint.add(1, p(var(c, lrr)));
				}
				for (DirectedLink lrr : getInLinks(routing, r0)) {
					constraint.add(-1, p(var(c, lrr)));
				}
				for (Mapping<Task, Resource> m : specification.getMappings().get(p0, r0)) {
					constraint.add(-1, p(m));
				}
				for (Mapping<Task, Resource> m : specification.getMappings().get(p1, r0)) {
					constraint.add(1, p(m));
				}
				constraints.add(constraint);
			}
		}
	}

	/**
	 * A resource in the routing graph of a communication must not have
	 * activated in-links if it is the mapping target of the predecessor of the
	 * communication.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ18(List<Constraint> constraints, Specification specification) {
		final Application<Task, Dependency> application = specification.getApplication();

		for (Task c : filterCommunications(application)) {
			for (Task p0 : filterProcesses(application.getPredecessors(c))) {
				for (Task p1 : filterProcesses(application.getSuccessors(c))) {
					for (Mapping<Task, Resource> m : specification.getMappings().get(p0)) {
						Resource r0 = m.getTarget();
						Architecture<Resource, Link> routing = specification.getRoutings().get(c);

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
	}

	/**
	 * A resource in the routing graph of a communication flow must not have
	 * more than one activated in-link.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ19(List<Constraint> constraints, Specification specification) {
		final Application<Task, Dependency> application = specification.getApplication();

		for (Task c : filterCommunications(application)) {
			for (Task p : filterProcesses(application.getSuccessors(c))) {
				Architecture<Resource, Link> routing = specification.getRoutings().get(c);

				for (Resource r0 : routing) {
					Constraint constraint = new Constraint("<=", 1);
					for (DirectedLink lrr : getInLinks(routing, r0)) {
						constraint.add(p(var(c, lrr, p)));
					}
					constraints.add(constraint);
				}
			}
		}
	}

	/**
	 * A resource in the routing graph of a communication flow must not have
	 * more than one activated out-link.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ20(List<Constraint> constraints, Specification specification) {
		final Application<Task, Dependency> application = specification.getApplication();

		for (Task c : filterCommunications(application)) {
			for (Task p : filterProcesses(application.getSuccessors(c))) {
				Architecture<Resource, Link> routing = specification.getRoutings().get(c);

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
	}

	/**
	 * A resource in the routing graph of a communication flow is either a) the
	 * mapping target of the communication predecessor of the communication and
	 * has one out-link, b) has one in- and one out-link, c) has one in-link and
	 * is the mapping target of the communication successor or d) is the mapping
	 * target of both the communication pre- and the communication successor
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ21(List<Constraint> constraints, Specification specification) {
		final Application<Task, Dependency> application = specification.getApplication();

		for (Task c : filterCommunications(application)) {
			assert (application.getPredecessorCount(c) == 1);
			Task p0 = application.getPredecessors(c).iterator().next();
			for (Task p1 : filterProcesses(application.getSuccessors(c))) {
				Architecture<Resource, Link> routing = specification.getRoutings().get(c);

				for (Resource r0 : routing) {
					Constraint constraint = new Constraint("=", 0);

					for (DirectedLink lrr : getOutLinks(routing, r0)) {
						constraint.add(1, p(var(c, lrr, p1)));
					}
					for (DirectedLink lrr : getInLinks(routing, r0)) {
						constraint.add(-1, p(var(c, lrr, p1)));
					}
					for (Mapping<Task, Resource> m : specification.getMappings().get(p0, r0)) {
						constraint.add(-1, p(m));
					}
					for (Mapping<Task, Resource> m : specification.getMappings().get(p1, r0)) {
						constraint.add(1, p(m));
					}
					constraints.add(constraint);
				}
			}
		}
	}

	/**
	 * A resource is only activated in the routing graph of a communication if
	 * at least one communication flow is routed over this resource.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ22(List<Constraint> constraints, Specification specification) {
		final Application<Task, Dependency> application = specification.getApplication();
		for (Task c : filterCommunications(application)) {
			Architecture<Resource, Link> routing = specification.getRoutings().get(c);
			for (DirectedLink lrr : getLinks(routing)) {
				Constraint constraint = new Constraint(">=", 0);
				constraint.add(-1, p(var(c, lrr)));
				for (Task p : filterProcesses(application.getSuccessors(c))) {
					constraint.add(p(var(c, lrr, p)));
				}
				constraints.add(constraint);
			}
		}
	}

	/**
	 * A resource may only be used for the routing of a communication flow if it
	 * is also activated in the routing of the communication.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ23(List<Constraint> constraints, Specification specification) {
		final Application<Task, Dependency> application = specification.getApplication();
		for (Task c : filterCommunications(application)) {
			for (Task p : filterProcesses(application.getSuccessors(c))) {
				Architecture<Resource, Link> routing = specification.getRoutings().get(c);
				for (DirectedLink lrr : getLinks(routing)) {
					Constraint constraint = new Constraint(">=", 0);
					constraint.add(-1, p(var(c, lrr, p)));
					constraint.add(p(var(c, lrr)));
					constraints.add(constraint);
				}
			}
		}
	}

	/**
	 * A resource may only be activated in the routing graph of a communication
	 * if it is activated in the architecture graph.
	 * 
	 * @param constraints
	 * @param specification
	 */
	protected void EQ30(List<Constraint> constraints, Specification specification) {
		final Application<Task, Dependency> application = specification.getApplication();
		final Architecture<Resource, Link> architecture = specification.getArchitecture();
		final Mappings<Task, Resource> mappings = specification.getMappings();
		final Routings<Task, Resource, Link> routings = specification.getRoutings();

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
	}

	@Override
	public Set<Constraint> toConstraints(Specification specification) {
		List<Constraint> constraints = new ArrayList<Constraint>();

		Application<Task, Dependency> application = specification.getApplication();
		Mappings<Task, Resource> mappings = specification.getMappings();
		Routings<Task, Resource, Link> routings = specification.getRoutings();

		EQ1(constraints, specification);
		EQ2(constraints, specification);
		EQ3EQ4(constraints, specification);
		EQ5(constraints, specification);
		EQ6(constraints, specification);

		EQ7(constraints, specification);
		EQ8(constraints, specification);
		EQ9(constraints, specification);
		EQ10EQ11(constraints, specification);
		EQ12(constraints, specification);
		EQ13(constraints, specification);
		EQ14(constraints, specification);
		EQ15(constraints, specification);

		boolean isUnicast = false;

		final int Tmax = 10;

		if (isUnicast) {
			EQ16(constraints, specification);
			EQ17(constraints, specification);
		}
		if (routingEncoding.equals(RoutingEncoding.FLOW)) {
			EQ18(constraints, specification);
			EQ19(constraints, specification);
			EQ20(constraints, specification);
			EQ21(constraints, specification);
			EQ22(constraints, specification);
			EQ23(constraints, specification);
		}

		if (routingEncoding.equals(RoutingEncoding.HOP)) {

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

		EQ30(constraints, specification);

		specificationConstraints.doEncoding(constraints);

		return new HashSet<Constraint>(constraints);
	}

}
