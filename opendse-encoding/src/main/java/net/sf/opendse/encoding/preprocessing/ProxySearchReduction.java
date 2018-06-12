package net.sf.opendse.encoding.preprocessing;

import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;

/**
 * The {@link ProxySearchReduction} removes the proxy links from the architecture.
 * 
 * @author Fedor Smirnov
 *
 */
public class ProxySearchReduction extends ProxySearch {

	@Override
	public Specification preprocessSpecification(Specification userSpecification) {
		Specification annotatedSpec = super.preprocessSpecification(userSpecification);
		Architecture<Resource, Link> arch = annotatedSpec.getArchitecture();
		Set<Link> toRemove = new HashSet<Link>();
		for (Link link : arch.getEdges()) {
			if (!ArchitectureElementPropertyService.getOffersRoutingVariety(link)) {
				toRemove.add(link);
			}
		}
		for (Link l : toRemove) {
			arch.removeEdge(l);
		}
		return new Specification(annotatedSpec.getApplication(), arch, annotatedSpec.getMappings());
	}
}
