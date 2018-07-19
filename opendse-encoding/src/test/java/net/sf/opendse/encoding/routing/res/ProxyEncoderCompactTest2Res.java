package net.sf.opendse.encoding.routing.res;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.preprocessing.ProxySearch;
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

public class ProxyEncoderCompactTest2Res {

	private ProxyEncoderCompactTest2Res() {
	}
	
	public static void main(String[] args) {
		SpecificationViewer.view(getSpecification());
	}
	
	public static Specification getSpecification() {
		// application
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Communication c = new Communication("c");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		appl.addEdge(d0, t0, c, EdgeType.DIRECTED);
		appl.addEdge(d1, t1, c, EdgeType.DIRECTED);
		appl.addEdge(d2, c, t2, EdgeType.DIRECTED);
		
		// architecture
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Resource r5 = new Resource("r5");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		Link l5 = new Link("l5");
		arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		arch.addEdge(l1, r2, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l2, r1, r3, EdgeType.UNDIRECTED);
		arch.addEdge(l3, r1, r4, EdgeType.UNDIRECTED);
		arch.addEdge(l4, r3, r4, EdgeType.UNDIRECTED);
		arch.addEdge(l5, r4, r5, EdgeType.UNDIRECTED);
		
		// mappings
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r2);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t2, r5);
		mappings.add(m0);
		mappings.add(m1);
		mappings.add(m2);
		
		ProxySearch proxySearch = new ProxySearch();
		Specification result = new Specification(appl, arch, mappings);
		proxySearch.preprocessSpecification(result);
		return result;
	}
	
}
