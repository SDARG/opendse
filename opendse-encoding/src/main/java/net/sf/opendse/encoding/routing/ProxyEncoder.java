package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.DDLRR;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.properties.ResourcePropertyService;

/**
 * Encodes the activation of the preprocessed routes based on the chosen
 * mapping.
 * 
 * @author Fedor Smirnov
 *
 */
public class ProxyEncoder {

	public Set<Constraint> toConstraints(CommunicationFlow flow, Architecture<Resource, Link> routing,
			Set<MappingVariable> mappingVariables) {
		Set<Constraint> result = new HashSet<Constraint>();
		// get the source and dest tasks
		Task srcTask = flow.getSourceDTT().getSourceTask();
		Task destTask = flow.getDestinationDTT().getDestinationTask();
		// iterate all resources in the routing
		for (Resource res : routing) {
			if (!res.getId().equals(ResourcePropertyService.getProxyId(res))) {
				// resource is in proxy area
				// get the mappins on the resource
				Set<M> srcMvars = new HashSet<M>();
				Set<M> destMvars = new HashSet<M>();
				for (MappingVariable mVar : mappingVariables) {
					M mV = (M) mVar;
					if (mV.getMapping().getTarget().equals(res)) {
						if (mV.getMapping().getSource().equals(srcTask)) {
							srcMvars.add(mV);
						}
						if (mV.getMapping().getSource().equals(destTask)) {
							destMvars.add(mV);
						}
					}
				}
				if (destMvars.size() > 1) {
					throw new IllegalArgumentException(
							"More than one mapping of task " + destTask + "on resource " + res);
				}
				if (srcMvars.size() > 1) {
					throw new IllegalArgumentException(
							"More than one mapping of task " + srcTask + "on resource " + res);
				}
				Set<DirectedLink> inLinks = new HashSet<Models.DirectedLink>(Models.getInLinks(routing, res));
				Set<DirectedLink> outLinks = new HashSet<Models.DirectedLink>(Models.getOutLinks(routing, res));
				result.add(generateResourceConstraint(srcMvars, destMvars, inLinks, outLinks, flow));
			}
		}
		return result;
	}


	/**
	 * Generates the constraint specifying the correct activation of adjacent edges
	 * for the given resource (inside a proxy area).
	 * 
	 * sum(DDLRR_out) - sum(DDLRR_in) + M_dest - M_src = 0
	 * 
	 * @param srcVars
	 *            the mapping variables of the src task on the resource
	 * @param destVars
	 *            the mapping variables of the dest task on the resource
	 * @param inLinks
	 *            the in links of the resource
	 * @param outLinks
	 *            the out links of the resource
	 * @param flow
	 *            the {@link CommunicationFlow} that is being routed
	 * @return the constraint specifying the correct activation of adjacent edges
	 *         for the given resource (inside a proxy area)
	 */
	protected Constraint generateResourceConstraint(Set<M> srcVars, Set<M> destVars, Set<DirectedLink> inLinks,
			Set<DirectedLink> outLinks, CommunicationFlow flow) {
		Constraint result = new Constraint(Operator.EQ, 0);
		for (DirectedLink outLink : outLinks) {
			DDLRR linkVar = Variables.varDDLRR(flow, outLink);
			result.add(Variables.p(linkVar));
		}
		for (DirectedLink inLink : inLinks) {
			DDLRR linkVar = Variables.varDDLRR(flow, inLink);
			result.add(-1, Variables.p(linkVar));
		}
		for (M srcMVar : srcVars) {
			result.add(-1, Variables.p(srcMVar));
		}
		for (M destMVar : destVars) {
			result.add(Variables.p(destMVar));
		}
		return result;
	}
}