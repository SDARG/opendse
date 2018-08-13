package net.sf.opendse.encoding.preprocessing;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.TaskPropertyService;

/**
 * The {@link ProxySearchReduction} removes the proxy links from the architecture.
 * 
 * @author Fedor Smirnov
 *
 */
public class ProxySearchReduction extends ProxySearch {

	@Inject
	public ProxySearchReduction(SpecificationPreprocessorMulti multiPreprocessor) {
		super(multiPreprocessor);
	}
	
	@Override
	public void preprocessSpecification(Specification userSpecification) {
		super.preprocessSpecification(userSpecification);
		Architecture<Resource, Link> arch = userSpecification.getArchitecture();
		Set<Link> toRemove = new HashSet<Link>();
		for (Link link : arch.getEdges()) {
			if (!ArchitectureElementPropertyService.getOffersRoutingVariety(link)) {
				toRemove.add(link);
			}
		}
		for (Task t : userSpecification.getApplication()) {
			if(TaskPropertyService.isCommunication(t)) {
				Architecture<Resource, Link> routing = userSpecification.getRoutings().get(t);
				for (Link l : toRemove) {
					routing.removeEdge(l);
				}
			}
		}
	}
}
