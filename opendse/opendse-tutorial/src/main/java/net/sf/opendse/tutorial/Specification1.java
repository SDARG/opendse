package net.sf.opendse.tutorial;
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


import net.sf.opendse.io.SpecificationWriter;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.visualization.SpecificationViewer;

public class Specification1 {

	public static void main(String[] args) {

		/*
		 * The application is defined by data-dependent tasks. In general, two
		 * tasks have to be implemented either on the same or adjacent
		 * resources.
		 */
		Application<Task, Dependency> application = new Application<Task, Dependency>();
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		application.addVertex(t1);
		application.addVertex(t2);
		application.addEdge(new Dependency("d1"), t1, t2);

		/*
		 * The architecture is defined by resources that can be linked (linked
		 * resources are considered to have a way to communicate). Note that it
		 * is possible to set attributes to each resources like the costs inthis
		 * case. Attributes might be integers, doubles, or strings. It is also
		 * possible to set attributes of tasks, mappings, etc.
		 */
		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();
		Resource r1 = new Resource("r1");
		r1.setAttribute("costs", 100);
		Resource r2 = new Resource("r2");
		r2.setAttribute("costs", 50);
		Link l1 = new Link("l1");
		architecture.addVertex(r1);
		architecture.addVertex(r2);
		architecture.addEdge(l1, r1, r2);

		/*
		 * The mappings define how tasks are mapped to resources. For a
		 * specification is it possible to define more than one possible mapping
		 * for a task such that the optimization selects the optimal mapping.
		 */
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r1);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t2, r2);
		mappings.add(m1);
		mappings.add(m2);

		/*
		 * The specification consists of the application, architecture, and
		 * mappings. Additionally it is possible to specify routings for
		 * communication tasks.
		 */
		Specification specification = new Specification(application, architecture, mappings);

		/*
		 * It is possible to write the specification to a file. Correspondingly,
		 * the class SpecificationReader can read classes.
		 */
		SpecificationWriter writer = new SpecificationWriter();
		writer.write(specification, "specs/spec1.xml");

		/*
		 * It is also possible to view the specification in a GUI.
		 */
		SpecificationViewer.view(specification);

	}
}
