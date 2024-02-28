package net.sf.opendse.model;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class GraphTest {

	@Test
	public void addVertex() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		app.addVertex(task);

		Assertions.assertTrue(app.containsVertex(task));
	}

	@Test
	public void getVertex() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		app.addVertex(task);

		Assertions.assertEquals(task, app.getVertex(task));
	}

	@Test
	public void getVertexPerId() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		app.addVertex(task);

		Assertions.assertEquals(task, app.getVertex("x"));
	}

	@Test
	public void getVertexUnkownId() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Assertions.assertNull(app.getVertex("y"));
	}

	@Test
	public void getEdge() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		Task task2 = new Task("y");
		Dependency e = new Dependency("e");
		app.addEdge(e, task, task2);

		Assertions.assertEquals(e, app.getEdge(e));
		Assertions.assertTrue(app.containsVertex(task));
		Assertions.assertTrue(app.containsVertex(task2));
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

		Assertions.assertEquals(e, app.getEdge("e"));
	}

	@Test
	public void getEdgeUnkownId() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Assertions.assertNull(app.getEdge("y"));
	}

	@Test
	public void removeVertex() {
		Graph<Task, Dependency> app = new Graph<Task, Dependency>();
		Task task = new Task("x");
		app.addVertex(task);

		Assertions.assertTrue(app.removeVertices(Collections.singleton(task)));
		Assertions.assertEquals(0, app.getVertexCount());
	}
}
