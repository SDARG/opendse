package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;
import org.opt4j.satdecoding.Term;

import net.sf.opendse.encoding.constraints.Constraints;
import net.sf.opendse.encoding.preprocessing.ProxyRoutings;
import net.sf.opendse.encoding.preprocessing.ProxyRoutingsShortestPath;
import net.sf.opendse.encoding.variables.DDLRR;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
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
		ProxyRoutings proxyRoutings = new ProxyRoutingsShortestPath(routing);
		Set<Constraint> result = new HashSet<Constraint>();
		// finds the mapping variables for the src and the dest task
		Task srcTask = flow.getSourceDTT().getSourceTask();
		Task destTask = flow.getDestinationDTT().getDestinationTask();
		Set<M> srcMapVars = new HashSet<M>();
		Set<M> destMapVars = new HashSet<M>();
		for (MappingVariable var : mappingVariables) {
			if (var instanceof M) {
				M mVar = (M) var;
				if (mVar.getMapping().getSource().equals(srcTask))
					srcMapVars.add(mVar);
				if (mVar.getMapping().getSource().equals(destTask))
					destMapVars.add(mVar);
			}
		}
		
		// process all pairs of src- and dest-mappings
		for (M srcM : srcMapVars) {
			for (M destM : destMapVars) {
				Set<DirectedLink> usedLinks = new HashSet<Models.DirectedLink>();
				Set<DirectedLink> unUsedLinks = new HashSet<Models.DirectedLink>(proxyRoutings.getInvariantLinks());
				
				Resource srcRes = srcM.getMapping().getTarget();
				Resource destRes = destM.getMapping().getTarget();
				String srcProxyId = ResourcePropertyService.getProxyId(srcRes);
				String destProxyId = ResourcePropertyService.getProxyId(destRes);
				
				if (srcRes.equals(destRes)) {
					// mapped on same res => no links used at all; do nothing
				}else {
					if (srcProxyId.equals(destProxyId)) {
						// same proxy area => use internal links
						usedLinks = new HashSet<Models.DirectedLink>(proxyRoutings.getLinksBetweenResources(srcRes, destRes));
					}else {
						// different proxy areas
						if (!srcProxyId.equals(srcRes.getId())) {
							// src in proxy
							usedLinks.addAll(proxyRoutings.getResourceToProxyLinks(srcRes));
						}
						if (!destProxyId.equals(destRes.getId())) {
							// dest in proxy
							usedLinks.addAll(proxyRoutings.getProxyToResourceLinks(destRes));
						}
					}
				}
				unUsedLinks.removeAll(usedLinks);
				for (DirectedLink dl : usedLinks) {
					Constraint useLink = new Constraint(Operator.LE, 1);
					useLink.add(Variables.p(srcM));
					useLink.add(Variables.p(destM));
					useLink.add(-1, Variables.p(Variables.varDDLRR(flow, dl)));
					result.add(useLink);
				}
				for (DirectedLink dl : unUsedLinks) {
					Constraint doNotUseLink = new Constraint(Operator.LE, 1);
					doNotUseLink.add(Variables.p(srcM));
					doNotUseLink.add(Variables.p(destM));
					doNotUseLink.add(-1, Variables.n(Variables.varDDLRR(flow, dl)));
					result.add(doNotUseLink);
				}
			}
		}
		return result;
	}

	/**
	 * Generates the {@link Constraint} that enforces the deactivation of the
	 * {@link DirectedLink} in cases where non of the relevant mappings are active.
	 * 
	 * @param dirLink
	 *            the {@link DirectedLink} in question
	 * @param commFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param srcMappings
	 *            the set of {@link M} variables encoding the activation of the
	 *            mappings of the source task of the flow
	 * @param destMappings
	 *            srcMappings the set of {@link M} variables encoding the activation
	 *            of the mappings of the destination task of the flow
	 * @return the {@link Constraint} that enforces the deactivation of the
	 *         {@link DirectedLink} in cases where non of the relevant mappings are
	 *         active
	 */
	protected Constraint processDirectedLink(DirectedLink dirLink, CommunicationFlow commFlow, Set<M> srcMappings,
			Set<M> destMappings, ProxyRoutings proxyRoutings) {
		Set<Resource> relevantResourcesSource = proxyRoutings.getRelevantSourceResources(dirLink);
		Set<Resource> relevantResourcesDestination = proxyRoutings.getRelevantDestinationResources(dirLink);
		Set<M> relevantVariables = new HashSet<M>();
		for (M srcM : srcMappings) {
			if (relevantResourcesSource.contains(srcM.getMapping().getTarget())) {
				relevantVariables.add(srcM);
			}
		}
		for (M destM : destMappings) {
			if (relevantResourcesDestination.contains(destM.getMapping().getTarget())) {
				relevantVariables.add(destM);
			}
		}
		return Constraints.generateMinimalRequirementConstraint(relevantVariables,
				Variables.varDDLRR(commFlow, dirLink));
	}

	/**
	 * Generates the {@link Constraint}s stating that the path from the mapping
	 * source to the mapping destination has to be active inside the proxy area if
	 * both mappings are active.
	 * 
	 * @param srcM
	 *            the {@link Variable} encoding the activation of the src mapping
	 * @param destM
	 *            the {@link Variable} encoding the activation of a dest mapping
	 *            inside the same proxy area
	 * @param commFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * 
	 * @return the {@link Constraint}s stating that the path from the mapping source
	 *         to the mapping destination has to be active inside the proxy area if
	 *         both mappings are active
	 */
	protected Set<Constraint> generateInternalRoutings(M srcM, M destM, CommunicationFlow commFlow,
			ProxyRoutings proxyRoutings) {
		Set<Constraint> result = new HashSet<Constraint>();
		Resource src = srcM.getMapping().getTarget();
		Resource dest = destM.getMapping().getTarget();
		// get the links used for the connections of the source to the destination
		// (always active if the stuff is active)
		Set<DirectedLink> usedLinks = proxyRoutings.getLinksBetweenResources(src, dest);
		for (DirectedLink dl : usedLinks) {
			Constraint useLink = new Constraint(Operator.LE, 1);
			useLink.add(Variables.p(srcM));
			useLink.add(Variables.p(destM));
			useLink.add(-1, Variables.p(Variables.varDDLRR(commFlow, dl)));
			result.add(useLink);
		}
		// get the links that are not used for the connection (always inactive if the
		// stuff is inactive)
		Set<DirectedLink> unusedLinks = new HashSet<Models.DirectedLink>(proxyRoutings.getInvariantLinks());
		unusedLinks.removeAll(usedLinks);
		for (DirectedLink dl : unusedLinks) {
			Constraint doNotUseLink = new Constraint(Operator.LE, 1);
			doNotUseLink.add(Variables.p(srcM));
			doNotUseLink.add(Variables.p(destM));
			doNotUseLink.add(-1, Variables.n(Variables.varDDLRR(commFlow, dl)));
			result.add(doNotUseLink);
		}
		return result;
	}

	/**
	 * Generates the {@link Constraint}s stating that the path from the proxy to the
	 * mapping destination has to be active if the destination mapping is active and
	 * the source mapping lies outside the proxy area.
	 * 
	 * 
	 * @param destM
	 *            the {@link Variable} encoding the activation of the destination
	 *            mapping
	 * @param neighborSrcMs
	 *            the set of the {@link Variable}s encoding the activation of source
	 *            mappings inside the same proxy area
	 * @param commFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @return {@link Constraint}s stating that the path from the proxy to the
	 *         destination mapping has to be active if the destination mapping is
	 *         active and the source mapping lies outside the proxy area
	 */
	protected Set<Constraint> generateProxyToDestinationConstraints(M destM, Set<M> neighborSrcMs,
			CommunicationFlow commFlow, ProxyRoutings proxyRoutings) {
		return generateProxyRoutingConstraints(destM, neighborSrcMs, commFlow, false, proxyRoutings);
	}

	/**
	 * Generates the {@link Constraint}s stating that the path from the mapping
	 * source to the proxy has to be active if the source mapping is active and the
	 * destination mapping lies outside the proxy area.
	 * 
	 * 
	 * @param srcM
	 *            the {@link Variable} encoding the activation of the source mapping
	 * @param neighborDestMs
	 *            the set of the {@link Variable}s encoding the activation of dest
	 *            mappings inside the same proxy area
	 * @param commFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @return {@link Constraint}s stating that the path from the mapping source to
	 *         the proxy has to be active if the source mapping is active and the
	 *         destination mapping lies outside the proxy area
	 */
	protected Set<Constraint> generateSourceToProxyConstraint(M srcM, Set<M> neighborDestMs, CommunicationFlow commFlow,
			ProxyRoutings proxyRoutings) {
		return generateProxyRoutingConstraints(srcM, neighborDestMs, commFlow, true, proxyRoutings);
	}

	/**
	 * Generates the constraints that activate the links between an end point and
	 * the proxy, if exactly one end point mapping (source or destination) is active
	 * within the proxy area.
	 * 
	 * @param endPointMapping
	 *            the {@link Variable} encoding the activation of the end point
	 *            mapping
	 * @param neighborMappings
	 *            the set of the {@link Variable}s encoding the activation of
	 *            mappings within the same proxy area
	 * @param commFlow
	 *            the {@link CommunicationFlow} that is being routed
	 * @param source
	 *            {@code true} if the end point mapping maps the source of the
	 *            commFlow, {@code false} if it maps its destination
	 * @return the constraints that activate the links between an end point and the
	 *         proxy, if exactly one end point mapping (source or destination) is
	 *         active within the proxy area
	 */
	protected Set<Constraint> generateProxyRoutingConstraints(M endPointMapping, Set<M> neighborMappings,
			CommunicationFlow commFlow, boolean source, ProxyRoutings proxyRoutings) {
		Set<Constraint> result = new HashSet<Constraint>();
		Resource resource = endPointMapping.getMapping().getTarget();
		Set<DirectedLink> directedLinks = source ? proxyRoutings.getResourceToProxyLinks(resource)
				: proxyRoutings.getProxyToResourceLinks(resource);
		for (DirectedLink dirLink : directedLinks) {
			// not(M_end) + sum((M_neighbor)) - not(DDLRR) >= 0
			DDLRR linkVar = Variables.varDDLRR(commFlow, dirLink);
			Constraint c = new Constraint(Operator.GE, 0);
			c.add(new Term(-1, net.sf.opendse.optimization.encoding.variables.Variables.n(linkVar)));
			c.add(Variables.n(endPointMapping));
			for (M neighborMapping : neighborMappings) {
				c.add(Variables.p(neighborMapping));
			}
			result.add(c);
		}
		Set<DirectedLink> unusedLinks = new HashSet<Models.DirectedLink>(
				proxyRoutings.getProxyLinks(ResourcePropertyService.getProxyId(resource)));
		unusedLinks.removeAll(directedLinks);
		for (DirectedLink dirLink : unusedLinks) {
			// not(M_end) + sum((M_neighbor)) - (DDLRR) >= 0
			DDLRR linkVar = Variables.varDDLRR(commFlow, dirLink);
			Constraint c = new Constraint(Operator.GE, 0);
			c.add(new Term(-1, net.sf.opendse.optimization.encoding.variables.Variables.p(linkVar)));
			c.add(Variables.n(endPointMapping));
			for (M neighborMapping : neighborMappings) {
				c.add(Variables.p(neighborMapping));
			}
			result.add(c);
		}
		return result;
	}
}
