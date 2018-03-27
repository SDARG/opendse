package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.DDLRR;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Models.DirectedLink;

public class DefaultActivationEncoder implements ActivationEncoder {

	@Override
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing) {
		Set<Constraint> activationConstraints = new HashSet<Constraint>();
		for (DirectedLink directedLink : Models.getLinks(routing)) {
			activationConstraints.add(makeDeactivationConstraint(communicationFlow, directedLink));
		}
		return activationConstraints;
	}

	/**
	 * Returns the {@link Constraint} stating that the {@link DDLRR} variable
	 * describing the usage of a {@link DirectedLink} is automatically deactivated
	 * if one of the {@link DTT} variables of the corresponding
	 * {@link CommunicationFlow} is deactivated.
	 * 
	 * -DTT_s - DTT_d + 2 * DDLRR <= 0
	 * 
	 * @param commFlow
	 * @param directedLink
	 * @return
	 */
	protected Constraint makeDeactivationConstraint(CommunicationFlow commFlow, DirectedLink directedLink) {
		Constraint result = new Constraint(Operator.LE, 0);
		result.add(-1, Variables.p(commFlow.getSourceDTT()));
		result.add(-1, Variables.p(commFlow.getDestinationDTT()));
		result.add(2, Variables.p(Variables.varDDLRR(commFlow, directedLink.getLink(), directedLink.getSource(),
				directedLink.getDest())));
		return result;
	}
}
