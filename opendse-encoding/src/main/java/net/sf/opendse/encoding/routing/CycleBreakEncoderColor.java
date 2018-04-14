package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.ColoredCommNode;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;

/**
 * The {@link CycleBreakEncoderColor} breaks cycles in disconnected graph
 * components by performing a two- and a three- coloring of the graph. It offers
 * an efficient way of preventing cycles for the non-redundant routing encoding.
 * 
 * @author Fedor Smirnov
 *
 */
public class CycleBreakEncoderColor implements CycleBreakEncoder {

	private final String black = "black";
	private final String red = "red";
	private final String blue = "blue";

	@Override
	public Set<Constraint> toConstraints(T communicationVariable, Architecture<Resource, Link> routing) {
		Set<Constraint> result = new HashSet<Constraint>();
		result.addAll(performTwoColoring(communicationVariable, routing));
		result.addAll(performThreeColoring(communicationVariable, routing));
		return result;
	}

	/**
	 * Formulates {@link Constraint}s that result in a 2-coloring of the routing
	 * graph. A link may only be used for the routing of the communication if
	 * its tow end points have a different color. By this, all cycles with an
	 * odd number of nodes are prevented.
	 * 
	 * @param commVar
	 *            the {@link T} {@link Variable} encoding the activation of the
	 *            communication that is being routed
	 * @param routing
	 *            the {@link Architecture} graph representing all possible
	 *            routings for the communication that is being routed
	 * @return {@link Constraint}s that result in a 2-coloring of the routing
	 *         graph. A link may only be used for the routing of the
	 *         communication if its tow end points have a different color. By
	 *         this, all cycles with an odd number of nodes are prevented.
	 */
	protected Set<Constraint> performTwoColoring(T commVar, Architecture<Resource, Link> routing) {
		Set<Constraint> result = new HashSet<Constraint>();
		Task comm = commVar.getTask();
		// iterates all directed links
		for (DirectedLink dLink : Models.getLinks(routing)) {
			CLRR linkUsed = Variables.varCLRR(comm, dLink);
			Resource src = dLink.getSource();
			Resource dest = dLink.getDest();
			ColoredCommNode srcBlack = Variables.varColoredCommNode(comm, src, black);
			ColoredCommNode destBlack = Variables.varColoredCommNode(comm, dest, black);
			Constraint notBothWhite = new Constraint(Operator.GE, 0);
			notBothWhite.add(Variables.p(srcBlack));
			notBothWhite.add(Variables.p(destBlack));
			notBothWhite.add(-1, Variables.p(linkUsed));
			result.add(notBothWhite);
			Constraint notBothBlack = new Constraint(Operator.LE, 2);
			notBothBlack.add(Variables.p(srcBlack));
			notBothBlack.add(Variables.p(destBlack));
			notBothBlack.add(Variables.p(linkUsed));
			result.add(notBothBlack);
		}
		return result;
	}

	/**
	 * 
	 * Formulates {@link Constraint}s that result in a 3-coloring of the routing
	 * graph. Each resource node that is used for the routing of the message
	 * must not share the color with its pre- or its successor. By this, all
	 * cycles with an even number of nodes are prevented.
	 * 
	 * @param commVar
	 *            the {@link T} {@link Variable} encoding the activation of the
	 *            communication that is being routed
	 * @param routing
	 *            the {@link Architecture} graph representing all possible
	 *            routings for the communication that is being routed
	 * @return {@link Constraint}s that result in a 3-coloring of the routing
	 *         graph. Each resource node that is used for the routing of the
	 *         message must not share the color with its pre- or its successor.
	 *         By this, all cycles with an even number of nodes are prevented.
	 */
	protected Set<Constraint> performThreeColoring(T commVar, Architecture<Resource, Link> routing) {
		Task comm = commVar.getTask();
		Set<Constraint> result = new HashSet<Constraint>();
		// iterates each resource and processes each in- and out-link pair
		for (Resource res : routing) {
			result.add(paintResource3Colors(comm, res));
			Set<DirectedLink> inLinks = new HashSet<Models.DirectedLink>(Models.getInLinks(routing, res));
			Set<DirectedLink> outLinks = new HashSet<Models.DirectedLink>(Models.getOutLinks(routing, res));
			// states that the resource has to have a different color than its
			// predecessor
			for (DirectedLink inLink : inLinks) {
				Resource predecessor = inLink.getSource();
				result.addAll(paintNeighborsDifferently(comm, inLink, res, predecessor));
			}
			// states that the resource has to have a different color than its
			// predecessor
			for (DirectedLink outLink : outLinks) {
				Resource successor = outLink.getDest();
				result.addAll(paintNeighborsDifferently(comm, outLink, res, successor));
			}
			// states that the predecessor has to be painted differently that
			// the successor
			for (DirectedLink inLink : inLinks) {
				for (DirectedLink outLink : outLinks) {
					Resource predecessor = inLink.getSource();
					Resource successor = outLink.getDest();
					if (predecessor.equals(successor))
						continue;
					result.addAll(paintNeighborhoodDifferently(comm, inLink, outLink, predecessor, successor));
				}
			}
		}
		return result;
	}

