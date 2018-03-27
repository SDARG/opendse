package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.DDR;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Models.DirectedLink;

public class DefaultRoutingResourceEncoder implements RoutingResourceEncoder {

	@Override
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing) {

		Set<Constraint> routingResourceConstraints = new HashSet<Constraint>();
		for (Resource res : routing) {
			DDR routingResourceVariable = Variables.varDDR(communicationFlow, res);
			Set<Variable> relevantVariables = gatherRelevantVariables(communicationFlow, res, routing);
			routingResourceConstraints
					.addAll(makeRoutingResourceConstraints(routingResourceVariable, relevantVariables));
		}
		return routingResourceConstraints;
	}

	/**
	 * Formulates the {@link Constraint} stating that the {@link DDR} variable has
	 * to be active if the given relevant {@link Variable} is active.
	 * 
	 * var - DDR <= 0
	 * 
	 * @param routingResourceVariable
	 *            the {@link DDR} variable that is being encoded
	 * @param relevantVariable
	 *            the {@link Variable} relevant for the activation of the
	 *            {@link DDR} variable
	 * @return the {@link Constraint} stating that the {@link DDR} variable has to
	 *         be active if the given relevant {@link Variable} is active
	 */
	protected Constraint makeActivationConstraint(DDR routingResourceVariable, Variable relevantVariable) {
		Constraint result = new Constraint(Operator.LE, 0);
		result.add(Variables.p(relevantVariable));
		result.add(-1, Variables.p(routingResourceVariable));
		return result;
	}

	/**
	 * Formulates the {@link Constraint} making sure that the {@link DDR} variable
	 * is deactivated if none of the relevant {@link Variable}s is active.
	 * 
	 * DDR - sum(var) <= 0
	 * 
	 * @param routingResourceVariable
	 *            the {@link DDR} variable that is being encoded
	 * @param relevantVariables
	 *            the {@link Variable} relevant for the activation
	 * @return the {@link Constraint} making sure that the {@link DDR} variable is
	 *         deactivated if none of the relevant {@link Variable}s is active
	 */
	protected Constraint makeDeactivationConstraint(DDR routingResourceVariable, Set<Variable> relevantVariables) {
		Constraint result = new Constraint(Operator.LE, 0);
		result.add(Variables.p(routingResourceVariable));
		for (Variable var : relevantVariables) {
			result.add(-1, Variables.p(var));
		}
		return result;
	}

	/**
	 * Formulates the constraints ensuring that the given {@link DDR} variable is
	 * activated in accordance with the relevant {@link Variable}s.
	 * 
	 * @param routingResourceVariable
	 *            the {@link DDR} variable that is encoded
	 * @param relevantVariables
	 *            the {@link Variable}s relevant for the value of the {@link DDR}
	 *            variable
	 * @return the constraints ensuring that the given {@link DDR} variable is
	 *         activated in accordance with the relevant {@link Variable}s
	 */
	protected Set<Constraint> makeRoutingResourceConstraints(DDR routingResourceVariable,
			Set<Variable> relevantVariables) {
		Set<Constraint> result = new HashSet<Constraint>();
		result.add(makeDeactivationConstraint(routingResourceVariable, relevantVariables));
		for (Variable relevantVariable : relevantVariables) {
			result.add(makeActivationConstraint(routingResourceVariable, relevantVariable));
		}
		return result;
	}

	/**
	 * Returns the set of all {@link Variable}s whose activation results in the
	 * activation of the {@link DDR} variable.
	 * 
	 * @param communicationFlow
	 *            the {@link CommunicationFlow} under consideration
	 * @param resource
	 *            the {@link Resource} under consideration
	 * @param routing
	 *            the {@link Architecture} representing all possible routings for
	 *            the {@link Communication} that is being routed
	 * @return the set of all {@link Variable}s whose activation results in the
	 *         activation of the {@link DDR} variable
	 */
	protected Set<Variable> gatherRelevantVariables(CommunicationFlow communicationFlow, Resource resource,
			Architecture<Resource, Link> routing) {
		Set<Variable> result = new HashSet<Variable>();
		result.add(Variables.varDDsR(communicationFlow, resource));
		result.add(Variables.varDDdR(communicationFlow, resource));
		Set<DirectedLink> nodeLinks = new HashSet<Models.DirectedLink>(Models.getInLinks(routing, resource));
		nodeLinks.addAll(Models.getOutLinks(routing, resource));
		for (DirectedLink directedLink : nodeLinks) {
			result.add(Variables.varDDLRR(communicationFlow, directedLink.getLink(), directedLink.getSource(),
					directedLink.getDest()));
		}
		return result;
	}
}