/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
package net.sf.opendse.optimization.encoding;

import static net.sf.opendse.model.Models.filterCommunications;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.constraints.SpecificationRouterConstraints;

/**
 * The {@code RoutingFilter} uses a simple graph-based approach to remove
 * unreachable vertices from routings.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class RoutingFilter {

	public static void filter(Specification specification) {
		Application<Task, Dependency> application = specification.getApplication();
		Routings<Task, Resource, Link> routings = specification.getRoutings();
		Mappings<Task, Resource> mappings = specification.getMappings();

		
		for (Task c : filterCommunications(application)) {
			Collection<Task> neighborTasks = application.getNeighbors(c);

			Set<Resource> incidentResources = new HashSet<Resource>();
			for(Task inTask: neighborTasks){
				incidentResources.addAll(mappings.getTargets(inTask));
			}
			
			Architecture<Resource, Link> routing = routings.get(c);
			Set<Resource> toRemove = new HashSet<Resource>();
			
			for(Resource resource: routing){
				if(!incidentResources.contains(resource)){
					if(incidentResources.size() <= 1){
						toRemove.add(resource);		
					} else if(!SpecificationRouterConstraints.isRouted(resource, c)){
						toRemove.add(resource);
					}
				}
			}
			routing.removeVertices(toRemove);
			
			
			
		}

		for (Task c : filterCommunications(application)) {
			Architecture<Resource, Link> routing = routings.get(c);

			Set<Resource> resources = new HashSet<Resource>();

			for (Task pred : application.getPredecessors(c)) {
				resources.addAll(mappings.getTargets(pred));
			}
			for (Task succ : application.getSuccessors(c)) {
				resources.addAll(mappings.getTargets(succ));
			}

			Set<Resource> remove = new HashSet<Resource>();
			do {
				remove.clear();
				for (Resource r : routing) {
					if (!resources.contains(r)) {
						Set<Resource> preds = new HashSet<Resource>(routing.getPredecessors(r));
						Set<Resource> succs = new HashSet<Resource>(routing.getSuccessors(r));

						// System.out.println(preds+" "+succs);

						if (preds.isEmpty() || succs.isEmpty() || (preds.size() == 1 && preds.equals(succs))) {
							remove.add(r);
						}
					}
				}
				for (Resource r : remove) {
					routing.removeVertex(r);
				}

			} while (!remove.isEmpty());
		}
	}

}
