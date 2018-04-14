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
		String color = "black";
		Task comm = commVar.getTask();
		// iterates all directed links
		for (DirectedLink dLink : Models.getLinks(routing)){
			CLRR linkUsed = Variables.varCLRR(comm, dLink);
			Resource src = dLink.getSource();
			Resource dest = dLink.getDest();
			ColoredCommNode srcBlack = Variables.varColoredCommNode(comm, src, color);
			ColoredCommNode destBlack = Variables.varColoredCommNode(comm, dest, color);
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
		Set<Constraint> result = new HashSet<Constraint>();
		return result;
	}
}
