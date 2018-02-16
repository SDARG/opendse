/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
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
