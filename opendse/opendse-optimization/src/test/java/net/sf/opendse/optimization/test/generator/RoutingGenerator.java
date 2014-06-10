package net.sf.opendse.optimization.test.generator;

import java.util.Random;

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
public class RoutingGenerator extends Generator {

	/**
	 * Construct a {@code RoutingGenerator} with a random seed.
	 */
	public RoutingGenerator() {
		this(System.currentTimeMillis());
	}

	/**
	 * Construct a {@code RoutingGenerator} with a random seed.
	 * 
	 * @param seed
	 *            the seed
	 */
	public RoutingGenerator(long seed) {
		super(new Random(seed));
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
