package net.sf.opendse.encoding.preprocessing;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

public class RoutingSearchTest {

	@Test(expected=IllegalArgumentException.class)
	public void testFindShortestPathNoPath() {
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		
		Link l0 = new Link("l0");
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		RoutingSearch.findShortestPath(r0, r2, arch);
	}
	
	@Test
	public void testFindShortestPath() {
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		Link l5 = new Link("l5");
		Link l6 = new Link("l6");
		
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r2, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l3, r0, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l4, r0, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l5, r1, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l6, r3, r4, EdgeType.UNDIRECTED);
		
		Architecture<Resource, Link> shortest = RoutingSearch.findShortestPath(r0, r4, arch);
		assertEquals(3, shortest.getVertexCount());
		assertEquals(2, shortest.getEdgeCount());
		assertTrue(shortest.containsEdge(l3));
		assertTrue(shortest.containsEdge(l6));
		assertTrue(shortest.containsVertex(r0));
		assertTrue(shortest.containsVertex(r3));
		assertTrue(shortest.containsVertex(r4));
	}
}
