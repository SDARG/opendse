package net.sf.opendse.tutorial;

import net.sf.opendse.io.SpecificationWriter;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;
import net.sf.opendse.optimization.encoding.SingleImplementation;
import net.sf.opendse.visualization.SpecificationViewer;

public class Part4 {

	public static void main(String[] args) {

		/*
		 */
		Application<Task, Dependency> application = new Application<Task, Dependency>();
		Task t1 = new Task("t1");
		Communication c1 = new Communication("c1");
		Task t2 = new Task("t2");
		application.addVertex(t1);
		application.addVertex(t2);
		application.addVertex(c1);
		application.addEdge(new Dependency("d1"), t1, c1);
		application.addEdge(new Dependency("d2"), c1, t2);

		Task t3 = new Task("t3");
		Communication c2 = new Communication("c2");
		Task t4 = new Task("t4");
		Task t5 = new Task("t5");
		Communication c3 = new Communication("c3");
		Task t6 = new Task("t6");
		application.addVertex(t3);
		application.addVertex(t4);
		application.addVertex(t5);
		application.addVertex(c2);
		application.addEdge(new Dependency("d3"), t3, c2);
		application.addEdge(new Dependency("d4"), c2, t4);
		application.addEdge(new Dependency("d5"), c2, t5);
		application.addEdge(new Dependency("d6"), t5, c3);
		application.addEdge(new Dependency("d7"), c3, t6);

		/*
		 * It is possible to define any kind of attribute that specifies
		 * requirements like bus load, memory requirements, etc.
		 */
		for (Task task : application) {
			if (task instanceof Communication) {
				task.setAttribute("utilization", 10);
			} else {
				task.setAttribute("memory", 5);
			}
		}

		/*
		 */
		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();
		Resource r1 = new Resource("r1");
		r1.setAttribute("costs", 100);
		Resource r2 = new Resource("r2");
		r2.setAttribute("costs", 50);
		Resource r3 = new Resource("r3");
		r1.setAttribute("costs", 70);
		Resource bus1 = new Resource("bus1");
		bus1.setAttribute("costs", 20);
		Resource bus2 = new Resource("bus2");
		bus2.setAttribute("costs", 20);

		architecture.addVertex(r1);
		architecture.addVertex(r1);
		architecture.addVertex(r3);
		architecture.addVertex(bus1);
		architecture.addVertex(bus2);

		architecture.addEdge(new Link("l1"), r1, bus1);
		architecture.addEdge(new Link("l2"), r2, bus1);
		architecture.addEdge(new Link("l3"), r1, bus2);
		architecture.addEdge(new Link("l4"), r2, bus2);
		architecture.addEdge(new Link("l5"), r3, bus2);

		/*
		 * It is possible to define connection constraints for resources. Here,
		 * we define that each link is of type "cc" and r1 can only connect to
		 * one of these links while r2 and r3 can connect to at most 2 of these
		 * links.
		 */
		for (Link link : architecture.getEdges()) {
			link.setType("cc");
		}
		r1.setAttribute("cc" + SpecificationConstraints.CONNECT_MAX, 1);
		r2.setAttribute("cc" + SpecificationConstraints.CONNECT_MAX, 2);
		r3.setAttribute("cc" + SpecificationConstraints.CONNECT_MAX, 2);

		/*
		 * It is possible to define capacity constraints for resources. Here, we
		 * define a capacity constraints for our buses using the utilization
		 * value of the communication tasks.
		 */
		bus1.setAttribute("utilization" + SpecificationConstraints.CAPACITY_MAX, 25);
		bus2.setAttribute("utilization" + SpecificationConstraints.CAPACITY_MAX, 25);
		/*
		 * For each resource that is not a bus, we specify to have a given
		 * amount of memory. This memory constraints how many tasks can be
		 * implemented on the specific resource.
		 */
		for (Resource resource : architecture) {
			if (resource.getId().startsWith("r")) {
				resource.setAttribute("memory" + SpecificationConstraints.CAPACITY_MAX, 15);
			}
		}

		/*
		 */
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r1);

		/*
		 * It is also possible to define attributes not per task but per
		 * mapping. Here, the memory requirement for task t1 is overwritten in
		 * case it is mapped with m1.
		 */
		m1.setAttribute("memory", 2);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t2, r2);
		Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t1, r3);
		Mapping<Task, Resource> m4 = new Mapping<Task, Resource>("m4", t2, r3);
		Mapping<Task, Resource> m5 = new Mapping<Task, Resource>("m5", t3, r3);
		Mapping<Task, Resource> m6 = new Mapping<Task, Resource>("m6", t4, r3);
		Mapping<Task, Resource> m7 = new Mapping<Task, Resource>("m7", t5, r2);
		Mapping<Task, Resource> m8 = new Mapping<Task, Resource>("m8", t6, r1);
		mappings.add(m1);
		mappings.add(m2);
		mappings.add(m3);
		mappings.add(m4);
		mappings.add(m5);
		mappings.add(m6);
		mappings.add(m7);
		mappings.add(m8);

		/*
		 */
		Specification specification = new Specification(application, architecture, mappings);

		/*
		 */
		SpecificationWriter writer = new SpecificationWriter();
		writer.write(specification, "specs/Specification4.xml");

		SingleImplementation singleImplementation = new SingleImplementation();
		Specification implementation = singleImplementation.get(specification);

		writer.write(specification, "specs/Implementation4.xml");

		/*
		 */
		SpecificationViewer.view(implementation);

	}

}
