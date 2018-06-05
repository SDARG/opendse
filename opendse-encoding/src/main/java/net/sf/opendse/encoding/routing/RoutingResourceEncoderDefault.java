package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import net.sf.opendse.encoding.constraints.Constraints;
import net.sf.opendse.encoding.variables.DDR;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;

public class RoutingResourceEncoderDefault implements RoutingResourceEncoder {

	@Override
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing,
			Set<MappingVariable> mappingVariables) {
		Set<Constraint> routingResourceConstraints = new HashSet<Constraint>();
		Task srcTask = communicationFlow.getSourceDTT().getSourceTask();
		Task destTask = communicationFlow.getDestinationDTT().getDestinationTask();
		Set<M> srcMappings = new HashSet<M>();
		Set<M> destMappings = new HashSet<M>();
		for (MappingVariable mappingVar : mappingVariables) {
			M mVar = (M) mappingVar;
			if (mVar.getMapping().getSource().equals(srcTask)) {
				srcMappings.add(mVar);
			}
			if (mVar.getMapping().getSource().equals(destTask)) {
				destMappings.add(mVar);
			}
		}
		for (Resource res : routing) {
			Set<M> srcMOnRes = new HashSet<M>();
			Set<M> destMOnRes = new HashSet<M>();
			for (M mVar : srcMappings) {
				if (mVar.getMapping().getTarget().equals(res)) {
					srcMOnRes.add(mVar);
				}
			}
			for (M mVar : destMappings) {
				if (mVar.getMapping().getTarget().equals(res)) {
					destMOnRes.add(mVar);
				}
			}
			Set<Variable> relevantVariables = gatherRelevantVariables(communicationFlow, res, routing);
			if (!(srcMOnRes.isEmpty() && destMOnRes.isEmpty())) {
				// consider the case where both src and destination are mapped on the resource
				Set<Variable> processingResource = new HashSet<Variable>();
				processingResource.addAll(srcMOnRes);
				processingResource.addAll(destMOnRes);
				processingResource.add(communicationFlow.getSourceDTT());
				processingResource.add(communicationFlow.getDestinationDTT());
				Variable andVariable = Constraints.generateAndVariable(processingResource, routingResourceConstraints);
				relevantVariables.add(andVariable);
			}
			DDR routingResourceVariable = Variables.varDDR(communicationFlow, res);
			routingResourceConstraints
					.addAll(Constraints.generateOrConstraints(relevantVariables, routingResourceVariable));
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
		Set<DirectedLink> nodeLinks = new HashSet<Models.DirectedLink>(Models.getInLinks(routing, resource));
		nodeLinks.addAll(Models.getOutLinks(routing, resource));
		for (DirectedLink directedLink : nodeLinks) {
			result.add(Variables.varDDLRR(communicationFlow, directedLink));
		}
		return result;
	}
}