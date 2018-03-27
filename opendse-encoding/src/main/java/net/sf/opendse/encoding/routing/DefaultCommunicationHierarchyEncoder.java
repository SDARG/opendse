package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.DDLRR;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class DefaultCommunicationHierarchyEncoder implements CommunicationHierarchyEncoder {

	@Override
	public Set<Constraint> toConstraints(T communicationVariable, Set<CommunicationFlow> communicationFlows,
			Architecture<Resource, Link> routing) {
		Set<Constraint> result = new HashSet<Constraint>();
		Task communication = communicationVariable.getTask();
		for (DirectedLink dLink : Models.getLinks(routing)) {
			result.addAll(formulateLinkConstraints(communication, communicationFlows, dLink));
		}
		return result;
	}

	/**
	 * Formulates the constraints expressing the hierarchy between the {@link CLRR}
	 * and the {@link DDLRR} variables of the given communication on the given link.
	 * 
	 * @param communication
	 *            the communication {@link Task} that is being routed
	 * @param communicationFlows
	 *            the set of {@link CommunicationFlow}s of the communication
	 *            {@link Task} that is being routed
	 * @param directedLink
	 *            the {@link DirectedLink} that is being processed
	 * @return the set of {@link Constraint}s expressing the hierarchy between the
	 *         {@link CLRR} and the {@link DDLRR} variables of the given
	 *         communication on the given link
	 */
	protected Set<Constraint> formulateLinkConstraints(Task communication, Set<CommunicationFlow> communicationFlows,
			DirectedLink directedLink) {
		Set<Constraint> linkHierarchyConstraints = new HashSet<Constraint>();
		linkHierarchyConstraints.add(makeDeactivationConstraint(communication, communicationFlows, directedLink));
		for (CommunicationFlow commFlow : communicationFlows) {
			linkHierarchyConstraints.add(makeActivationConstraint(communication, commFlow, directedLink));
		}
		return linkHierarchyConstraints;
	}

	/**
	 * Formulates the constraint stating that the {@link CLRR} variable on a link is
	 * deactivated if all the {@link DDLRR} of this link are also deactivated.
	 * 
	 * CLRR - sum(DDLRR) <= 0
	 * 
	 * @param communication
	 *            the communication {@link Task} that is being routed
	 * @param commFlows
	 *            the set of {@link CommunicationFlow}s of the communication
	 *            {@link Task} that is being routed
	 * @param directedLink
	 *            the {@link DirectedLink} that is being processed
	 * @return the {@link Constraint} stating that the {@link CLRR} variable on a
	 *         link is deactivated if all the {@link DDLRR} of this link are also
	 *         deactivated
	 */
	protected Constraint makeDeactivationConstraint(Task communication, Set<CommunicationFlow> commFlows,
			DirectedLink directedLink) {
		Constraint result = new Constraint(Operator.LE, 0);
		result.add(Variables.p(Variables.varCLRR(communication, directedLink.getLink(), directedLink.getSource(),
				directedLink.getDest())));
		for (CommunicationFlow flow : commFlows) {
			result.add(-1, Variables.p(Variables.varDDLRR(flow, directedLink.getLink(), directedLink.getSource(),
					directedLink.getDest())));
		}
		return result;
	}

	/**
	 * Formulates the {@link Constraint} stating that the {@link CLRR} variable is
	 * active if a {@link DDLRR} of the same communication on the same
	 * {@link DirectedLink} is active.
	 * 
	 * DDLRR - CLRR <= 0
	 * 
	 * @param communication
	 *            the communication {@link Task} that is being routed
	 * @param commFlow
	 *            a {@link CommunicationFlow} of the communication that is being
	 *            routed
	 * @param directedLink
	 *            the {@link DirectedLink} that is being processed
	 * @return the {@link Constraint} stating that the {@link CLRR} variable is
	 *         active if a {@link DDLRR} of the same communication on the same
	 *         {@link DirectedLink} is active
	 */
	protected Constraint makeActivationConstraint(Task communication, CommunicationFlow commFlow,
			DirectedLink directedLink) {
		Constraint result = new Constraint(Operator.LE, 0);
		CLRR commVar = Variables.varCLRR(communication, directedLink.getLink(), directedLink.getSource(),
				directedLink.getDest());
		DDLRR commFlowVar = Variables.varDDLRR(commFlow, directedLink.getLink(), directedLink.getSource(),
				directedLink.getDest());
		result.add(Variables.p(commFlowVar));
		result.add(-1, Variables.p(commVar));
		return result;
	}
}