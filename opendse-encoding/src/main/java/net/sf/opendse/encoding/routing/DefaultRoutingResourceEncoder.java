package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import net.sf.opendse.encoding.constraints.Constraints;
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
			routingResourceConstraints.addAll(Constraints.generateOrConstraints(relevantVariables, routingResourceVariable));
		}
		return routingResourceConstraints;
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
			result.add(Variables.varDDLRR(communicationFlow, directedLink));
		}
		return result;
	}
}