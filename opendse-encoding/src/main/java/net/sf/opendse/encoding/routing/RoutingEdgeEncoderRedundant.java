package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.DDR;
import net.sf.opendse.encoding.variables.DDdR;
import net.sf.opendse.encoding.variables.DDsR;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Models.DirectedLink;

public class RoutingEdgeEncoderRedundant implements RoutingEdgeEncoder {

	@Override
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing) {
		Set<Constraint> result = new HashSet<Constraint>();
		for (Resource res : routing) {
			result.addAll(generateSrcConstraints(communicationFlow, res, routing));
			result.addAll(generateDestConstraints(communicationFlow, res, routing));
			result.addAll(generateLinkBalanceConstraints(communicationFlow, res, routing));
		}

		return result;
	}

	/**
	 * Generates the constraints stating that a resource either does not have any
	 * activated edges or that it has both in- and out- edges that are active.
	 * 
	 * src + sum(outLink) - CR >= 0 dest + sum(inLink) - CR >= 0
	 * 
	 * @param flow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param res
	 *            the {@link Resource} that is being considered
	 * @param routing
	 *            the routing graph
	 * @return the constraints stating that a resource either does not have any
	 *         activated edges or that it has both in- and out- edges that are
	 *         active
	 */
	protected Set<Constraint> generateLinkBalanceConstraints(CommunicationFlow flow, Resource res,
			Architecture<Resource, Link> routing) {
		Set<Constraint> result = new HashSet<Constraint>();
		Set<DirectedLink> inLinks = new HashSet<Models.DirectedLink>(Models.getInLinks(routing, res));
		Set<DirectedLink> outLinks = new HashSet<Models.DirectedLink>(Models.getOutLinks(routing, res));
		Constraint outLinkConstraint = new Constraint(Operator.GE, 0);
		Constraint inLinkConstraint = new Constraint(Operator.GE, 0);
		DDR resourceUsed = Variables.varDDR(flow, res);
		DDsR resourceSrc = Variables.varDDsR(flow, res);
		DDdR resourceDest = Variables.varDDdR(flow, res);
		inLinkConstraint.add(Variables.p(resourceSrc));
		inLinkConstraint.add(-1, Variables.p(resourceUsed));
		for (DirectedLink inLink : inLinks) {
			inLinkConstraint.add(Variables.p(Variables.varDDLRR(flow, inLink)));
		}
		outLinkConstraint.add(Variables.p(resourceDest));
		outLinkConstraint.add(-1, Variables.p(resourceUsed));
		for (DirectedLink outLink : outLinks) {
			outLinkConstraint.add(Variables.p(Variables.varDDLRR(flow, outLink)));
		}
		result.add(inLinkConstraint);
		result.add(outLinkConstraint);
		return result;
	}

	/**
	 * Generates the constraints stating that the destination of the communication
	 * flow (which is not a source) has a) at least one in-edge and b) no out-edges.
	 * 
	 * @param flow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param res
	 *            the {@link Resource} that is being considered
	 * @param routing
	 *            the routing graph
	 * @return the constraints stating that the destination of the communication
	 *         flow has a) at least one in-edge and b) no out-edges
	 */
	protected Set<Constraint> generateDestConstraints(CommunicationFlow flow, Resource res,
			Architecture<Resource, Link> routing) {
		return generateEndPointConstraints(flow, res, routing, false);
	}

	/**
	 * Generates the constraints stating that the source of the communication flow (which is not a destination)
	 * has a) at least one out-edge
	 * 
	 * sum(outLink) + dest - src >= 0 each inLink <= src
	 * 
	 * @param flow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param res
	 *            the {@link Resource} that is being considered
	 * @param routing
	 *            the routing graph
	 * @return the constraints stating that the source of the communication flow has
	 *         a) at least one out-edge and b) no in-edges
	 */
	protected Set<Constraint> generateSrcConstraints(CommunicationFlow flow, Resource res,
			Architecture<Resource, Link> routing) {
		return generateEndPointConstraints(flow, res, routing, true);
	}

	protected Set<Constraint> generateEndPointConstraints(CommunicationFlow flow, Resource res,
			Architecture<Resource, Link> routing, boolean source) {
		Set<Constraint> result = new HashSet<Constraint>();
//		Set<DirectedLink> inLinks = source ? new HashSet<Models.DirectedLink>(Models.getInLinks(routing, res))
//				: new HashSet<Models.DirectedLink>(Models.getOutLinks(routing, res));
		Set<DirectedLink> outLinks = source ? new HashSet<Models.DirectedLink>(Models.getOutLinks(routing, res))
				: new HashSet<Models.DirectedLink>(Models.getInLinks(routing, res));
		Constraint outLinkConstraint = new Constraint(Operator.GE, 0);
		Variable resourceSource = source ? Variables.varDDsR(flow, res) : Variables.varDDdR(flow, res);
		Variable resourceDest = source ? Variables.varDDdR(flow, res) : Variables.varDDsR(flow, res);
		outLinkConstraint.add(Variables.p(resourceDest));
		outLinkConstraint.add(-1, Variables.p(resourceSource));
		for (DirectedLink outLink : outLinks) {
			outLinkConstraint.add(Variables.p(Variables.varDDLRR(flow, outLink)));
		}
		result.add(outLinkConstraint);
//		for (DirectedLink inLink : inLinks) {
//			Constraint inLinkConstraint = new Constraint(Operator.LE, 0);
//			inLinkConstraint.add(Variables.p(Variables.varDDLRR(flow, inLink)));
//			inLinkConstraint.add(-1, Variables.n(resourceSource));
//			result.add(inLinkConstraint);
//		}
		return result;
	}
}
