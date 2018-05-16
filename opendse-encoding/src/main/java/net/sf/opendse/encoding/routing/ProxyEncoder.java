package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;
import org.opt4j.satdecoding.Term;

import net.sf.opendse.encoding.constraints.Constraints;
import net.sf.opendse.encoding.preprocessing.ProxyRoutings;
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
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;

/**
 * Encodes the activation of the preprocessed routes based on the chosen
 * mapping.
 * 
 * @author Fedor Smirnov
 *
 */
public class ProxyEncoder implements AdditionalCommFlowEncoder {

	protected final ProxyRoutings proxyRoutings;

	public ProxyEncoder(ProxyRoutings proxyRoutings) {
		this.proxyRoutings = proxyRoutings;
	}

	@Override
	public Set<Constraint> toConstraints(CommunicationFlow flow, Architecture<Resource, Link> routing,
			Set<MappingVariable> mappingVariables) {
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
		// formulates the routing constraints for the source mappings
		for (M srcMvar : srcMapVars) {
			// find all dest mappings inside same proxy area
			Set<M> proxyNeighborMappings = new HashSet<M>();
			String proxyId = ResourcePropertyService.getProxyId(srcMvar.getMapping().getTarget());
			for (M destMvar : destMapVars) {
				if (ResourcePropertyService.getProxyId(destMvar.getMapping().getTarget()).equals(proxyId)) {
					proxyNeighborMappings.add(destMvar);
				}
			}
			// formulates the routing to proxy constraints
			result.addAll(generateSourceToProxyConstraint(srcMvar, proxyNeighborMappings, flow));
			// formulates the internal routing constraints
			for (M neighborDestM : proxyNeighborMappings) {
				result.addAll(generateInternalRoutings(srcMvar, neighborDestM, flow));
			}
		}
		// formulate the constraints for external dest mappings
		for (M destMvar : destMapVars) {
			Set<M> proxyNeighborMappings = new HashSet<M>();
			String proxyId = ResourcePropertyService.getProxyId(destMvar.getMapping().getTarget());
			for (M srcMvar : srcMapVars) {
				if (ResourcePropertyService.getProxyId(srcMvar.getMapping().getTarget()).equals(proxyId)) {
					proxyNeighborMappings.add(srcMvar);
				}
			}
			result.addAll(generateProxyToDestinationConstraints(destMvar, proxyNeighborMappings, flow));
		}
		// formulates the constraints excluding unnecessary links that don't offer
		// routing variety
		for (Link link : routing.getEdges()) {
			if (!ArchitectureElementPropertyService.getOffersRoutingVariety(link)) {
				for (DirectedLink dirLink : Models.getLinks(routing, link)) {
					result.add(processDirectedLink(dirLink, flow, srcMapVars, destMapVars));
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
			Set<M> destMappings) {
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
	protected Set<Constraint> generateInternalRoutings(M srcM, M destM, CommunicationFlow commFlow) {
		Set<Constraint> result = new HashSet<Constraint>();
		Resource src = srcM.getMapping().getTarget();
		Resource dest = srcM.getMapping().getTarget();
		Set<DirectedLink> path = proxyRoutings.getLinksBetweenResources(src, dest);
		Set<M> activationConditions = new HashSet<M>();
		activationConditions.add(srcM);
		activationConditions.add(destM);
		for (DirectedLink dirLink : path) {
			result.add(Constraints.generateDistributedActivationConstraint(activationConditions,
					Variables.varDDLRR(commFlow, dirLink)));
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
			CommunicationFlow commFlow) {
		return generateProxyRoutingConstraints(destM, neighborSrcMs, commFlow, false);
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
	protected Set<Constraint> generateSourceToProxyConstraint(M srcM, Set<M> neighborDestMs,
			CommunicationFlow commFlow) {
		return generateProxyRoutingConstraints(srcM, neighborDestMs, commFlow, true);
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
			CommunicationFlow commFlow, boolean source) {
		Set<Constraint> result = new HashSet<Constraint>();
		Resource resource = endPointMapping.getMapping().getTarget();
		Set<DirectedLink> directedLinks = source ? proxyRoutings.getResourceToProxyLinks(resource)
				: proxyRoutings.getProxyToResourceLinks(resource);
		for (DirectedLink dirLink : directedLinks) {
			// not(M_end) + sum(not(M_neighbor)) - not(DDLRR) >= 0
			DDLRR linkVar = Variables.varDDLRR(commFlow, dirLink);
			Constraint c = new Constraint(Operator.GE, 0);
			c.add(new Term(-1, net.sf.opendse.optimization.encoding.variables.Variables.n(linkVar)));
			c.add(Variables.n(endPointMapping));
			for (M neighborMapping : neighborMappings) {
				c.add(Variables.n(neighborMapping));
			}
			result.add(c);
		}
		return result;
	}
}
