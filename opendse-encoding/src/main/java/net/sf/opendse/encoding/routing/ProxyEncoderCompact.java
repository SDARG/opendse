package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.constraints.Constraints;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;

/**
 * Encodes the activation of the preprocessed routes based on the chosen
 * mapping, that is, encodes the activation of all links that do not offer any
 * routing variety.
 * 
 * @author Fedor Smirnov
 *
 */
public class ProxyEncoderCompact implements ProxyEncoder {

	@Override
	public Set<Constraint> toConstraints(Task communication, Architecture<Resource, Link> routing,
			Set<MappingVariable> mappingVariables, Set<ApplicationVariable> applicationVariables) {
		Set<Constraint> result = new HashSet<Constraint>();
		// iterate all resources inside proxy areas
		for (Resource res : routing) {
			Set<Link> upLinks = new HashSet<Link>();
			Set<Link> downLinks = new HashSet<Link>();
			findUpDownLinks(res, routing, upLinks, downLinks);
			Set<Variable> srcMappings = new HashSet<Variable>();
			Set<Variable> destMappings = new HashSet<Variable>();
			getSrcDestMappings(mappingVariables, applicationVariables, communication, res, result, srcMappings,
					destMappings);
			if (insideProxyArea(res, routing)) {
				result.addAll(makePushPullConstraints(res, communication, upLinks, downLinks, srcMappings, destMappings,
						routing));
				result.add(makeForbidDownInLinksConstraint(res, communication, upLinks, downLinks, srcMappings,
						destMappings, routing));
				result.add(makeForbidDownOutLinksConstraint(res, communication, upLinks, downLinks, srcMappings,
						destMappings, routing));
			}
		}
		return result;
	}

