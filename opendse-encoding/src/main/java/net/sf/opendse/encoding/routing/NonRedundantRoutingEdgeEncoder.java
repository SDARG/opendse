package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Models.DirectedLink;

/**
 * The {@link NonRedundantRoutingEdgeEncoder} encodes the activation of
 * {@link Link}s in the routing of a {@link CommunicationFlow} for the case of a
 * non-redundant message transmission. During a non-redundant transmission of a
 * {@link CommunicationFlow}, each {@link Resource} has at most one in- and at
 * most one out-link that is used for the routing of the communication flow.
 * 
 * @author Fedor Smirnov
 *
 */
public class NonRedundantRoutingEdgeEncoder implements RoutingEdgeEncoder {

	@Override
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing) {
		Set<Constraint> routingEdgeConstraints = new HashSet<Constraint>();
		for (Resource resource : routing) {
			Set<DirectedLink> inLinks = new HashSet<Models.DirectedLink>(Models.getInLinks(routing, resource));
			Set<DirectedLink> outLinks = new HashSet<Models.DirectedLink>(Models.getOutLinks(routing, resource));
			routingEdgeConstraints.add(makeOutLinkConstraint(communicationFlow, resource, outLinks));
			routingEdgeConstraints.add(makeInLinkConstraint(communicationFlow, resource, inLinks));
			routingEdgeConstraints.add(makeLinkBalanceConstraint(communicationFlow, resource, inLinks, outLinks));
		}
		return routingEdgeConstraints;
	}

	/**
	 * Returns the {@link Constraint} stating that the considered {@link Resource}
	 * has at most one activated out {@link Link} if it is not the destination of
	 * the message transmission and has no activated out links in the case where it
	 * is the destination.
	 * 
	 * sum(DDL_oRR) + DDdR <= 1
	 * 
	 * @param commFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param resource
	 *            the considered {@link Resource}
	 * @param outLinks
	 *            the out {@link Link}s of the resource
	 * @return the {@link Constraint} stating that the considered {@link Resource}
	 *         has at most one activated out {@link Link} if it is not the
	 *         destination of the message transmission and has no activated out
	 *         links in the case where it is the destination
	 */
	protected Constraint makeOutLinkConstraint(CommunicationFlow commFlow, Resource resource,
			Set<DirectedLink> outLinks) {
		Constraint result = new Constraint(Operator.LE, 1);
		result.add(Variables.p(Variables.varDDdR(commFlow, resource)));
		for (DirectedLink outLink : outLinks) {
			result.add(Variables.p(Variables.varDDLRR(commFlow, outLink)));
		}
		return result;
	}

	/**
	 * Returns the {@link Constraint} stating that the considered {@link Resource}
	 * has at most one activated in {@link Link} if it is not the source of the
	 * message transmission and has no activated in links in the case where it is
	 * the source.
	 * 
	 * sum(DDL_iRR) + DDsR <= 1
	 * 
	 * @param commFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param resource
	 *            the considered {@link Resource}
	 * @param inLinks
	 *            the in {@link Link}s of the resource
	 * @return the {@link Constraint} stating that the considered {@link Resource}
	 *         has at most one activated in {@link Link} if it is not the source of
	 *         the message transmission and has no activated in links in the case
	 *         where it is the source
	 */
	protected Constraint makeInLinkConstraint(CommunicationFlow commFlow, Resource resource,
			Set<DirectedLink> inLinks) {
		Constraint result = new Constraint(Operator.LE, 1);
		result.add(Variables.p(Variables.varDDsR(commFlow, resource)));
		for (DirectedLink inLink : inLinks) {
			result.add(Variables.p(Variables.varDDLRR(commFlow, inLink)));
		}
		return result;
	}

	/**
	 * Returns the {@link Constraint} set that expresses the balance between the in-
	 * and the out-links of a resource. A resource that is neither source nor
	 * destination has either no activated links at all or exactly one activated in
	 * and one activated out link.
	 * 
	 * DDsR - DDdR + sum(DDL_iRR) - sum(DDL_oRR) = 0
	 * 
	 * @param commFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param resource
	 *            the considered {@link Resource}
	 * @param inLinks
	 *            the in {@link Link}s of the resource
	 * @param outLinks
	 *            the out links of the resource
	 * @return the {@link Constraint} set that expresses the balance between the in-
	 *         and the out-links of a resource
	 */
	protected Constraint makeLinkBalanceConstraint(CommunicationFlow commFlow, Resource resource,
			Set<DirectedLink> inLinks, Set<DirectedLink> outLinks) {
		Constraint result = new Constraint(Operator.EQ, 0);
		result.add(Variables.p(Variables.varDDsR(commFlow, resource)));
		result.add(-1, Variables.p(Variables.varDDdR(commFlow, resource)));
		for (DirectedLink inLink : inLinks) {
			result.add(Variables.p(Variables.varDDLRR(commFlow, inLink)));
		}
		for (DirectedLink outLink : outLinks) {
			result.add(-1, Variables.p(Variables.varDDLRR(commFlow, outLink)));
		}
		return result;
	}
}