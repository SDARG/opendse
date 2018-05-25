package net.sf.opendse.encoding.preprocessing;

import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.encoding.SpecificationPreprocessor;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;

/**
 * The proxy search is used to identify the links and resources that offer no
 * routing variety and to mark their proxy resources.
 * 
 * @author Fedor Smirnov
 *
 */
public class ProxySearch implements SpecificationPreprocessor {

	@Override
	public Specification preprocessSpecification(Specification userSpecification) {
		Architecture<Resource, Link> arch = userSpecification.getArchitecture();
		Set<Resource> varietyResources = new HashSet<Resource>(arch.getVertices());
		int varResNum = 0;
		while (varResNum != varietyResources.size()) {
			varResNum = varietyResources.size();
			Set<Resource> proxiedResources = findProxiedResources(arch, varietyResources);
			varietyResources.removeAll(proxiedResources);
		}
		return new Specification(userSpecification.getApplication(), arch, userSpecification.getMappings());
	}

	/**
	 * Find all resources that can only be reached by one single path (without using
	 * the links already marked as not offering variety). Mark their proxies and
	 * their links. Update the proxies of all resources pointing to the newly found
	 * proxies.
	 * 
	 * @param arch
	 *            the {@link Architecture}
	 * @param varietyResources
	 *            the {@link Resource} that are currently assumed to be offering
	 *            variety
	 * @return the set of resources that can be reached by only one communication
	 *         path
	 */
	protected Set<Resource> findProxiedResources(Architecture<Resource, Link> arch, Set<Resource> varietyResources) {
		Set<Resource> proxiedResources = new HashSet<Resource>();
		for (Resource res : varietyResources) {
			Resource proxy = findProxy(arch, res);
			if (!proxy.equals(res)) {
				proxiedResources.add(res);
				ResourcePropertyService.setProxyId(res, proxy);
				updateProxies(arch, res);
				markProxyLinks(arch, res);
			}
		}
		return proxiedResources;
	}

	/**
	 * Returns the proxy resource to the given resource. Returns the resource itself
	 * if the resource does not have a proxy.
	 * 
	 * @param arch
	 *            the {@link Architecture}
	 * @param resource
	 *            the {@link Resource} that is to be checked
	 * @return the proxy resource to the given resource. Returns the resource itself
	 *         if the resource does not have a proxy
	 */
	protected Resource findProxy(Architecture<Resource, Link> arch, Resource resource) {
		Set<Link> varietyEdges = new HashSet<Link>();
		for (Link link : arch.getIncidentEdges(resource)) {
			if (ArchitectureElementPropertyService.getOffersRoutingVariety(link)) {
				varietyEdges.add(link);
			}
		}
		if (varietyEdges.isEmpty()) {
			throw new IllegalArgumentException("Unconnected resource found");
		}
		if (varietyEdges.size() > 1) {
			// more than one variety edge => no proxies
			return resource;
		}
		// exactly one variety edge => its end point can be used as proxy
		return arch.getOpposite(resource, varietyEdges.iterator().next());
	}

	/**
	 * Update the proxy relations so that no proxy edge points to a resource that
	 * has a proxy itself.
	 * 
	 * @param arch
	 *            the {@link Architecture}
	 * @param res
	 *            the {@link Resource} that has been updated
	 */
	protected void updateProxies(Architecture<Resource, Link> arch, Resource res) {
		Resource proxy = arch.getVertex(ResourcePropertyService.getProxyId(res));
		for (Resource r : arch) {
			if (ResourcePropertyService.getProxyId(r).equals(res.getId())) {
				ResourcePropertyService.setProxyId(r, proxy);
			}
		}
	}

	/**
	 * Mark the links adjacent to the newly found proxy resource as offering no
	 * routing variety.
	 * 
	 * @param arch
	 *            the {@link Architecture}
	 * @param res
	 *            the newly found proxy {@link Resource}
	 */
	protected void markProxyLinks(Architecture<Resource, Link> arch, Resource res) {
		Set<Link> unmarked = new HashSet<Link>();
		for (Link l : arch.getIncidentEdges(res)) {
			if (ArchitectureElementPropertyService.getOffersRoutingVariety(l)) {
				unmarked.add(l);
			}
		}
		if (unmarked.size() != 1) {
			throw new IllegalArgumentException("A newly found proxy should have exactly one unmarked edge.");
		}
		ArchitectureElementPropertyService.setOfferRoutingVariety(unmarked.iterator().next(), false);
	}
}