	/**
	 * Fills the sets of {@link Variable}s that encode the source and the
	 * destination mappings relevant for the given communication that are mapped on
	 * the given resource.
	 * 
	 * @param mappingVariables
	 *            the set of variables encoding the mappings of all tasks
	 * @param applicationVariables
	 *            the set of variables encoding the activation of the application
	 *            elements
	 * @param communication
	 *            the communication that is bein routed
	 * @param res
	 *            the given resource
	 * @param constraints
	 *            the set of constraints (it will be extended by this method)
	 */
	protected void getSrcDestMappings(Set<MappingVariable> mappingVariables,
			Set<ApplicationVariable> applicationVariables, Task communication, Resource res,
			Set<Constraint> constraints, Set<Variable> srcMappings, Set<Variable> destMappings) {
		for (ApplicationVariable applVar : applicationVariables) {
			if (applVar instanceof DTT) {
				DTT dttVar = (DTT) applVar;
				if (dttVar.getDestinationTask().equals(communication)) {
					// edge to the communication
					Task srcTask = dttVar.getSourceTask();
					for (MappingVariable mapVar : mappingVariables) {
						if (mapVar instanceof M) {
							M mVar = (M) mapVar;
							Mapping<Task, Resource> mapping = mVar.getMapping();
							if (mapping.getSource().equals(srcTask) && mapping.getTarget().equals(res)) {
								Set<Variable> args = new HashSet<Variable>();
								args.add(mVar);
								args.add(dttVar);
								Variable srcVar = Constraints.generateAndVariable(args, constraints);
								srcMappings.add(srcVar);
							}
						}
					}
				} else if (dttVar.getSourceTask().equals(communication)) {
					// edge from the communication
					Task destTask = dttVar.getDestinationTask();
					for (MappingVariable mapVar : mappingVariables) {
						if (mapVar instanceof M) {
							M mVar = (M) mapVar;
							Mapping<Task, Resource> mapping = mVar.getMapping();
							if (mapping.getSource().equals(destTask) && mapping.getTarget().equals(res)) {
								Set<Variable> args = new HashSet<Variable>();
								args.add(mVar);
								args.add(dttVar);
								Variable destVar = Constraints.generateAndVariable(args, constraints);
								destMappings.add(destVar);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Fills the given sets with the uplinks and the downlinks of the given
	 * resource.
	 * 
	 * @param res
	 *            the given resource
	 * @param routing
	 *            the routing graph of the communication that is being routed
	 * @param upLinks
	 *            empty set for the uplinks
	 * @param downLinks
	 *            empty set for the downlinks
	 */
	protected void findUpDownLinks(Resource res, Architecture<Resource, Link> routing, Set<Link> upLinks,
			Set<Link> downLinks) {
		int ownProxyDistance = ResourcePropertyService.getProxyDistance(res);
		for (Link incidentLink : routing.getIncidentEdges(res)) {
			if (ArchitectureElementPropertyService.getOffersRoutingVariety(incidentLink)) {
				// uplink of the proxy of the area
				upLinks.add(incidentLink);
			} else {
				Resource opposite = routing.getOpposite(res, incidentLink);
				int otherDistance = ResourcePropertyService.getProxyDistance(opposite);
				Set<Link> targetSet = otherDistance > ownProxyDistance ? downLinks : upLinks;
				targetSet.add(incidentLink);
			}
		}
	}

	/**
	 * Returns {@code true} if the resource is inside a proxy area, returns
	 * {@code false} otherwise.
	 * 
	 * @param res
	 *            the resource in question
	 * @param routing
	 *            the routing graph of the communication
	 * @return {@code true} if the resource is inside a proxy area, returns
	 *         {@code false} otherwise
	 */
	protected boolean insideProxyArea(Resource res, Architecture<Resource, Link> routing) {
		boolean result = false;
		for (Link link : routing.getIncidentEdges(res)) {
			result |= !ArchitectureElementPropertyService.getOffersRoutingVariety(link);
		}
		return result;
	}

	/**
	 * Formulates the constraint stating that in-links coming from the children of a
	 * node may only be active if a) the node is the binding target of a destination
	 * task, or b) the node has activated out-links (either up or down).
	 * 
	 * N(l) * (M_d + sum(L_o) + sum(l_o)) - sum(l_in) >= 0
	 * 
	 * @param node
	 *            the current node
	 * @param communication
	 *            the communication that is being routed
	 * @param upLink
	 *            the incident link leading into the direction of the proxy
	 * @param downLinks
	 *            the set of incident links leading away from the proxy
	 * @param srcMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of the source task of the communication
	 * @param destMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of a destination task of the communication
	 * @param routing
	 *            the entire routing graph for the communication at hand
	 * @return the constraint stating that the node is a pull node if it a) has pull
	 *         nodes among its children or is the binding target of the
	 *         communication destination AND b) has no push nodes among its children
	 *         and is not the binding target of a communication source task
	 */
	protected Constraint makeForbidDownInLinksConstraint(Resource node, Task communication, Set<Link> upLinks,
			Set<Link> downLinks, Set<Variable> srcMappings, Set<Variable> destMappings,
			Architecture<Resource, Link> routing) {
		return makeForbidDownInOutLinksConstraint(node, communication, upLinks, downLinks, srcMappings, destMappings,
				routing, true);
	}

	/**
	 * Formulates the constraint stating that out-links coming from the children of
	 * a node may only be active if a) the node is the binding target of a source
	 * task, or b) the node has activated in-links (either up or down).
	 * 
	 * N(l) * (M_s + sum(L_i) + sum(l_i)) - sum(l_o) >= 0
	 * 
	 * @param node
	 *            the current node
	 * @param communication
	 *            the communication that is being routed
	 * @param upLink
	 *            the incident link leading into the direction of the proxy
	 * @param downLinks
	 *            the set of incident links leading away from the proxy
	 * @param srcMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of the source task of the communication
	 * @param destMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of a destination task of the communication
	 * @param routing
	 *            the entire routing graph for the communication at hand
	 * @return the constraint stating that the node is a pull node if it a) has pull
	 *         nodes among its children or is the binding target of the
	 *         communication destination AND b) has no push nodes among its children
	 *         and is not the binding target of a communication source task
	 */
	protected Constraint makeForbidDownOutLinksConstraint(Resource node, Task communication, Set<Link> upLinks,
			Set<Link> downLinks, Set<Variable> srcMappings, Set<Variable> destMappings,
			Architecture<Resource, Link> routing) {
		return makeForbidDownInOutLinksConstraint(node, communication, upLinks, downLinks, srcMappings, destMappings,
				routing, false);
	}

	protected Constraint makeForbidDownInOutLinksConstraint(Resource node, Task communication, Set<Link> upLinks,
			Set<Link> downLinks, Set<Variable> srcMappings, Set<Variable> destMappings,
			Architecture<Resource, Link> routing, boolean inLinks) {
		Constraint result = new Constraint(Operator.GE, 0);
		int coefficient = downLinks.size();
		// M_d
		Set<Variable> mappings = inLinks ? destMappings : srcMappings;
		for (Variable mapping : mappings) {
			result.add(coefficient, Variables.p(mapping));
		}
		// L_o
		for (Link upLink : upLinks) {
			DirectedLink dirLink = inLinks ? new DirectedLink(upLink, node, routing.getOpposite(node, upLink))
					: new DirectedLink(upLink, routing.getOpposite(node, upLink), node);
			result.add(coefficient, Variables.p(Variables.varCLRR(communication, dirLink)));
		}
		// l_o
		for (Link downLink : downLinks) {
			DirectedLink dirLink = inLinks ? new DirectedLink(downLink, node, routing.getOpposite(node, downLink))
					: new DirectedLink(downLink, routing.getOpposite(node, downLink), node);
			result.add(coefficient, Variables.p(Variables.varCLRR(communication, dirLink)));
		}
		// - l_in
		for (Link downLink : downLinks) {
			DirectedLink dirLink = inLinks ? new DirectedLink(downLink, routing.getOpposite(node, downLink), node)
					: new DirectedLink(downLink, node, routing.getOpposite(node, downLink));
			result.add(-1, Variables.p(Variables.varCLRR(communication, dirLink)));
		}
		return result;
	}

	/**
	 * Formulates the constraints that determine whether the current node is a push
	 * node, that is whether its uplink is an activated out edge.
	 * 
	 * @param node
	 *            the current resource
	 * @param communication
	 *            the communication that is being routed
	 * @param upLink
	 *            the incident link leading into the direction of the proxy
	 * @param downLinks
	 *            the set of incident links leading away from the proxy
	 * @param srcMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of the source task of the communication
	 * @param destMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of a destination task of the communication
	 * @param routing
	 *            the entire routing graph for the communication at hand
	 * @return the constraints that determine whether the current node is a push
	 *         node, that is whether its uplink is an activated out edge
	 */
	protected Set<Constraint> makePushPullConstraints(Resource node, Task communication, Set<Link> upLinks,
			Set<Link> downLinks, Set<Variable> srcMappings, Set<Variable> destMappings,
			Architecture<Resource, Link> routing) {
		Set<Constraint> result = new HashSet<Constraint>();
		result.add(makeNoPushConstraint(node, communication, upLinks, downLinks, srcMappings, routing));
		result.add(makePushConstraint(node, communication, upLinks, downLinks, srcMappings, destMappings, routing));
		result.add(makeNoPullConstraint(node, communication, upLinks, downLinks, destMappings, routing));
		result.add(makePullConstraint(node, communication, upLinks, downLinks, srcMappings, destMappings, routing));
		return result;
	}

	/**
	 * Generates the constraint stating that the node is a pull node if it a) has
	 * pull nodes among its children or is the binding target of the communication
	 * destination AND b) has no push nodes among its children and is not the
	 * binding target of a communication source task.
	 * 
	 * sum(l_o) + sum(M_d) - N(l_o, M_d) x (L_i + sum(l_i) + sum(M_s)) <= 0
	 * 
	 * @param node
	 *            the current node
	 * @param communication
	 *            the communication that is being routed
	 * @param upLink
	 *            the incident link leading into the direction of the proxy
	 * @param downLinks
	 *            the set of incident links leading away from the proxy
	 * @param srcMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of the source task of the communication
	 * @param destMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of a destination task of the communication
	 * @param routing
	 *            the entire routing graph for the communication at hand
	 * @return the constraint stating that the node is a pull node if it a) has pull
	 *         nodes among its children or is the binding target of the
	 *         communication destination AND b) has no push nodes among its children
	 *         and is not the binding target of a communication source task
	 */
	protected Constraint makePullConstraint(Resource node, Task communication, Set<Link> upLinks, Set<Link> downLinks,
			Set<Variable> srcMappings, Set<Variable> destMappings, Architecture<Resource, Link> routing) {
		return makePushPullConstraint(node, communication, upLinks, downLinks, srcMappings, destMappings, routing,
				false);
	}

	/**
	 * Generates the constraint stating that the node is a push node if it a) has
	 * push nodes among its children or is the binding target of the communication
	 * src AND b) has no pull nodes among its children and is not the binding target
	 * of a communication destination task.
	 * 
	 * sum(l_in) + sum(M_s) - (L_o + sum(l_o) + sum(M_d)) <= 0
	 * 
	 * @param node
	 *            the current node
	 * @param communication
	 *            the communication that is being routed
	 * @param upLink
	 *            the incident link leading into the direction of the proxy
	 * @param downLinks
	 *            the set of incident links leading away from the proxy
	 * @param srcMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of the source task of the communication
	 * @param destMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of a destination task of the communication
	 * @param routing
	 *            the entire routing graph for the communication at hand
	 * @return the constraint stating that the node is a push node if it a) has push
	 *         nodes among its children or is the binding target of the
	 *         communication src AND b) has no pull nodes among its children and is
	 *         not the binding target of a communication destination task
	 */
	protected Constraint makePushConstraint(Resource node, Task communication, Set<Link> upLinks, Set<Link> downLinks,
			Set<Variable> srcMappings, Set<Variable> destMappings, Architecture<Resource, Link> routing) {
		return makePushPullConstraint(node, communication, upLinks, downLinks, srcMappings, destMappings, routing,
				true);
	}

	protected Constraint makePushPullConstraint(Resource node, Task communication, Set<Link> upLinks,
			Set<Link> downLinks, Set<Variable> srcMappings, Set<Variable> destMappings,
			Architecture<Resource, Link> routing, boolean push) {
		// the comments concern the push case
		Constraint pushPullConstraint = new Constraint(Operator.LE, 0);
		// sum(l_in)
		for (Link downLink : downLinks) {
			DirectedLink dirDownLink = push ? new DirectedLink(downLink, routing.getOpposite(node, downLink), node)
					: new DirectedLink(downLink, node, routing.getOpposite(node, downLink));
			pushPullConstraint.add(Variables.p(Variables.varCLRR(communication, dirDownLink)));
		}
		// sum(M_s)
		Set<Variable> proConditions = push ? srcMappings : destMappings;
		for (Variable proCondition : proConditions) {
			pushPullConstraint.add(Variables.p(proCondition));
		}
		int coefficient = (downLinks.size() + proConditions.size());// push ? 1 : (downLinks.size() +
																	// proConditions.size());
		// - L_o (the sum only applies to the proxy)
		for (Link upLink : upLinks) {
			DirectedLink encodedLink = push ? new DirectedLink(upLink, node, routing.getOpposite(node, upLink))
					: new DirectedLink(upLink, routing.getOpposite(node, upLink), node);
			pushPullConstraint.add(-coefficient, Variables.p(Variables.varCLRR(communication, encodedLink)));
		}
		// - sum(l_o)
		for (Link downLink : downLinks) {
			DirectedLink dirDownLink = push ? new DirectedLink(downLink, node, routing.getOpposite(node, downLink))
					: new DirectedLink(downLink, routing.getOpposite(node, downLink), node);
			pushPullConstraint.add(-coefficient, Variables.p(Variables.varCLRR(communication, dirDownLink)));
		}
		Set<Variable> contraConditions = push ? destMappings : srcMappings;
		// - sum(M_d)
		for (Variable contraCondition : contraConditions) {
			pushPullConstraint.add(-1, Variables.p(contraCondition));
		}
		return pushPullConstraint;
	}

	/**
	 * Generates the constraint stating that the node is never a pull node if it has
	 * not at least one pull node among its children or is the mapping target of a
	 * communication destination task.
	 * 
	 * sum(l_out) + sum(M_d) - L_i >= 0
	 * 
	 * @param the
	 *            current resource
	 * @param communication
	 *            the communication that is being routed
	 * @param upLink
	 *            the incident link leading into the direction of the proxy
	 * @param downLinks
	 *            the set of incident links leading away from the proxy
	 * @param srcMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of the source task of the communication
	 * @param routing
	 *            the entire routing graph for the communication at hand
	 * @return
	 */
	protected Constraint makeNoPullConstraint(Resource node, Task communication, Set<Link> upLinks, Set<Link> downLinks,
			Set<Variable> destMappings, Architecture<Resource, Link> routing) {
		return makeNoPushPullConstraint(node, communication, upLinks, downLinks, destMappings, routing, false);
	}

	/**
	 * Generates the constraint stating that the node is never a push node if it has
	 * not at least one push node among its children or is the mapping target of the
	 * communication source task.
	 * 
	 * sum(l_in) + sum(M_s) - L_o >= 0
	 * 
	 * @param the
	 *            current resource
	 * @param communication
	 *            the communication that is being routed
	 * @param upLink
	 *            the incident link leading into the direction of the proxy
	 * @param downLinks
	 *            the set of incident links leading away from the proxy
	 * @param srcMappings
	 *            the set of variables describing whether the current node is the
	 *            binding target of the source task of the communication
	 * @param routing
	 *            the entire routing graph for the communication at hand
	 * @return
	 */
	protected Constraint makeNoPushConstraint(Resource node, Task communication, Set<Link> upLinks, Set<Link> downLinks,
			Set<Variable> srcMappings, Architecture<Resource, Link> routing) {
		return makeNoPushPullConstraint(node, communication, upLinks, downLinks, srcMappings, routing, true);
	}

	protected Constraint makeNoPushPullConstraint(Resource node, Task communication, Set<Link> upLinks,
			Set<Link> downLinks, Set<Variable> mappings, Architecture<Resource, Link> routing, boolean push) {
		Constraint result = new Constraint(Operator.GE, 0);
		if (upLinks.size() > 1) {
			// the uplinks are not in the proxy area
			return result;
		}
		for (Link upLink : upLinks) {
			DirectedLink pushPullLink = push ? new DirectedLink(upLink, node, routing.getOpposite(node, upLink))
					: new DirectedLink(upLink, routing.getOpposite(node, upLink), node);
			result.add(-1, Variables.p(Variables.varCLRR(communication, pushPullLink)));
		}
		int coefficient = upLinks.size();
		for (Variable mappingVariable : mappings) {
			result.add(coefficient, Variables.p(mappingVariable));
		}
		for (Link downLink : downLinks) {
			DirectedLink pushPullLinkDown = push ? new DirectedLink(downLink, routing.getOpposite(node, downLink), node)
					: new DirectedLink(downLink, node, routing.getOpposite(node, downLink));
			result.add(coefficient, Variables.p(Variables.varCLRR(communication, pushPullLinkDown)));
		}
		return result;
	}
}