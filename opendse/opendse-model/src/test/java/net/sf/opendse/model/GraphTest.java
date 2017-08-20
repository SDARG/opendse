package net.sf.opendse.model;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class GraphTest {

	@Test
	public void addVertex() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		app.addVertex(task);

		Assert.assertTrue(app.containsVertex(task));
	}

	@Test
	public void getVertex() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		app.addVertex(task);

		Assert.assertEquals(task, app.getVertex(task));
	}

	@Test
	public void getVertexPerId() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		app.addVertex(task);

		Assert.assertEquals(task, app.getVertex("x"));
	}

	@Test
	public void getVertexUnkownId() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Assert.assertNull(app.getVertex("y"));
	}

	@Test
	public void getEdge() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		Task task2 = new Task("y");
		Dependency e = new Dependency("e");
		app.addEdge(e, task, task2);

		Assert.assertEquals(e, app.getEdge(e));
		Assert.assertTrue(app.containsVertex(task));
		Assert.assertTrue(app.containsVertex(task2));
	}

	@Test
	public void getEdgePerId() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		Task task2 = new Task("y");
		Dependency e = new Dependency("e");
		app.addVertex(task);
		app.addVertex(task2);
		app.addEdge(e, task, task2);

		Assert.assertEquals(e, app.getEdge("e"));
	}

	@Test
	public void getEdgeUnkownId() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Assert.assertNull(app.getEdge("y"));
	}

	@Test
	public void removeVertex() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		app.addVertex(task);

		Assert.assertTrue(app.removeVertices(Collections.singleton(task)));
		Assert.assertEquals(0, app.getVertexCount());
	}
}