	/**
	 * Formulates the {@link Constraint}s stating that a pair consisting of an
	 * in- and an out-link of the same resource may only be activated for the
	 * routing of a communication {@link Task} if the predecessor and the
	 * successor of the resource are colored differently (out of 3 possible
	 * colors).
	 * 
	 * @param comm
	 *            the communication that is being routed
	 * @param inLink
	 *            the {@link DirectedLink} that has the resource as destination
	 * @param outLink
	 *            the {@link DirectedLink} that has the resource as source
	 * @param predecessor
	 *            the predecessor of the resource
	 * @param successor
	 *            the successor of the resource
	 * @return the {@link Constraint}s stating that a pair consisting of an in-
	 *         and an out-link of the same resource may only be activated for
	 *         the routing of a communication {@link Task} if the predecessor
	 *         and the successor of the resource are colored differently (out of
	 *         3 possible colors)
	 */
	protected Set<Constraint> paintNeighborhoodDifferently(Task comm, DirectedLink inLink, DirectedLink outLink,
			Resource predecessor, Resource successor) {
		Set<Constraint> result = new HashSet<Constraint>();
		CLRR inLinkUsed = Variables.varCLRR(comm, inLink);
		CLRR outLinkUsed = Variables.varCLRR(comm, outLink);
		ColoredCommNode predecessorRed = Variables.varColoredCommNode(comm, predecessor, red);
		ColoredCommNode predecessorBlue = Variables.varColoredCommNode(comm, predecessor, blue);
		ColoredCommNode successorRed = Variables.varColoredCommNode(comm, successor, red);
		ColoredCommNode successorBlue = Variables.varColoredCommNode(comm, successor, blue);
		Constraint notBothRed = new Constraint(Operator.LE, 3);
		notBothRed.add(Variables.p(successorRed));
		notBothRed.add(Variables.p(predecessorRed));
		notBothRed.add(Variables.p(inLinkUsed));
		notBothRed.add(Variables.p(outLinkUsed));
		result.add(notBothRed);
		Constraint notBothBlue = new Constraint(Operator.LE, 3);
		notBothBlue.add(Variables.p(successorBlue));
		notBothBlue.add(Variables.p(predecessorBlue));
		notBothBlue.add(Variables.p(inLinkUsed));
		notBothBlue.add(Variables.p(outLinkUsed));
		result.add(notBothBlue);
		Constraint notBothWhite = new Constraint(Operator.GE, -1);
		notBothWhite.add(Variables.p(successorRed));
		notBothWhite.add(Variables.p(successorBlue));
		notBothWhite.add(Variables.p(predecessorRed));
		notBothWhite.add(Variables.p(predecessorBlue));
		notBothWhite.add(-1, Variables.p(inLinkUsed));
		notBothWhite.add(-1, Variables.p(outLinkUsed));
		result.add(notBothWhite);
		return result;
	}

	/**
	 * Formulates the {@link Constraint}s stating that a {@link Link} may only
	 * be used for the routing of a communication {@link Task} if its two end
	 * points have a different color (out of 3 possible colors).
	 * 
	 * @param comm
	 *            the communication that is being routed
	 * @param dLink
	 *            the {@link DirectedLink} that may be used for the routing
	 * @param first
	 *            one of the end points of the link
	 * @param second
	 *            the other end point of the link
	 * @return the {@link Constraint}s stating that a {@link Link} may only be
	 *         used for the routing of a communication {@link Task} if its two
	 *         end points have a different color (out of 3 possible colors)
	 */
	protected Set<Constraint> paintNeighborsDifferently(Task comm, DirectedLink dLink, Resource first,
			Resource second) {
		Set<Constraint> result = new HashSet<Constraint>();
		CLRR linkUsed = Variables.varCLRR(comm, dLink);
		ColoredCommNode firstRed = Variables.varColoredCommNode(comm, first, red);
		ColoredCommNode firstBlue = Variables.varColoredCommNode(comm, first, blue);
		ColoredCommNode secondRed = Variables.varColoredCommNode(comm, second, red);
		ColoredCommNode secondBlue = Variables.varColoredCommNode(comm, second, blue);
		Constraint notBothRed = new Constraint(Operator.LE, 2);
		notBothRed.add(Variables.p(firstRed));
		notBothRed.add(Variables.p(secondRed));
		notBothRed.add(Variables.p(linkUsed));
		result.add(notBothRed);
		Constraint notBothBlue = new Constraint(Operator.LE, 2);
		notBothBlue.add(Variables.p(firstBlue));
		notBothBlue.add(Variables.p(secondBlue));
		notBothBlue.add(Variables.p(linkUsed));
		result.add(notBothBlue);
		Constraint notBothWhite = new Constraint(Operator.GE, 0);
		notBothWhite.add(Variables.p(firstRed));
		notBothWhite.add(Variables.p(firstBlue));
		notBothWhite.add(Variables.p(secondRed));
		notBothWhite.add(Variables.p(secondBlue));
		notBothWhite.add(-1, Variables.p(linkUsed));
		result.add(notBothWhite);
		return result;
	}

	/**
	 * Formulates the {@link Constraint} stating that the given {@link Resource}
	 * has exactly one of three colors in the routing of the given communication
	 * {@link Task}.
	 * 
	 * @param comm
	 *            the communication that is being routed
	 * @param res
	 *            the resource that is being painted
	 * @return the {@link Constraint} stating that the given {@link Resource}
	 *         has exactly one of three colors in the routing of the given
	 *         communication {@link Task}
	 */
	protected Constraint paintResource3Colors(Task comm, Resource res) {
		ColoredCommNode resourceRed = Variables.varColoredCommNode(comm, res, red);
		ColoredCommNode resourceBlue = Variables.varColoredCommNode(comm, res, blue);
		Constraint result = new Constraint(Operator.LE, 1);
		result.add(Variables.p(resourceBlue));
		result.add(Variables.p(resourceRed));
		return result;
	}
}
