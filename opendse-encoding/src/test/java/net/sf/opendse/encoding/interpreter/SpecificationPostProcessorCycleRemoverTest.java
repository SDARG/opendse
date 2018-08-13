package net.sf.opendse.encoding.interpreter;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

public class SpecificationPostProcessorCycleRemoverTest {

	@Test
	public void test() {
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Communication comm = new Communication("comm");
		appl.addEdge(new Dependency("d0"), t0, comm, EdgeType.DIRECTED);
		appl.addEdge(new Dependency("d1"), comm, t1, EdgeType.DIRECTED);

		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource res0 = new Resource("r0");
		Resource res1 = new Resource("r1");
		Resource res2 = new Resource("r2");
		Resource res3 = new Resource("r3");
		Resource res4 = new Resource("r4");

		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		Link l5 = new Link("l5");

		arch.addEdge(l0, res0, res1, EdgeType.UNDIRECTED);
		arch.addEdge(l1, res2, res3, EdgeType.UNDIRECTED);
		arch.addEdge(l2, res3, res4, EdgeType.UNDIRECTED);
		arch.addEdge(l3, res2, res4, EdgeType.UNDIRECTED);
		arch.addEdge(l4, res0, res2, EdgeType.UNDIRECTED);
		arch.addEdge(l5, res1, res3, EdgeType.UNDIRECTED);

		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, res0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, res1);
		mappings.add(m0);
		mappings.add(m1);

		Routings<Task, Resource, Link> routings = new Routings<Task, Resource, Link>();
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addEdge(l0, res0, res1, EdgeType.DIRECTED);
		routing.addEdge(l1, res2, res3, EdgeType.DIRECTED);
		routing.addEdge(l2, res3, res4, EdgeType.DIRECTED);
		routing.addEdge(l3, res2, res4, EdgeType.DIRECTED);
		routings.set(comm, routing);

		Specification spec = new Specification(appl, arch, mappings, routings);
		SpecificationPostProcessorCycleRemover cycleRemover = new SpecificationPostProcessorCycleRemover(
				new SpecificationPostProcessorMulti());
		cycleRemover.postProcessImplementation(spec);

		assertTrue(spec.getRoutings().get(comm).containsVertex(res0));
		assertTrue(spec.getRoutings().get(comm).containsVertex(res1));
		assertFalse(spec.getRoutings().get(comm).containsVertex(res2));
		assertFalse(spec.getRoutings().get(comm).containsVertex(res3));
		assertFalse(spec.getRoutings().get(comm).containsVertex(res4));
	}
}
