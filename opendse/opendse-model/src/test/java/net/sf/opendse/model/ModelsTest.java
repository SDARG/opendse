package net.sf.opendse.model;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

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
			Assert.assertFalse(visited);
			Assert.assertEquals(c, t);
			Assert.assertNotEquals(task, t);
			visited = true;
		}
		Assert.assertTrue(visited);
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
			Assert.assertFalse(visited);
			Assert.assertNotEquals(c, t);
			Assert.assertEquals(task, t);
			visited = true;
		}
		Assert.assertTrue(visited);
	}

	@Test
	public void isProcess() {
		Communication communication = new Communication("c");
		Task task = new Task("t");

		Assert.assertFalse(Models.isProcess(communication));
		Assert.assertTrue(Models.isProcess(task));
	}

	@Test
	public void isCommunication() {
		Communication communication = new Communication("c");
		Task task = new Task("t");

		Assert.assertTrue(Models.isCommunication(communication));
		Assert.assertFalse(Models.isCommunication(task));
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

		Assert.assertEquals(1, spec2.getApplication().getVertexCount());
		Assert.assertTrue(spec2.getApplication().containsVertex(task));

		Assert.assertEquals(1, spec2.getArchitecture().getVertexCount());
		Assert.assertTrue(spec2.getArchitecture().containsVertex(resource));

		Assert.assertEquals(1, spec2.getMappings().size());
		Assert.assertEquals(mapping, spec2.getMappings().get(task).iterator().next());
		Assert.assertEquals(mapping, spec2.getMappings().get(resource).iterator().next());
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

		Assert.assertEquals(2, spec2.getApplication().getVertexCount());
		Assert.assertTrue(spec2.getApplication().containsVertex(task1));
		Assert.assertTrue(spec2.getApplication().containsVertex(task2));
		Assert.assertTrue(spec2.getApplication().isNeighbor(task1, task2));
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

		Assert.assertEquals(2, spec2.getArchitecture().getVertexCount());
		Assert.assertTrue(spec2.getArchitecture().isNeighbor(resource1, resource2));

	}
}
