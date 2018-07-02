package net.sf.opendse.encoding.routing.res;

import edu.uci.ics.jung.graph.util.EdgeType;
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
import net.sf.opendse.model.properties.ArchitectureElementPropertyService;
import net.sf.opendse.model.properties.ResourcePropertyService;
import net.sf.opendse.visualization.SpecificationViewer;

public class ProxyRoutingTestRes {

	private ProxyRoutingTestRes() {
	}

	public static void main(String[] args) {
		SpecificationViewer.view(makeSpec());
	}

	public static Specification makeSpec() {
		// application
		Application<Task, Dependency> appl = new Application<Task, Dependency>();
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		Task t2 = new Task("t2");
		Task t3 = new Task("t3");
		Task t4 = new Task("t4");
		Task t5 = new Task("t5");
		Communication c0 = new Communication("c0");
		Communication c1 = new Communication("c1");
		Communication c2 = new Communication("c2");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		Dependency d3 = new Dependency("d3");
		Dependency d4 = new Dependency("d4");
		Dependency d5 = new Dependency("d5");
		appl.addEdge(d0, t0, c0, EdgeType.DIRECTED);
		appl.addEdge(d1, c0, t1, EdgeType.DIRECTED);
		appl.addEdge(d2, t2, c1, EdgeType.DIRECTED);
		appl.addEdge(d3, c1, t3, EdgeType.DIRECTED);
		appl.addEdge(d4, t4, c2, EdgeType.DIRECTED);
		appl.addEdge(d5, c2, t5, EdgeType.DIRECTED);
		
		// architecture
		Architecture<Resource, Link> arch = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		ResourcePropertyService.setProxyId(r0, r1);
		ResourcePropertyService.setProxyId(r4, r1);
		ResourcePropertyService.setProxyId(r3, r2);
		arch.addEdge(l2, r1, r2, EdgeType.UNDIRECTED);
		arch.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		ArchitectureElementPropertyService.setOfferRoutingVariety(l0, false);
		arch.addEdge(l3, r2, r3, EdgeType.UNDIRECTED);
		ArchitectureElementPropertyService.setOfferRoutingVariety(l3, false);
		arch.addEdge(l1, r1, r4, EdgeType.UNDIRECTED);
		ArchitectureElementPropertyService.setOfferRoutingVariety(l1, false);
		ResourcePropertyService.setProxyDistance(r4, 1);
		ResourcePropertyService.setProxyDistance(r0, 1);
		ResourcePropertyService.setProxyDistance(r3, 1);
		
		// mappings
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r3);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", t2, r0);
		Mapping<Task, Resource> m3 = new Mapping<Task, Resource>("m3", t3, r4);
		Mapping<Task, Resource> m4 = new Mapping<Task, Resource>("m4", t4, r0);
		Mapping<Task, Resource> m5 = new Mapping<Task, Resource>("m5", t5, r0);
		mappings.add(m0);
		mappings.add(m1);
		mappings.add(m2);
		mappings.add(m3);
		mappings.add(m4);
		mappings.add(m5);
		
		return new Specification(appl, arch, mappings);
	}

}
