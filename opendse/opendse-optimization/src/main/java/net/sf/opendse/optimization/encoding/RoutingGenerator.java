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

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.ICommunication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;

/**
 * The {@code RoutingGenerator} contains several methods to generate and
 * transform routings.
 * 
 * @author lukasiewycz
 * 
 */
public class RoutingGenerator {

	/**
	 * Construct a {@code RoutingGenerator} with a random seed.
	 */
	public RoutingGenerator() {
	}

	/**
	 * Create full routings graphs.
	 * 
	 * @param application
	 *            the application
	 * @param architecture
	 *            the architecture
	 * @return the routings
	 */
	public Routings<Task, Resource, Link> fill(Application<Task, Dependency> application,
			Architecture<Resource, Link> architecture) {
		Routings<Task, Resource, Link> routings = new Routings<Task, Resource, Link>();

		for (Task task : application) {
			if (task instanceof ICommunication) {
				Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
				
				for (Resource resource: architecture.getVertices()){
					routing.addVertex(resource);
				}
				for (Link link : architecture.getEdges()) {
					routing.addEdge(link, architecture.getEndpoints(link), architecture.getEdgeType(link));
				}
				routings.set(task, routing);
			}
		}

		return routings;
	}

}
