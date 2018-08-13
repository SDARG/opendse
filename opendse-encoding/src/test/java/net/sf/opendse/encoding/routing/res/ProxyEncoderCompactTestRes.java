package net.sf.opendse.encoding.routing.res;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.preprocessing.ProxySearch;
import net.sf.opendse.encoding.preprocessing.SpecificationPreprocessorMulti;
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
import net.sf.opendse.visualization.SpecificationViewer;

public class ProxyEncoderCompactTestRes {

	private ProxyEncoderCompactTestRes() {
	}
	
	public static void main(String[] args) {
		SpecificationViewer.view(makeSpec());
	}
	
	public static Specification makeSpec() {
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Task t3 = new Task("t3");
		Task t4 = new Task("t4");
		Communication c0 = new Communication("c0");
		Communication c1 = new Communication("c1");
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		appl.addEdge(new Dependency("d1"), t0, c0, EdgeType.DIRECTED);
		appl.addEdge(new Dependency("d2"), c0, t1, EdgeType.DIRECTED);
		appl.addEdge(new Dependency("d3"), c0, t2, EdgeType.DIRECTED);
		appl.addEdge(new Dependency("d4"), t3, c1, EdgeType.DIRECTED);
		appl.addEdge(new Dependency("d5"), c1, t4, EdgeType.DIRECTED);

		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Resource r5 = new Resource("r5");
		Resource r6 = new Resource("r6");
		Resource r7 = new Resource("r7");
		Resource r8 = new Resource("r8");
		Resource r9 = new Resource("r9");
		Resource r10 = new Resource("r10");
		Resource r11 = new Resource("r11");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		Link l5 = new Link("l5");
		Link l6 = new Link("l6");
		Link l7 = new Link("l7");
		Link l8 = new Link("l8");
		Link l9 = new Link("l9");
		Link l10 = new Link("l10");
		Link l11 = new Link("l11");
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r1, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l3, r1, r4, EdgeType.UNDIRECTED);
		arch.addEdge(l4, r2, r5, EdgeType.UNDIRECTED);
		arch.addEdge(l5, r2, r6, EdgeType.UNDIRECTED);
		arch.addEdge(l6, r4, r7, EdgeType.UNDIRECTED);
		arch.addEdge(l7, r4, r8, EdgeType.UNDIRECTED);
		arch.addEdge(l8, r4, r9, EdgeType.UNDIRECTED);
		arch.addEdge(l9, r0, r10, EdgeType.UNDIRECTED);
		arch.addEdge(l10, r0, r11, EdgeType.UNDIRECTED);
		arch.addEdge(l11, r10, r11, EdgeType.UNDIRECTED);
		
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r3);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t2, r7);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t1, r8);
		Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t3, r5);
		Mapping<Task, Resource> m4 = new Mapping<Task, Resource>("m4", t4, r10);
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		mappings.add(m0);
		mappings.add(m1);
		mappings.add(m2);
		mappings.add(m3);
		mappings.add(m4);
		
		ProxySearch proxySearch = new ProxySearch(new SpecificationPreprocessorMulti());
		Specification result = new Specification(appl, arch, mappings);
		proxySearch.preprocessSpecification(result);
		return result;
	}
}
