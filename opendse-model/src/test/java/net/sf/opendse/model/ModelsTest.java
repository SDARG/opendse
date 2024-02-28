package net.sf.opendse.model;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class ModelsTest {
	@Test
	public void filterCommunications() {
		Set<Task> tasks = new HashSet<Task>();
		Communication c = new Communication("c");
		Task task = new Task("t");
		tasks.add(task);
		tasks.add(c);

		boolean visited = false;
		for (Task t : Models.filterCommunications(tasks)) {
			Assertions.assertFalse(visited);
			Assertions.assertEquals(c, t);
			Assertions.assertNotEquals(task, t);
			visited = true;
		}
		Assertions.assertTrue(visited);
	}

	@Test
	public void filterProcesses() {
		Set<Task> tasks = new HashSet<Task>();
		Communication c = new Communication("c");
		Task task = new Task("t");
		tasks.add(task);
		tasks.add(c);

		boolean visited = false;
		for (Task t : Models.filterProcesses(tasks)) {
			Assertions.assertFalse(visited);
			Assertions.assertNotEquals(c, t);
			Assertions.assertEquals(task, t);
			visited = true;
		}
		Assertions.assertTrue(visited);
	}

	@Test
	public void isProcess() {
		Communication communication = new Communication("c");
		Task task = new Task("t");

		Assertions.assertFalse(Models.isProcess(communication));
		Assertions.assertTrue(Models.isProcess(task));
	}

	@Test
	public void isCommunication() {
		Communication communication = new Communication("c");
		Task task = new Task("t");

		Assertions.assertTrue(Models.isCommunication(communication));
		Assertions.assertFalse(Models.isCommunication(task));
	}

	@Test
	public void copy() {
		Application<Task, Dependency> app = new Application<Task, Dependency>();
		Task task = new Task("t");
		app.addVertex(task);

		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource resource = new Resource("r");
		arch.addVertex(resource);

		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", task, resource);
		mappings.add(mapping);

		Specification spec = new Specification(app, arch, mappings);
		Specification spec2 = Models.copy(spec);

		Assertions.assertEquals(1, spec2.getApplication().getVertexCount());
		Assertions.assertTrue(spec2.getApplication().containsVertex(task));

		Assertions.assertEquals(1, spec2.getArchitecture().getVertexCount());
		Assertions.assertTrue(spec2.getArchitecture().containsVertex(resource));

		Assertions.assertEquals(1, spec2.getMappings().size());
		Assertions.assertEquals(mapping, spec2.getMappings().get(task).iterator().next());
		Assertions.assertEquals(mapping, spec2.getMappings().get(resource).iterator().next());
	}

	@Test
	public void copyWithDependencies() {
		Application<Task, Dependency> app = new Application<Task, Dependency>();
		Task task1 = new Task("t1");
		Task task2 = new Task("t2");
		Dependency dependency = new Dependency("d");
		app.addVertex(task1);
		app.addVertex(task2);
		app.addEdge(dependency, task1, task2);

		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();

		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();

		Specification spec = new Specification(app, arch, mappings);
		Specification spec2 = Models.copy(spec);

		Assertions.assertEquals(2, spec2.getApplication().getVertexCount());
		Assertions.assertTrue(spec2.getApplication().containsVertex(task1));
		Assertions.assertTrue(spec2.getApplication().containsVertex(task2));
		Assertions.assertTrue(spec2.getApplication().isNeighbor(task1, task2));
	}

	@Test
	public void copyWithLinks() {
		Application<Task, Dependency> app = new Application<Task, Dependency>();

		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource resource1 = new Resource("r1");
		Resource resource2 = new Resource("r2");
		Link link = new Link("l");
		arch.addVertex(resource1);
		arch.addVertex(resource2);
		arch.addEdge(link, resource1, resource2);

		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();

		Specification spec = new Specification(app, arch, mappings);
		Specification spec2 = Models.copy(spec);

		Assertions.assertEquals(2, spec2.getArchitecture().getVertexCount());
		Assertions.assertTrue(spec2.getArchitecture().isNeighbor(resource1, resource2));

	}

	@Test
	public void cloneApplication() {
		Application<Task, Dependency> app = new Application<Task, Dependency>();
		Task task1 = new Task("t1");
		Task task2 = new Task("t2");
		Dependency dependency = new Dependency("d");
		app.addVertex(task1);
		app.addVertex(task2);
		app.addEdge(dependency, task1, task2);

		Application<Task, Dependency> app2 = Models.clone(app);
		Assertions.assertEquals(2, app2.getVertexCount());
		Assertions.assertTrue(app2.containsVertex(task1));
		Assertions.assertTrue(app2.getVertex(task1) == task1);
		Assertions.assertTrue(app2.containsVertex(task2));
		Assertions.assertTrue(app2.isNeighbor(task1, task2));
		Assertions.assertTrue(app2.getSuccessors(task1).iterator().hasNext());
		Assertions.assertTrue(app2.getOutEdges(task1).iterator().hasNext());
		Assertions.assertTrue(app2.getOutEdges(task1).iterator().next() == dependency);
	}
}
