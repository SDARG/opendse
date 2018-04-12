package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link CycleBreakEncoderNonRedundant} formulates {@link Constraint}s that
 * prevent cycles for non-redundant message transmissions. In this simple case,
 * cycles can be prevented by stating that at most one in-link of a resource may
 * be used for the routing of the same message.
 * 
 * @author Fedor Smirnov
 *
 */
public class CycleBreakEncoderNonRedundant implements CycleBreakEncoder {

	@Override
	public Set<Constraint> toConstraints(T communicationVariable, Architecture<Resource, Link> routing) {
		Set<Constraint> cycleBreakConstraints = new HashSet<Constraint>();
		Task communication = communicationVariable.getTask();
		for (Resource res : routing) {
			Set<DirectedLink> inLinks = new HashSet<Models.DirectedLink>(Models.getInLinks(routing, res));
			cycleBreakConstraints.add(makeOneInLinkConstraint(communication, inLinks));
		}
		return cycleBreakConstraints;
	}

	/**
	 * Returns the constraint stating that the given communication may be routed
	 * using at most one of the given {@link DirectedLink}s.
	 * 
	 * sum(CLRR) <= 1
	 * 
	 * @param communication
	 *            the communication task that is to be routed
	 * @param inLinks
	 *            the set of {@link DirectedLink}s, all of them in-links of the same
	 *            resource
	 * @return {@link Constraint} stating that the given communication may be routed
	 *         using at most one of the given {@link DirectedLink}s
	 */
	public Constraint makeOneInLinkConstraint(Task communication, Set<DirectedLink> inLinks) {
		Constraint result = new Constraint(Operator.LE, 1);
		for (DirectedLink inLink : inLinks) {
			result.add(Variables
					.p(Variables.varCLRR(communication, inLink.getLink(), inLink.getSource(), inLink.getDest())));
		}
		return result;
	}
}