package net.sf.opendse.encoding.interpreter;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.TaskPropertyService;

/**
 * The {@link SpecificationPostProcessorCycleRemover} goes through the routing
 * and manually removes the isolated cycles.
 * 
 * @author Fedor Smirnov
 *
 */
public class SpecificationPostProcessorCycleRemover extends SpecificationPostProcessorComposable {

	@Inject
	public SpecificationPostProcessorCycleRemover(SpecificationPostProcessorMulti multiPostProcessor) {
		multiPostProcessor.addPostProcessor(this);
	}
	
	@Override
	public void postProcessImplementation(Specification implementation) {
		Application<Task, Dependency> appl = implementation.getApplication();
		Routings<Task, Resource, Link> routings = implementation.getRoutings();
		Mappings<Task, Resource> mappings = implementation.getMappings();
		// iterates all routings
		for (Task comm : appl) {
			if (TaskPropertyService.isCommunication(comm)) {
				Architecture<Resource, Link> routing = routings.get(comm);
				// finds the binding targets of the source tasks
				Set<Resource> srcNodes = new HashSet<Resource>();
				for (Task predecessor : appl.getPredecessors(comm)) {
					for (Mapping<Task, Resource> mapping : mappings.get(predecessor)) {
						srcNodes.add(mapping.getTarget());
					}
				}
				// finds all connected nodes
				Set<Resource> connected = new HashSet<Resource>();
				for (Resource srcNode : srcNodes) {
					connected.addAll(findConnected(srcNode, routing));
				}
				// remove all disconnected nodes
				Set<Resource> nodes2remove = new HashSet<Resource>();
				Set<Link> links2remove = new HashSet<Link>();
				for (Resource node : routing) {
					if (!connected.contains(node)) {
						for (Link link : routing.getIncidentEdges(node)) {
							links2remove.add(link);
						}
						nodes2remove.add(node);
					}
				}
				for (Link l : links2remove) {
					routing.removeEdge(l);
				}
				for (Resource res : nodes2remove) {
					routing.removeVertex(res);
				}
			}
		}
	}

	/**
	 * Returns the set of resources that can be reached following the routing from
	 * the given node.
	 * 
	 * @param current
	 *            the current node
	 * @param routing
	 *            the routing graph
	 * @return the set of resources that can be reached following the routing from
	 *         the given node
	 */
	protected Set<Resource> findConnected(Resource current, Architecture<Resource, Link> routing) {
		Set<Resource> result = new HashSet<Resource>();
		result.add(current);
		for (Resource successor : routing.getSuccessors(current)) {
			result.addAll(findConnected(successor, routing));
		}
		return result;
	}
}
